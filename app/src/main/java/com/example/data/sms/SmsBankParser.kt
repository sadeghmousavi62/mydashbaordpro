package com.example.data.sms

import com.example.model.BankRegistry
import com.example.model.ParsedSmsResult
import com.example.model.TransactionType
import com.example.util.PersianUtils
import java.security.MessageDigest
import java.util.regex.Pattern

object SmsBankParser {

    // Regex for card or account numbers
    private val cardPatterns = listOf(
        // e.g. 6037***1234 or 6037-****-****-1234
        Pattern.compile("""(?:کارت|حساب|به|از|شماره)[\s:]*([0-9*xX\-]{4,19})""", Pattern.CASE_INSENSITIVE),
        Pattern.compile("""([0-9]{4}[*xX\-]{2,10}[0-9]{4})"""),
        Pattern.compile("""(?:کارت|حساب)[\s:]*(\d{4})"""),
        Pattern.compile("""(\d{4}[*xX]\d{4})""")
    )

    // Regex for transaction amounts
    private val amountPatterns = listOf(
        Pattern.compile("""(?:مبلغ|مبلغ تراکنش|واریز|برداشت|خرید|انتقال)[\s:]*([\d,،]+)"""),
        Pattern.compile("""([+\-][\d,،]+)\s*(?:ریال|تومان)"""),
        Pattern.compile("""([\d,،]+)\s*(?:ریال|تومان)\s*(?:واریز|برداشت|خرید)""")
    )

    // Regex for remaining balance
    private val balancePatterns = listOf(
        Pattern.compile("""(?:مانده|موجودی|موجودي|مانده حساب|مانده کارت|مانده کل|اعتبار)[\s:]*([\d,،]+)"""),
        Pattern.compile("""(?:موجودی|مانده)[\s:]*([\d,،]+)\s*(?:ریال|تومان)""")
    )

    fun parse(smsBody: String, sender: String? = null, timestamp: Long = System.currentTimeMillis()): ParsedSmsResult? {
        val cleanText = PersianUtils.normalizeDigits(smsBody).trim()
        if (cleanText.isEmpty()) return null

        // 1. Identify Bank
        val bank = BankRegistry.findBankByTextOrSender(cleanText, sender)

        // 2. Identify Transaction Type
        val type = determineTransactionType(cleanText)

        // 3. Extract Card / Account Number
        val cardNumber = extractCardNumber(cleanText)

        // 4. Extract Amount
        val rawAmount = extractAmount(cleanText) ?: return null

        // Detect currency
        val isToman = cleanText.contains("تومان") || bank.code == "BLUE"
        val amountToman = if (isToman) rawAmount else (rawAmount / 10)

        // 5. Extract Balance
        val rawBalance = extractBalance(cleanText)
        val balanceToman = rawBalance?.let {
            if (isToman) it else (it / 10)
        }

        // 6. Extract Description / Category
        val description = extractDescription(cleanText)

        return ParsedSmsResult(
            bankCode = bank.code,
            bankName = bank.persianName,
            cardNumber = cardNumber ?: formatFallbackCard(bank.code),
            type = type,
            amountToman = amountToman,
            balanceAfterToman = balanceToman,
            timestamp = timestamp,
            description = description,
            rawText = smsBody,
            sender = sender
        )
    }

    private fun determineTransactionType(text: String): TransactionType {
        return when {
            text.contains("واریز حقوق") -> TransactionType.DEPOSIT
            text.contains("واریز") || text.contains("انتقال به") || text.contains("+") || text.contains("دریافت") -> TransactionType.DEPOSIT
            text.contains("خرید") || text.contains("شاپرک") || text.contains("پوز") -> TransactionType.PURCHASE
            text.contains("قبض") || text.contains("خدمات") || text.contains("شارژ") -> TransactionType.BILL
            text.contains("انتقال از") || text.contains("کارت به کارت") || text.contains("پایا") || text.contains("ساتنا") -> TransactionType.TRANSFER_OUT
            text.contains("برداشت") || text.contains("-") -> TransactionType.WITHDRAWAL
            else -> TransactionType.UNKNOWN
        }
    }

    private fun extractCardNumber(text: String): String? {
        for (pattern in cardPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val match = matcher.group(1)?.replace("-", "")?.trim() ?: ""
                if (match.length >= 4) {
                    return match
                }
            }
        }
        return null
    }

    private fun extractAmount(text: String): Long? {
        for (pattern in amountPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val group = matcher.group(1) ?: matcher.group(0)
                val clean = group.replace(",", "")
                    .replace("،", "")
                    .replace("+", "")
                    .replace("-", "")
                    .trim()
                val parsed = clean.toLongOrNull()
                if (parsed != null && parsed > 0) {
                    return parsed
                }
            }
        }
        return null
    }

    private fun extractBalance(text: String): Long? {
        for (pattern in balancePatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val group = matcher.group(1) ?: ""
                val clean = group.replace(",", "")
                    .replace("،", "")
                    .trim()
                val parsed = clean.toLongOrNull()
                if (parsed != null && parsed >= 0) {
                    return parsed
                }
            }
        }
        return null
    }

    private fun extractDescription(text: String): String {
        return when {
            text.contains("خرید شاپرک") -> "خرید پایانه شاپرک"
            text.contains("خرید اینترنتی") -> "خرید اینترنتی"
            text.contains("واریز حقوق") -> "واریز حقوق ماهانه"
            text.contains("کارت به کارت") -> "کارت به کارت"
            text.contains("پایا") -> "انتقال وجه پایا"
            text.contains("ساتنا") -> "انتقال وجه ساتنا"
            text.contains("قبض") -> "پرداخت قبض خدماتی"
            text.contains("خرید") -> "خرید فروشگاهی"
            text.contains("واریز") -> "واریز به حساب"
            text.contains("برداشت") -> "برداشت وجه"
            else -> "تراکنش بانکی"
        }
    }

    private fun formatFallbackCard(bankCode: String): String {
        return when (bankCode) {
            "MELLI" -> "6037***0001"
            "MELLAT" -> "6104***0002"
            "PASARGAD" -> "5022***0003"
            "BLUE" -> "6219***0004"
            "SAMAN" -> "6219***0005"
            "TEJARAT" -> "5859***0006"
            "SEPAH" -> "5892***0007"
            else -> "حساب پیش‌فرض"
        }
    }

    fun computeHash(text: String, timestamp: Long): String {
        val input = "$text-$timestamp"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.take(16)
    }
}

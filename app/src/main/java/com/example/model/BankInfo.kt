package com.example.model

import androidx.compose.ui.graphics.Color

enum class TransactionType(val persianLabel: String, val isIncome: Boolean) {
    DEPOSIT("واریز", true),
    TRANSFER_IN("انتقال به حساب", true),
    WITHDRAWAL("برداشت", false),
    TRANSFER_OUT("انتقال از حساب", false),
    PURCHASE("خرید پایانه", false),
    BILL("پرداخت قبض", false),
    UNKNOWN("نامشخص", false)
}

data class BankInfo(
    val code: String,
    val persianName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val textBadgeColor: Color = Color.White,
    val smsKeywords: List<String> = emptyList(),
    val cardPrefixes: List<String> = emptyList()
)

object BankRegistry {
    val banks = listOf(
        BankInfo(
            code = "MELLI",
            persianName = "بانک ملی ایران",
            primaryColor = Color(0xFF00796B),
            secondaryColor = Color(0xFF004D40),
            smsKeywords = listOf("ملی", "melli", "BMI", "بانک ملی"),
            cardPrefixes = listOf("603799")
        ),
        BankInfo(
            code = "MELLAT",
            persianName = "بانک ملت",
            primaryColor = Color(0xFFC62828),
            secondaryColor = Color(0xFF8E0000),
            smsKeywords = listOf("ملت", "mellat", "بانک ملت"),
            cardPrefixes = listOf("610433", "991975")
        ),
        BankInfo(
            code = "PASARGAD",
            persianName = "بانک پاسارگاد",
            primaryColor = Color(0xFFD4AF37),
            secondaryColor = Color(0xFF262626),
            smsKeywords = listOf("پاسارگاد", "pasargad", "BPI"),
            cardPrefixes = listOf("502229", "639347")
        ),
        BankInfo(
            code = "BLUE",
            persianName = "بلوبانک",
            primaryColor = Color(0xFF0066FF),
            secondaryColor = Color(0xFF00BFFF),
            smsKeywords = listOf("بلوبانک", "بلو", "bluebank", "blubank"),
            cardPrefixes = listOf("62198610")
        ),
        BankInfo(
            code = "SAMAN",
            persianName = "بانک سامان",
            primaryColor = Color(0xFF009688),
            secondaryColor = Color(0xFF00695C),
            smsKeywords = listOf("سامان", "saman", "بانک سامان"),
            cardPrefixes = listOf("621986")
        ),
        BankInfo(
            code = "TEJARAT",
            persianName = "بانک تجارت",
            primaryColor = Color(0xFF1565C0),
            secondaryColor = Color(0xFF0D47A1),
            smsKeywords = listOf("تجارت", "tejarat", "بانک تجارت"),
            cardPrefixes = listOf("585983", "627353")
        ),
        BankInfo(
            code = "SEPAH",
            persianName = "بانک سپه",
            primaryColor = Color(0xFFB45309),
            secondaryColor = Color(0xFF1E293B),
            smsKeywords = listOf("سپه", "sepah", "بانک سپه", "انصار", "قوامین"),
            cardPrefixes = listOf("589210")
        ),
        BankInfo(
            code = "KESHAVARZI",
            persianName = "بانک کشاورزی",
            primaryColor = Color(0xFF2E7D32),
            secondaryColor = Color(0xFF1B5E20),
            smsKeywords = listOf("کشاورزی", "keshavarzi", "بانک کشاورزی"),
            cardPrefixes = listOf("603770", "639217")
        ),
        BankInfo(
            code = "SADERAT",
            persianName = "بانک صادرات ایران",
            primaryColor = Color(0xFF1976D2),
            secondaryColor = Color(0xFF0D47A1),
            smsKeywords = listOf("صادرات", "saderat", "بانک صادرات"),
            cardPrefixes = listOf("603769")
        ),
        BankInfo(
            code = "RESALAT",
            persianName = "بانک قرض‌الحسنه رسالت",
            primaryColor = Color(0xFF00838F),
            secondaryColor = Color(0xFF004D40),
            smsKeywords = listOf("رسالت", "resalat"),
            cardPrefixes = listOf("504172")
        ),
        BankInfo(
            code = "PARSIAN",
            persianName = "بانک پارسیان",
            primaryColor = Color(0xFF991B1B),
            secondaryColor = Color(0xFF450A0A),
            smsKeywords = listOf("پارسیان", "parsian"),
            cardPrefixes = listOf("622106")
        ),
        BankInfo(
            code = "AYANDEH",
            persianName = "بانک آینده",
            primaryColor = Color(0xFF701A75),
            secondaryColor = Color(0xFF4A044E),
            smsKeywords = listOf("آینده", "ayandeh"),
            cardPrefixes = listOf("636214")
        )
    )

    val defaultBank = BankInfo(
        code = "OTHER",
        persianName = "بانک ناشناس",
        primaryColor = Color(0xFF475569),
        secondaryColor = Color(0xFF1E293B),
        smsKeywords = emptyList(),
        cardPrefixes = emptyList()
    )

    fun findBankByTextOrSender(text: String, sender: String? = null): BankInfo {
        val lowerText = text.lowercase()
        val lowerSender = sender?.lowercase() ?: ""

        // Check exact match in sender
        for (bank in banks) {
            for (keyword in bank.smsKeywords) {
                if (lowerSender.contains(keyword.lowercase()) || lowerText.contains(keyword.lowercase())) {
                    return bank
                }
            }
        }
        return defaultBank
    }

    fun getBankByCode(code: String): BankInfo {
        return banks.find { it.code.equals(code, ignoreCase = true) } ?: defaultBank
    }
}

data class ParsedSmsResult(
    val bankCode: String,
    val bankName: String,
    val cardNumber: String?,
    val type: TransactionType,
    val amountToman: Long,
    val balanceAfterToman: Long?,
    val timestamp: Long,
    val description: String?,
    val rawText: String,
    val sender: String? = null
)

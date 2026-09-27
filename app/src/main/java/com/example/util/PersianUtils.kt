package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PersianUtils {

    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    /**
     * Converts Persian and Arabic numbers in string to English ASCII digits
     */
    fun normalizeDigits(input: String?): String {
        if (input == null) return ""
        val builder = StringBuilder()
        for (ch in input) {
            when {
                ch in '۰'..'۹' -> builder.append(ch - '۰')
                ch in '٠'..'٩' -> builder.append(ch - '٠')
                else -> builder.append(ch)
            }
        }
        return builder.toString()
    }

    /**
     * Converts English ASCII digits to Persian digits
     */
    fun toPersianDigits(input: String): String {
        val builder = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                builder.append(persianDigits[ch - '0'])
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun toPersianDigits(number: Long): String {
        return toPersianDigits(number.toString())
    }

    /**
     * Formats an amount with 3-digit comma separators
     */
    fun formatMoney(
        amount: Long,
        isToman: Boolean = true,
        usePersianDigits: Boolean = true
    ): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
        }
        val formatter = DecimalFormat("#,###", symbols)
        val formatted = formatter.format(amount)
        val currencySuffix = if (isToman) "تومان" else "ریال"

        val result = if (usePersianDigits) {
            toPersianDigits(formatted)
        } else {
            formatted
        }
        return "$result $currencySuffix"
    }

    /**
     * Format a simple Jalali/Persian date from timestamp
     */
    fun formatDateTime(timestamp: Long, usePersianDigits: Boolean = true): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.US)
        val formatted = sdf.format(date)
        return if (usePersianDigits) toPersianDigits(formatted) else formatted
    }

    fun formatRelativeTime(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "لحظاتی پیش"
            minutes < 60 -> "${toPersianDigits(minutes)} دقیقه پیش"
            hours < 24 -> "${toPersianDigits(hours)} ساعت پیش"
            days < 7 -> "${toPersianDigits(days)} روز پیش"
            else -> formatDateTime(timestamp)
        }
    }
}

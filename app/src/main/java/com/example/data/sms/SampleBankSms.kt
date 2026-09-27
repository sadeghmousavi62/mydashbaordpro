package com.example.data.sms

data class SampleSmsItem(
    val sender: String,
    val body: String,
    val timestampOffsetMinutes: Long
)

object SampleBankSms {
    private val now = System.currentTimeMillis()

    fun getSamples(): List<SampleSmsItem> {
        return listOf(
            SampleSmsItem(
                sender = "بانک ملی",
                body = """
                    بانک ملی ایران
                    واریز به کارت 6037***4512
                    مبلغ: 35,000,000 ریال
                    مانده: 142,500,000 ریال
                    واریز حقوق شهریور
                    1403/07/04-14:30
                """.trimIndent(),
                timestampOffsetMinutes = 60 * 24 * 2
            ),
            SampleSmsItem(
                sender = "بانک ملت",
                body = """
                    بانک ملت
                    برداشت خرید شاپرک
                    مبلغ: 8,450,000 ریال
                    از: 6104***8921
                    موجودی: 68,200,000 ریال
                    فروشگاه هایپرمی
                    1403/07/05-18:45
                """.trimIndent(),
                timestampOffsetMinutes = 60 * 18
            ),
            SampleSmsItem(
                sender = "BlueBank",
                body = """
                    بلوبانک
                    انتقال وجه با موفقیت انجام شد.
                    مبلغ: 2,500,000 تومان
                    مانده: 18,750,000 تومان
                    کارت: 6219***1024
                    به علی کریمی
                """.trimIndent(),
                timestampOffsetMinutes = 60 * 8
            ),
            SampleSmsItem(
                sender = "بانک پاسارگاد",
                body = """
                    بانک پاسارگاد
                    واریز سود سپرده
                    مبلغ: 4,800,000 ریال
                    حساب: 5022***3344
                    مانده: 54,300,000 ریال
                    1403/07/05-08:12
                """.trimIndent(),
                timestampOffsetMinutes = 60 * 4
            ),
            SampleSmsItem(
                sender = "بانک سامان",
                body = """
                    بانک سامان
                    خرید اینترنتی
                    مبلغ: 1,200,000 ریال
                    کارت: 6219***7711
                    مانده: 31,400,000 ریال
                    دیجی‌کالا
                """.trimIndent(),
                timestampOffsetMinutes = 60 * 2
            ),
            SampleSmsItem(
                sender = "بانک تجارت",
                body = """
                    بانک تجارت
                    برداشت نقدی خودپرداز
                    مبلغ: 2,000,000 ریال
                    از حساب: 5859***9900
                    مانده: 12,850,000 ریال
                """.trimIndent(),
                timestampOffsetMinutes = 45
            ),
            SampleSmsItem(
                sender = "بانک کشاورزی",
                body = """
                    بانک کشاورزی
                    واریز پایا
                    مبلغ: 15,000,000 ریال
                    کارت: 6037***6789
                    مانده: 25,600,000 ریال
                """.trimIndent(),
                timestampOffsetMinutes = 20
            ),
            SampleSmsItem(
                sender = "بانک سپه",
                body = """
                    بانک سپه
                    خرید پایانه فروش
                    مبلغ: 640,000 ریال
                    کارت: 5892***5432
                    موجودی: 9,450,000 ریال
                """.trimIndent(),
                timestampOffsetMinutes = 5
            )
        )
    }
}

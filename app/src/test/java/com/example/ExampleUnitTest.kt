package com.example

import com.example.data.sms.SmsBankParser
import com.example.model.TransactionType
import com.example.util.PersianUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testPersianUtilsNormalization() {
    val input = "مبلغ: ۱۲,۵۰۰,۰۰۰ ریال"
    val normalized = PersianUtils.normalizeDigits(input)
    assertTrue(normalized.contains("12,500,000"))
  }

  @Test
  fun testMelliSmsParser() {
    val sms = """
        بانک ملی ایران
        واریز به کارت 6037***4512
        مبلغ: 35,000,000 ریال
        مانده: 142,500,000 ریال
        1403/07/04-14:30
    """.trimIndent()

    val parsed = SmsBankParser.parse(sms, "بانک ملی")
    assertNotNull(parsed)
    assertEquals("MELLI", parsed?.bankCode)
    assertEquals(TransactionType.DEPOSIT, parsed?.type)
    assertEquals(3500000L, parsed?.amountToman) // 35,000,000 Rials / 10 = 3,500,000 Tomans
    assertEquals(14250000L, parsed?.balanceAfterToman) // 142,500,000 Rials / 10 = 14,250,000 Tomans
  }

  @Test
  fun testBlueBankSmsParser() {
    val sms = """
        بلوبانک
        خرید با موفقیت انجام شد.
        مبلغ: 450,000 تومان
        مانده: 3,210,000 تومان
        کارت: 8821
    """.trimIndent()

    val parsed = SmsBankParser.parse(sms, "BlueBank")
    assertNotNull(parsed)
    assertEquals("BLUE", parsed?.bankCode)
    assertEquals(450000L, parsed?.amountToman)
    assertEquals(3210000L, parsed?.balanceAfterToman)
  }
}

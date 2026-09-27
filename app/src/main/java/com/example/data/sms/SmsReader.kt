package com.example.data.sms

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import com.example.model.ParsedSmsResult

class SmsReader(private val context: Context) {

    companion object {
        private const val TAG = "SmsReader"
        private val SMS_INBOX_URI = Uri.parse("content://sms/inbox")
    }

    /**
     * Reads SMS from Android system inbox and parses bank messages
     */
    fun readInboxBankSms(limit: Int = 100): List<ParsedSmsResult> {
        val parsedList = mutableListOf<ParsedSmsResult>()

        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE
        )

        try {
            val cursor = context.contentResolver.query(
                SMS_INBOX_URI,
                projection,
                null,
                null,
                "${Telephony.Sms.DATE} DESC"
            )

            cursor?.use {
                val addressIdx = it.getColumnIndex(Telephony.Sms.ADDRESS)
                val bodyIdx = it.getColumnIndex(Telephony.Sms.BODY)
                val dateIdx = it.getColumnIndex(Telephony.Sms.DATE)

                var count = 0
                while (it.moveToNext() && count < limit) {
                    val address = if (addressIdx != -1) it.getString(addressIdx) else ""
                    val body = if (bodyIdx != -1) it.getString(bodyIdx) else ""
                    val date = if (dateIdx != -1) it.getLong(dateIdx) else System.currentTimeMillis()

                    if (body.isNotEmpty()) {
                        val parsed = SmsBankParser.parse(body, address, date)
                        if (parsed != null) {
                            parsedList.add(parsed)
                            count++
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SMS read permission not granted", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading SMS inbox", e)
        }

        return parsedList
    }
}

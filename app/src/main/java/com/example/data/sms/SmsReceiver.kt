package com.example.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.data.db.SadeghDatabase
import com.example.data.repository.BankRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SmsReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        try {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isNullOrEmpty()) return

            val sb = StringBuilder()
            var sender = ""
            var timestamp = System.currentTimeMillis()

            for (sms in messages) {
                sb.append(sms.displayMessageBody)
                sender = sms.displayOriginatingAddress ?: ""
                timestamp = sms.timestampMillis
            }

            val body = sb.toString()
            val parsed = SmsBankParser.parse(body, sender, timestamp) ?: return

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = SadeghDatabase.getInstance(context)
                    val repository = BankRepository(db.bankAccountDao(), db.transactionDao(), context)
                    repository.saveParsedSms(parsed)
                    Log.d(TAG, "Successfully processed bank SMS from ${parsed.bankName}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to save bank SMS", e)
                } finally {
                    pendingResult.finish()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in SmsReceiver", e)
        }
    }
}

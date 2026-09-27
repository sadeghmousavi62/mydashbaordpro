package com.example.data.repository

import android.content.Context
import com.example.data.dao.BankAccountDao
import com.example.data.dao.TransactionDao
import com.example.data.entity.BankAccountEntity
import com.example.data.entity.TransactionEntity
import com.example.data.sms.SampleBankSms
import com.example.data.sms.SmsBankParser
import com.example.data.sms.SmsReader
import com.example.model.ParsedSmsResult
import com.example.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BankRepository(
    private val accountDao: BankAccountDao,
    private val transactionDao: TransactionDao,
    private val context: Context
) {

    val accountsFlow: Flow<List<BankAccountEntity>> = accountDao.getAllAccountsFlow()
    val transactionsFlow: Flow<List<TransactionEntity>> = transactionDao.getAllTransactionsFlow()
    val recentTransactionsFlow: Flow<List<TransactionEntity>> = transactionDao.getRecentTransactionsFlow(15)
    val totalBalanceFlow: Flow<Long?> = accountDao.getTotalBalanceFlow()

    suspend fun saveParsedSms(parsed: ParsedSmsResult): Boolean = withContext(Dispatchers.IO) {
        val smsHash = SmsBankParser.computeHash(parsed.rawText, parsed.timestamp)
        if (transactionDao.hasSmsHash(smsHash)) {
            return@withContext false
        }

        // 1. Find or create account
        val card = parsed.cardNumber ?: "حساب پیش‌فرض"
        var account = accountDao.getAccountByCard(parsed.bankCode, card)

        val newBalance = parsed.balanceAfterToman ?: run {
            val current = account?.currentBalanceToman ?: 0L
            if (parsed.type.isIncome) {
                current + parsed.amountToman
            } else {
                (current - parsed.amountToman).coerceAtLeast(0L)
            }
        }

        val accountId = if (account == null) {
            val newAcc = BankAccountEntity(
                bankCode = parsed.bankCode,
                bankName = parsed.bankName,
                cardOrAccount = card,
                customTitle = "${parsed.bankName} - $card",
                currentBalanceToman = newBalance,
                lastTransactionType = parsed.type.name,
                lastUpdated = parsed.timestamp
            )
            accountDao.insertOrUpdate(newAcc)
        } else {
            // Update existing account balance
            accountDao.updateBalance(
                id = account.id,
                balance = newBalance,
                lastType = parsed.type.name,
                updated = parsed.timestamp
            )
            account.id
        }

        // 2. Insert transaction
        val tx = TransactionEntity(
            accountId = accountId,
            bankCode = parsed.bankCode,
            bankName = parsed.bankName,
            cardNumber = card,
            type = parsed.type.name,
            amountToman = parsed.amountToman,
            balanceAfterToman = parsed.balanceAfterToman,
            timestamp = parsed.timestamp,
            description = parsed.description,
            category = parsed.type.persianLabel,
            rawSmsBody = parsed.rawText,
            smsHash = smsHash
        )
        transactionDao.insert(tx)
        return@withContext true
    }

    suspend fun scanInboxSms(): Int = withContext(Dispatchers.IO) {
        val reader = SmsReader(context)
        val parsedList = reader.readInboxBankSms()
        var addedCount = 0
        for (parsed in parsedList) {
            if (saveParsedSms(parsed)) {
                addedCount++
            }
        }
        return@withContext addedCount
    }

    suspend fun loadSampleData(): Int = withContext(Dispatchers.IO) {
        val samples = SampleBankSms.getSamples()
        var count = 0
        val baseTime = System.currentTimeMillis()

        for (sample in samples) {
            val timestamp = baseTime - (sample.timestampOffsetMinutes * 60 * 1000)
            val parsed = SmsBankParser.parse(sample.body, sample.sender, timestamp)
            if (parsed != null) {
                if (saveParsedSms(parsed)) {
                    count++
                }
            }
        }
        return@withContext count
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        accountDao.clearAll()
        transactionDao.clearAll()
    }

    suspend fun deleteAccount(id: Long) = withContext(Dispatchers.IO) {
        accountDao.deleteById(id)
    }

    suspend fun deleteTransaction(id: Long) = withContext(Dispatchers.IO) {
        transactionDao.deleteById(id)
    }
}

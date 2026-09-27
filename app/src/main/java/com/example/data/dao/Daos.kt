package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.BankAccountEntity
import com.example.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {
    @Query("SELECT * FROM bank_accounts ORDER BY currentBalanceToman DESC")
    fun getAllAccountsFlow(): Flow<List<BankAccountEntity>>

    @Query("SELECT * FROM bank_accounts WHERE bankCode = :bankCode AND cardOrAccount = :cardOrAccount LIMIT 1")
    suspend fun getAccountByCard(bankCode: String, cardOrAccount: String): BankAccountEntity?

    @Query("SELECT * FROM bank_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: Long): BankAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(account: BankAccountEntity): Long

    @Query("UPDATE bank_accounts SET currentBalanceToman = :balance, lastTransactionType = :lastType, lastUpdated = :updated WHERE id = :id")
    suspend fun updateBalance(id: Long, balance: Long, lastType: String, updated: Long)

    @Query("SELECT SUM(currentBalanceToman) FROM bank_accounts")
    fun getTotalBalanceFlow(): Flow<Long?>

    @Query("DELETE FROM bank_accounts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM bank_accounts")
    suspend fun clearAll()
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactionsFlow(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY timestamp DESC")
    fun getTransactionsByAccountFlow(accountId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE bankCode = :bankCode ORDER BY timestamp DESC")
    fun getTransactionsByBankFlow(bankCode: String): Flow<List<TransactionEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM transactions WHERE smsHash = :smsHash)")
    suspend fun hasSmsHash(smsHash: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}

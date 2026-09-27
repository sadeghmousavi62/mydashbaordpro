package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bank_accounts",
    indices = [Index(value = ["bankCode", "cardOrAccount"], unique = true)]
)
data class BankAccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bankCode: String,
    val bankName: String,
    val cardOrAccount: String, // e.g., "6037***9012"
    val customTitle: String = "",
    val currentBalanceToman: Long = 0L,
    val lastTransactionType: String = "UNKNOWN",
    val lastUpdated: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["smsHash"], unique = true),
        Index(value = ["timestamp"]),
        Index(value = ["bankCode"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long? = null,
    val bankCode: String,
    val bankName: String,
    val cardNumber: String? = null,
    val type: String, // TransactionType.name
    val amountToman: Long,
    val balanceAfterToman: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String? = null,
    val category: String = "عمومی",
    val rawSmsBody: String = "",
    val smsHash: String = ""
)

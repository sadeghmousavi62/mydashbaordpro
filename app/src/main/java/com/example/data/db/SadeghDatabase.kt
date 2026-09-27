package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BankAccountDao
import com.example.data.dao.TransactionDao
import com.example.data.entity.BankAccountEntity
import com.example.data.entity.TransactionEntity

@Database(
    entities = [BankAccountEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SadeghDatabase : RoomDatabase() {

    abstract fun bankAccountDao(): BankAccountDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: SadeghDatabase? = null

        fun getInstance(context: Context): SadeghDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SadeghDatabase::class.java,
                    "sadegh_banking_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

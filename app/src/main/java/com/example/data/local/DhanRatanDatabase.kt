package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppNotificationEntity
import com.example.data.model.BidRecordEntity
import com.example.data.model.MarketConfigEntity
import com.example.data.model.StatementTransactionEntity
import com.example.data.model.SupportMessageEntity
import com.example.data.model.UserAccountEntity

@Database(
    entities = [
        UserAccountEntity::class,
        BidRecordEntity::class,
        StatementTransactionEntity::class,
        SupportMessageEntity::class,
        MarketConfigEntity::class,
        AppNotificationEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class DhanRatanDatabase : RoomDatabase() {
    abstract fun dhanRatanDao(): DhanRatanDao

    companion object {
        @Volatile
        private var INSTANCE: DhanRatanDatabase? = null

        fun getInstance(context: Context): DhanRatanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DhanRatanDatabase::class.java,
                    "dhanratan_games_offline.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppNotificationEntity
import com.example.data.model.BidRecordEntity
import com.example.data.model.MarketConfigEntity
import com.example.data.model.StatementTransactionEntity
import com.example.data.model.SupportMessageEntity
import com.example.data.model.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DhanRatanDao {

    // --- User Accounts ---
    @Query("SELECT * FROM users WHERE isActiveSession = 1 LIMIT 1")
    fun observeActiveUser(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT 1")
    fun observeLastUser(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun observeAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserAccountEntity)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("UPDATE users SET isActiveSession = 0")
    suspend fun clearActiveSessions()

    // --- Bids ---
    @Query("SELECT * FROM bids WHERE userPhone = :userPhone ORDER BY createdAtMillis DESC")
    fun observeBidsForUser(userPhone: String): Flow<List<BidRecordEntity>>

    @Query("SELECT * FROM bids")
    fun observeAllBids(): Flow<List<BidRecordEntity>>

    @Query("SELECT * FROM bids WHERE marketId = :marketId AND status = 'Active'")
    suspend fun getActiveBidsForMarket(marketId: String): List<BidRecordEntity>

    @Query("SELECT * FROM bids WHERE marketId = :marketId AND (status = 'Won' OR status = 'Lost')")
    suspend fun getSettledBidsForMarket(marketId: String): List<BidRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBids(bids: List<BidRecordEntity>)

    @Update
    suspend fun updateBid(bid: BidRecordEntity)

    // --- Statement Transactions (Payin & Payout Check) ---
    @Query("SELECT * FROM transactions WHERE userPhone = :userPhone ORDER BY createdAtMillis DESC")
    fun observeTransactionsForUser(userPhone: String): Flow<List<StatementTransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'deposit' ORDER BY createdAtMillis DESC")
    fun observeAllPayins(): Flow<List<StatementTransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'withdraw' ORDER BY createdAtMillis DESC")
    fun observeAllWithdrawals(): Flow<List<StatementTransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'deposit_check' ORDER BY createdAtMillis DESC")
    fun observeDepositChecks(): Flow<List<StatementTransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :txId LIMIT 1")
    suspend fun getTransactionById(txId: String): StatementTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: StatementTransactionEntity)

    @Update
    suspend fun updateTransaction(tx: StatementTransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :txId")
    suspend fun deleteTransactionById(txId: String)

    // --- Support Messages ---
    @Query("SELECT * FROM support_messages WHERE userPhone = :userPhone ORDER BY createdAtMillis ASC")
    fun observeSupportMessages(userPhone: String): Flow<List<SupportMessageEntity>>

    @Query("SELECT COUNT(*) FROM support_messages WHERE userPhone = :userPhone")
    suspend fun countSupportMessages(userPhone: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportMessages(messages: List<SupportMessageEntity>)

    // --- Admin Market Configs ---
    @Query("SELECT * FROM market_configs")
    fun observeMarketConfigs(): Flow<List<MarketConfigEntity>>

    @Query("SELECT * FROM market_configs WHERE marketId = :marketId LIMIT 1")
    suspend fun getMarketConfig(marketId: String): MarketConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMarketConfig(config: MarketConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMarketConfigs(configs: List<MarketConfigEntity>)

    // --- Notifications (Market Results, All-User Notices, Particular User Notices, Welcome Notice Board) ---
    @Query("SELECT * FROM notifications ORDER BY createdAtMillis DESC")
    fun observeNotifications(): Flow<List<AppNotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0 AND type != 'WELCOME_NOTICE_BOARD' AND type != 'WELCOME_NOTICE_ERASED'")
    fun observeUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()
}

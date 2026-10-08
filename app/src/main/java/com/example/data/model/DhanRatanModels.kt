package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserAccountEntity(
    @PrimaryKey val phone: String,
    val id: String,
    val fullName: String,
    val email: String,
    val password: String,
    val mpin: String,
    val upiId: String,
    val bankName: String,
    val accountNumber: String,
    val ifscCode: String,
    val balance: Int,
    val createdAt: String,
    val isActiveSession: Boolean = true,
    val isAccountActive: Boolean = true
)

@Entity(tableName = "bids")
data class BidRecordEntity(
    @PrimaryKey val id: String,
    val userPhone: String,
    val marketId: String,
    val marketName: String,
    val gameType: String,
    val session: String, // "OPEN" | "CLOSE" | "JODI" | "SANGAM"
    val digit: String,
    val amount: Int,
    val potentialPayout: Int,
    val status: String, // "Active" | "Won" | "Lost"
    val timestamp: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class StatementTransactionEntity(
    @PrimaryKey val id: String,
    val userPhone: String,
    val type: String, // "deposit" | "withdraw" | "bid" | "win" | "refund"
    val title: String,
    val subtitle: String,
    val amount: Int,
    val direction: String, // "credit" | "debit"
    val method: String,
    val referenceId: String,
    val status: String, // "Pending Admin Check" | "Accepted & Credited" | "Pending Approval" | "Accepted & Transferred" | "Rejected by Admin"
    val timestamp: String,
    val balanceAfter: Int,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_messages")
data class SupportMessageEntity(
    @PrimaryKey val id: String,
    val userPhone: String,
    val sender: String, // "user" | "support"
    val text: String,
    val timestamp: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "market_configs")
data class MarketConfigEntity(
    @PrimaryKey val marketId: String,
    val name: String = "",
    val category: String = "main", // "main" | "starline"
    val openTime: String = "",
    val closeTime: String = "",
    val isLive: Boolean = true,
    val status: String = "Betting is Running",
    val resultCode: String = "***-**-***",
    val isDeleted: Boolean = false,
    val isCustom: Boolean = false,
    val updatedAt: String = ""
)

@Entity(tableName = "notifications")
data class AppNotificationEntity(
    @PrimaryKey val id: String,
    val type: String, // "MARKET_RESULT" | "ADMIN_NOTICE" | "USER_NOTICE" | "WELCOME_NOTICE_BOARD" | "WELCOME_NOTICE_ERASED"
    val title: String,
    val message: String,
    val marketName: String = "", // For USER_NOTICE, stores target user's phone number
    val timestamp: String,
    val isRead: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class ChartEntry(
    val date: String,
    val day: String,
    val openPana: String,
    val jodi: String,
    val closePana: String
)

data class MarketItem(
    val id: String,
    val name: String,
    val category: String, // "main" | "starline"
    val resultCode: String,
    val status: String,
    val openTime: String,
    val closeTime: String,
    val isLive: Boolean,
    val multiplierLabel: String,
    val chartHistory: List<ChartEntry>
)

enum class GameVariety(val displayName: String) {
    SINGLE_ANK("Single Ank"),
    SINGLE_DIGIT_BULK("Single Digit Bulk"),
    JODI("Jodi"),
    JODI_DIGIT_BULK("Jodi Digit Bulk"),
    SINGLE_PATTI("Single Patti"),
    SINGLE_PANA_BULK("Single Pana Bulk"),
    DOUBLE_PATTI("Double Patti"),
    DOUBLE_PANA_BULK("Double Pana Bulk"),
    TRIPLE_PATTI("Triple Patti"),
    HALF_SANGAM_A("Half Sangam A"),
    HALF_SANGAM_B("Half Sangam B"),
    FULL_SANGAM("Full Sangam")
}

data class QueuedBidItem(
    val id: String,
    val digit: String,
    val points: Int,
    val gameType: String // "OPEN" | "CLOSE" | "JODI" | "SANGAM"
)

data class GameRateItem(
    val label: String,
    val ratio: String,
    val multiplier: Double
)

data class StarlineDateSlot(
    val time: String,
    val result: String
)

enum class ActiveTab {
    DASHBOARD,
    PROFILE,
    HISTORY,
    CHAT,
    SUPPORT,
    MORE
}

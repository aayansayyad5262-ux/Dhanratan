package com.example.data.repository

import com.example.data.local.DhanRatanDao
import com.example.data.local.InitialMarketData
import com.example.data.model.AppNotificationEntity
import com.example.data.model.BidRecordEntity
import com.example.data.model.MarketConfigEntity
import com.example.data.model.MarketItem
import com.example.data.model.StatementTransactionEntity
import com.example.data.model.SupportMessageEntity
import com.example.data.model.UserAccountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DhanRatanRepository(
    private val dao: DhanRatanDao
) {
    val activeUser: Flow<UserAccountEntity?> = dao.observeActiveUser()
    val lastKnownUser: Flow<UserAccountEntity?> = dao.observeAllUsers().map { users ->
        users.firstOrNull { !InitialMarketData.isSpecialAdminUser(it.phone) }
    }
    val allUsers: Flow<List<UserAccountEntity>> = dao.observeAllUsers().map { users ->
        users.filter { !InitialMarketData.isSpecialAdminUser(it.phone) }
    }
    val allBids: Flow<List<BidRecordEntity>> = dao.observeAllBids()
    val notifications: Flow<List<AppNotificationEntity>> = dao.observeNotifications()
    val unreadNotificationCount: Flow<Int> = dao.observeUnreadNotificationCount()
    val allPayins: Flow<List<StatementTransactionEntity>> = dao.observeAllPayins()
    val allWithdrawals: Flow<List<StatementTransactionEntity>> = dao.observeAllWithdrawals()
    val allDepositChecks: Flow<List<StatementTransactionEntity>> = dao.observeDepositChecks()

    val isApkGameActive: Flow<Boolean> = dao.observeMarketConfigs().map { configs ->
        configs.firstOrNull { it.marketId == "GLOBAL_APK_GAME_STATUS" }?.isLive ?: true
    }

    private val legacyRemovedIds = setOf(
        "milan-night",
        "old-main-mumbai",
        "kalyan-night",
        "ratan-khatri",
        "rajdhani-night",
        "main-mumbai-rk",
        "night-time-bazar",
        "kalyan-day"
    )

    private fun resolveMarketState(
        category: String,
        openTime: String,
        closeTime: String,
        resultCode: String,
        cfg: MarketConfigEntity?,
        nowMins: Int
    ): Pair<Boolean, String> {
        val isFullyDeclared = !resultCode.contains("*")
        if (cfg != null) {
            if (!cfg.isLive) {
                return false to "Market is OFF"
            }
            if (cfg.status.contains("Admin ON", ignoreCase = true)) {
                return true to "Betting is Running"
            }
        }
        if (isFullyDeclared) {
            return false to "Betting is Closed"
        }

        if (category == "starline") {
            val openMins = InitialMarketData.parseTimeMinutesOfDay(openTime)
            return if (openMins != null && nowMins >= openMins) {
                false to "Betting is Closed"
            } else {
                true to "Betting is Running"
            }
        } else {
            val openMins = InitialMarketData.parseTimeMinutesOfDay(openTime)
            val closeMins = InitialMarketData.parseTimeMinutesOfDay(closeTime)
            val openPana = resultCode.split("-").getOrNull(0).orEmpty()
            val openDeclared = openPana.length == 3 && !openPana.contains("*")

            return when {
                closeMins != null && nowMins >= closeMins -> false to "Betting is Closed"
                (openMins != null && nowMins >= openMins) || openDeclared -> true to "Close Betting Running"
                else -> true to "Betting is Running"
            }
        }
    }

    val markets: Flow<List<MarketItem>> = dao.observeMarketConfigs().map { configs ->
        val configById = configs.associateBy { it.marketId }
        val nowMins = InitialMarketData.currentMinutesOfDay()

        val baseList = InitialMarketData.INITIAL_MARKETS.mapNotNull { base ->
            val cfg = configById[base.id]
            if (cfg?.isDeleted == true) {
                return@mapNotNull null
            }
            val effName = cfg?.name?.ifEmpty { base.name } ?: base.name
            val effOpen = cfg?.openTime?.ifEmpty { base.openTime } ?: base.openTime
            val effClose = cfg?.closeTime?.ifEmpty { base.closeTime } ?: base.closeTime
            val effResult = cfg?.resultCode?.ifEmpty { base.resultCode } ?: base.resultCode
            val (liveState, statusText) = resolveMarketState(
                category = base.category,
                openTime = effOpen,
                closeTime = effClose,
                resultCode = effResult,
                cfg = cfg,
                nowMins = nowMins
            )

            base.copy(
                name = effName,
                openTime = effOpen,
                closeTime = effClose,
                isLive = liveState,
                status = statusText,
                resultCode = effResult
            )
        }

        val initialIds = InitialMarketData.INITIAL_MARKETS.map { it.id }.toSet()
        val customList = configs
            .filter {
                it.isCustom &&
                    !it.isDeleted &&
                    it.marketId !in initialIds &&
                    it.marketId !in legacyRemovedIds &&
                    it.marketId != "GLOBAL_APK_GAME_STATUS"
            }
            .map { cfg ->
                val (liveState, statusText) = resolveMarketState(
                    category = cfg.category,
                    openTime = cfg.openTime,
                    closeTime = cfg.closeTime,
                    resultCode = cfg.resultCode,
                    cfg = cfg,
                    nowMins = nowMins
                )
                MarketItem(
                    id = cfg.marketId,
                    name = cfg.name,
                    category = cfg.category,
                    resultCode = cfg.resultCode,
                    status = statusText,
                    openTime = cfg.openTime,
                    closeTime = cfg.closeTime,
                    isLive = liveState,
                    multiplierLabel = if (cfg.category == "starline") "10 KA 1600" else "",
                    chartHistory = emptyList()
                )
            }

        baseList + customList
    }

    fun observeBids(userPhone: String): Flow<List<BidRecordEntity>> =
        dao.observeBidsForUser(userPhone)

    fun observeTransactions(userPhone: String): Flow<List<StatementTransactionEntity>> =
        dao.observeTransactionsForUser(userPhone)

    fun observeSupportMessages(userPhone: String): Flow<List<SupportMessageEntity>> =
        dao.observeSupportMessages(userPhone)

    fun currentFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
        return sdf.format(Date())
    }

    fun currentTimeShort(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        return sdf.format(Date())
    }

    suspend fun loginUser(
        identifier: String,
        password: String
    ): Result<UserAccountEntity> {
        val cleanId = identifier.trim()
        val cleanPass = password.trim()
        if (cleanId.isEmpty() || cleanPass.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter your Username/Mobile No. and Password."))
        }

        val digitsOnly = cleanId.filter { it.isDigit() }
        val allUsersList = dao.observeAllUsers().firstOrNull().orEmpty()

        val matchedUser = allUsersList.firstOrNull { user ->
            (digitsOnly.length == 10 && user.phone == digitsOnly) ||
                user.email.equals(cleanId, ignoreCase = true) ||
                user.fullName.equals(cleanId, ignoreCase = true) ||
                user.id.equals(cleanId, ignoreCase = true)
        }

        if (matchedUser == null) {
            return Result.failure(
                IllegalArgumentException("Account not found for '$cleanId'. Tap 'Create an account' to Sign Up.")
            )
        }

        if (matchedUser.password != cleanPass) {
            return Result.failure(
                IllegalArgumentException("Incorrect password. Please try again.")
            )
        }

        dao.clearActiveSessions()
        val activated = matchedUser.copy(
            isActiveSession = true,
            isAccountActive = true
        )
        dao.upsertUser(activated)
        ensureSupportWelcome(activated.phone)
        return Result.success(activated)
    }

    suspend fun registerNewUser(
        fullName: String,
        username: String,
        phone: String,
        password: String
    ): Result<UserAccountEntity> {
        val cleanName = fullName.trim()
        val cleanUsername = username.trim()
        val cleanPhone = phone.filter { it.isDigit() }.take(10)
        val cleanPassword = password.trim()

        if (cleanName.length < 2) {
            return Result.failure(IllegalArgumentException("Please enter your full name."))
        }
        if (cleanUsername.length < 3) {
            return Result.failure(IllegalArgumentException("Username must be at least 3 characters."))
        }
        if (cleanPhone.length != 10) {
            return Result.failure(IllegalArgumentException("Please enter a valid 10-digit mobile number."))
        }
        if (cleanPassword.length < 4) {
            return Result.failure(IllegalArgumentException("Password must be at least 4 characters."))
        }

        val allUsersList = dao.observeAllUsers().firstOrNull().orEmpty()
        if (allUsersList.any { it.phone == cleanPhone }) {
            return Result.failure(
                IllegalArgumentException("Mobile number +91$cleanPhone is already registered. Please Sign In.")
            )
        }
        if (allUsersList.any { it.email.equals(cleanUsername, ignoreCase = true) }) {
            return Result.failure(
                IllegalArgumentException("Username '$cleanUsername' is already taken. Please choose another.")
            )
        }

        dao.clearActiveSessions()
        val newUser = UserAccountEntity(
            phone = cleanPhone,
            id = "USR-${cleanPhone.takeLast(4)}",
            fullName = cleanName,
            email = cleanUsername,
            password = cleanPassword,
            mpin = "0000",
            upiId = "",
            bankName = "",
            accountNumber = "",
            ifscCode = "",
            balance = 0,
            createdAt = currentFormattedTimestamp(),
            isActiveSession = true,
            isAccountActive = true
        )
        dao.upsertUser(newUser)
        ensureSupportWelcome(cleanPhone)
        return Result.success(newUser)
    }

    suspend fun signInWithGoogleAccount(
        fullName: String,
        username: String,
        phone: String
    ): Result<UserAccountEntity> {
        val cleanName = fullName.trim().ifEmpty { "Ratan Player" }
        val cleanUsername = username.trim().ifEmpty { "ratan_user" }
        val cleanPhone = phone.filter { it.isDigit() }.take(10)
        if (cleanPhone.length != 10) {
            return Result.failure(IllegalArgumentException("Please enter a valid 10-digit mobile number."))
        }

        val existing = dao.getUserByPhone(cleanPhone)
        dao.clearActiveSessions()
        val userToActivate = if (existing != null) {
            existing.copy(
                fullName = cleanName,
                email = cleanUsername,
                isActiveSession = true,
                isAccountActive = true
            )
        } else {
            UserAccountEntity(
                phone = cleanPhone,
                id = "USR-${cleanPhone.takeLast(4)}",
                fullName = cleanName,
                email = cleanUsername,
                password = cleanPhone.takeLast(6),
                mpin = "0000",
                upiId = "",
                bankName = "",
                accountNumber = "",
                ifscCode = "",
                balance = 0,
                createdAt = currentFormattedTimestamp(),
                isActiveSession = true,
                isAccountActive = true
            )
        }
        dao.upsertUser(userToActivate)
        ensureSupportWelcome(cleanPhone)
        return Result.success(userToActivate)
    }

    suspend fun changeUserPassword(
        userPhone: String,
        oldPassword: String,
        newPassword: String
    ): Result<Unit> {
        val user = dao.getUserByPhone(userPhone)
            ?: return Result.failure(IllegalArgumentException("User account not found."))
        if (user.password != oldPassword.trim()) {
            return Result.failure(IllegalArgumentException("Current password is incorrect."))
        }
        if (newPassword.trim().length < 4) {
            return Result.failure(IllegalArgumentException("New password must be at least 4 characters."))
        }
        dao.upsertUser(user.copy(password = newPassword.trim()))
        return Result.success(Unit)
    }

    suspend fun logoutCurrentUser() {
        dao.clearActiveSessions()
    }

    suspend fun updateUserProfile(updatedUser: UserAccountEntity) {
        dao.upsertUser(updatedUser.copy(isActiveSession = true, isAccountActive = true))
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    private suspend fun ensureSupportWelcome(phone: String) {
        val count = dao.countSupportMessages(phone)
        if (count == 0) {
            dao.insertSupportMessages(InitialMarketData.buildInitialSupportMessages(phone))
        }
    }

    suspend fun recordUserCheckedDeposit(
        user: UserAccountEntity,
        stepNote: String = "Clicked Deposit Option • Trying to Deposit",
        amountTrying: Int = 0
    ) {
        val now = System.currentTimeMillis()
        val tx = StatementTransactionEntity(
            id = "chk-${user.phone}-$now",
            userPhone = user.phone,
            type = "deposit_check",
            title = "${user.fullName} (+91${user.phone})",
            subtitle = stepNote,
            amount = amountTrying,
            direction = "info",
            method = "Checked Deposit Option",
            referenceId = "CHK-${(100000..999999).random()}",
            status = "Trying to Deposit",
            timestamp = currentFormattedTimestamp(),
            balanceAfter = user.balance,
            createdAtMillis = now
        )
        dao.insertTransaction(tx)
    }

    /**
     * Records a user Deposit (Payin) and credits the wallet balance.
     */
    suspend fun recordDeposit(
        user: UserAccountEntity,
        amount: Int,
        method: String,
        utr: String
    ) {
        val newBalance = user.balance + amount
        dao.upsertUser(user.copy(balance = newBalance))

        val tx = StatementTransactionEntity(
            id = "tx-${System.currentTimeMillis()}",
            userPhone = user.phone,
            type = "deposit",
            title = "Wallet Deposit Added",
            subtitle = "$method • UTR: $utr",
            amount = amount,
            direction = "credit",
            method = method,
            referenceId = utr,
            status = "Completed",
            timestamp = currentFormattedTimestamp(),
            balanceAfter = newBalance,
            createdAtMillis = System.currentTimeMillis()
        )
        dao.insertTransaction(tx)
    }

    suspend fun recordWithdrawal(
        user: UserAccountEntity,
        amount: Int,
        destination: String,
        refId: String
    ) {
        val newBalance = (user.balance - amount).coerceAtLeast(0)
        val updatedUser = user.copy(balance = newBalance)
        dao.upsertUser(updatedUser)

        val tx = StatementTransactionEntity(
            id = "tx-${System.currentTimeMillis()}",
            userPhone = user.phone,
            type = "withdraw",
            title = "Bank / UPI Withdrawal Request",
            subtitle = "To: $destination",
            amount = amount,
            direction = "debit",
            method = destination,
            referenceId = refId,
            status = "Pending Approval",
            timestamp = currentFormattedTimestamp(),
            balanceAfter = newBalance,
            createdAtMillis = System.currentTimeMillis()
        )
        dao.insertTransaction(tx)
    }

    suspend fun submitBidsBatch(
        user: UserAccountEntity,
        draftBids: List<BidRecordEntity>
    ) {
        if (draftBids.isEmpty()) return
        val totalPoints = draftBids.sumOf { it.amount }
        val newBalance = (user.balance - totalPoints).coerceAtLeast(0)
        val nowMillis = System.currentTimeMillis()
        val formattedTime = currentFormattedTimestamp()

        dao.upsertUser(user.copy(balance = newBalance))

        val finalizedBids = draftBids.mapIndexed { index, bid ->
            bid.copy(
                id = "bid-$nowMillis-$index",
                userPhone = user.phone,
                status = "Active",
                timestamp = formattedTime,
                createdAtMillis = nowMillis + index
            )
        }
        dao.insertBids(finalizedBids)

        val first = finalizedBids.first()
        val refId = "BID-${(1000000..9999999).random()}"
        val bidTx = StatementTransactionEntity(
            id = "tx-$nowMillis",
            userPhone = user.phone,
            type = "bid",
            title = "Bids Placed • ${first.marketName}",
            subtitle = "${finalizedBids.size} Bid(s) • ${first.gameType} (${first.session})",
            amount = totalPoints,
            direction = "debit",
            method = "DhanRatan Wallet",
            referenceId = refId,
            status = "Completed",
            timestamp = formattedTime,
            balanceAfter = newBalance,
            createdAtMillis = nowMillis
        )
        dao.insertTransaction(bidTx)
    }

    suspend fun sendSupportQuestion(userPhone: String, text: String) {
        val now = System.currentTimeMillis()
        val timeLabel = currentTimeShort()

        val userMsg = SupportMessageEntity(
            id = "msg-$now",
            userPhone = userPhone,
            sender = "user",
            text = text,
            timestamp = timeLabel,
            createdAtMillis = now
        )

        val lower = text.lowercase(Locale.US)
        val replyText = when {
            lower.contains("rule") || lower.contains("cheating") || lower.contains("unfair") ->
                "Official Rules & Regulations of DhanRatan Games:\n1. Cheating - Bets: If Admin Found Any Cheating, Hacking Or Phishing Activity, Admin Has All Rights To Take Necessary Action And Block The User Immediately.\n2. Unfair - Bets: Blocking Digits, Canning, Match Fix Bets Or Any Unfair Betting Is Strictly Not Allowed.\n3. Withdrawal Information: Before Requesting Withdrawal, Please Re-Check Your Bank Details Carefully. (Withdrawal Time: 10:00 AM to 09:00 PM • Limit: ₹500 to ₹1,00,000)"

            lower.contains("deposit") || lower.contains("utr") || lower.contains("upi") ->
                "Deposit Solution: Tap \"Deposit\" on the dashboard, enter an amount between ₹100 (Min) and ₹10,000 (Max), pay via Payment Gateway, and submit your 12-digit UTR. Admin will check the payment and accept your Payin request."

            lower.contains("withdraw") || lower.contains("bank") ->
                "Withdrawal Solution: Ensure your Bank Account (Bank Name, A/C No, IFSC) or UPI ID is saved in Profile -> Update Bank Details. Enter an amount between ₹500 and ₹1,00,000 and submit for Admin payout verification."

            lower.contains("bid") || lower.contains("bet") || lower.contains("closed") ->
                "Market & Bid Info: Select any active market, pick your game mode, enter your digits and points, and submit your bid. Winning bids are settled automatically when the official Open/Close market result is declared."

            lower.contains("starline") ->
                "DhanRatan Starline has 12 hourly draws from 10:15 AM to 09:15 PM (Bet Limit: ₹1 to ₹10,000). Available games: Single Ank, Single Digit Bulk, Single Patti, Single Pana Bulk, Double Patti, Double Pana Bulk, and Triple Patti."

            lower.contains("rate") || lower.contains("ratio") || lower.contains("multiplier") ->
                "Main Game Win Ratio: Single Digit (10 KA 95), Jodi Digit (10 KA 950), Single Panna (10 KA 1400), Double Panna (10 KA 2800), Triple Panna (10 KA 8000), Half Sangam (10 KA 10000), Full Sangam (10 KA 100000).\nDhanRatan Starline Win Ratio: Single Digit (10 KA 100), Single Panna (10 KA 1600), Double Panna (10 KA 3000), Triple Panna (10 KA 10000)."

            else ->
                "Welcome to DhanRatan Games Support. I can help you with Market Timings, Bid Rules, Deposits, Bank Withdrawals, Starline Results, or Game Win Ratios."
        }

        val botMsg = SupportMessageEntity(
            id = "msg-${now + 1}",
            userPhone = userPhone,
            sender = "support",
            text = replyText,
            timestamp = timeLabel,
            createdAtMillis = now + 1
        )

        dao.insertSupportMessages(listOf(userMsg, botMsg))
    }

    companion object {
        val DEFAULT_PLAYER_USER = UserAccountEntity(
            phone = "9876543210",
            id = "USR-3210",
            fullName = "DhanRatan Player",
            email = "",
            password = "",
            mpin = "",
            upiId = "",
            bankName = "",
            accountNumber = "",
            ifscCode = "",
            balance = 0,
            createdAt = "Official Member",
            isActiveSession = true,
            isAccountActive = true
        )
    }
}

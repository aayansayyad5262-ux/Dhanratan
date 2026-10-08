package com.example.ui

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.DhanRatanDatabase
import com.example.data.local.InitialMarketData
import com.example.data.model.ActiveTab
import com.example.data.model.AppNotificationEntity
import com.example.data.model.BidRecordEntity
import com.example.data.model.MarketItem
import com.example.data.model.StatementTransactionEntity
import com.example.data.model.SupportMessageEntity
import com.example.data.model.UserAccountEntity
import com.example.data.repository.DhanRatanRepository
import com.example.data.repository.FirestoreUserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class DhanRatanViewModel(
    private val repository: DhanRatanRepository,
    private val firestoreUserRepository: FirestoreUserRepository? = null
) : ViewModel() {

    private val _isStartingApk = MutableStateFlow(true)
    val isStartingApk: StateFlow<Boolean> = _isStartingApk.asStateFlow()

    private val _activeTab = MutableStateFlow(ActiveTab.DASHBOARD)
    val activeTab: StateFlow<ActiveTab> = _activeTab.asStateFlow()

    private val _marketFilter = MutableStateFlow("main") // "main" | "starline"
    val marketFilter: StateFlow<String> = _marketFilter.asStateFlow()

    val markets: StateFlow<List<MarketItem>> = repository.markets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialMarketData.INITIAL_MARKETS)

    private val _selectedPlayMarket = MutableStateFlow<MarketItem?>(null)
    val selectedPlayMarket: StateFlow<MarketItem?> = _selectedPlayMarket.asStateFlow()

    private val _isDepositOpen = MutableStateFlow(false)
    val isDepositOpen: StateFlow<Boolean> = _isDepositOpen.asStateFlow()

    private val _isWithdrawOpen = MutableStateFlow(false)
    val isWithdrawOpen: StateFlow<Boolean> = _isWithdrawOpen.asStateFlow()

    private val _isNotificationsOpen = MutableStateFlow(false)
    val isNotificationsOpen: StateFlow<Boolean> = _isNotificationsOpen.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    val currentUser: StateFlow<UserAccountEntity?> = repository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val lastKnownUser: StateFlow<UserAccountEntity?> = repository.lastKnownUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notifications: StateFlow<List<AppNotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = repository.unreadNotificationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val isApkGameActive: StateFlow<Boolean> = repository.isApkGameActive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val bids: StateFlow<List<BidRecordEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.observeBids(user.phone)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val statement: StateFlow<List<StatementTransactionEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.observeTransactions(user.phone)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportMessages: StateFlow<List<SupportMessageEntity>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.observeSupportMessages(user.phone)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            delay(850)
            _isStartingApk.value = false
        }
    }

    private suspend fun syncUserToFirestoreIfAuthenticated(user: UserAccountEntity) {
        val firebaseUser = try {
            FirebaseAuth.getInstance().currentUser
        } catch (_: Exception) {
            null
        }
        if (firebaseUser != null && firestoreUserRepository != null) {
            try {
                firestoreUserRepository.createOrSyncUserProfile(
                    fullName = user.fullName,
                    username = user.email.ifBlank { firebaseUser.email ?: user.phone },
                    phone = user.phone,
                    balance = user.balance.toLong()
                )
            } catch (_: Exception) {
                // Keep local session active even if network is temporarily unavailable
            }
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun triggerToast(message: String) {
        viewModelScope.launch {
            _toastMessage.value = message
            delay(3200)
            if (_toastMessage.value == message) {
                _toastMessage.value = null
            }
        }
    }

    fun handleLogin(identifier: String, password: String) {
        viewModelScope.launch {
            _authError.value = null
            val res = repository.loginUser(identifier, password)
            res.fold(
                onSuccess = { user ->
                    _activeTab.value = ActiveTab.DASHBOARD
                    triggerToast("Welcome back, ${user.fullName}!")
                },
                onFailure = { err ->
                    _authError.value = err.message ?: "Login failed."
                }
            )
        }
    }

    fun handleRegister(
        fullName: String,
        username: String,
        phone: String,
        password: String
    ) {
        viewModelScope.launch {
            _authError.value = null
            val res = repository.registerNewUser(
                fullName = fullName,
                username = username,
                phone = phone,
                password = password
            )
            res.fold(
                onSuccess = { user ->
                    _activeTab.value = ActiveTab.DASHBOARD
                    triggerToast("Account created! Welcome, ${user.fullName}!")
                },
                onFailure = { err ->
                    _authError.value = err.message ?: "Registration failed."
                }
            )
        }
    }

    fun handleGoogleSignIn(
        fullName: String,
        username: String,
        phone: String = "",
        googleUid: String = ""
    ) {
        viewModelScope.launch {
            _authError.value = null
            val res = repository.signInWithGoogleAccount(
                fullName = fullName,
                username = username,
                phone = phone
            )
            res.fold(
                onSuccess = { user ->
                    syncUserToFirestoreIfAuthenticated(user)
                    _activeTab.value = ActiveTab.DASHBOARD
                    triggerToast("Signed in with Google as ${user.fullName}!")
                },
                onFailure = { err ->
                    _authError.value = err.message ?: "Google Sign-In failed."
                }
            )
        }
    }

    fun handleChangePassword(
        oldPassword: String,
        newPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.changeUserPassword(user.phone, oldPassword, newPassword)
            res.fold(
                onSuccess = {
                    triggerToast("Password updated successfully!")
                    onResult(true, "Password updated successfully!")
                },
                onFailure = { err ->
                    onResult(false, err.message ?: "Failed to update password.")
                }
            )
        }
    }

    fun handleLogout(context: Context? = null) {
        viewModelScope.launch {
            try {
                FirebaseAuth.getInstance().signOut()
            } catch (_: Exception) {
            }
            if (context != null) {
                try {
                    CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
                } catch (_: Exception) {
                }
            }
            repository.logoutCurrentUser()
            _selectedPlayMarket.value = null
            _isDepositOpen.value = false
            _isWithdrawOpen.value = false
            _activeTab.value = ActiveTab.DASHBOARD
            triggerToast("Logged out successfully.")
        }
    }

    fun setActiveTab(tab: ActiveTab) {
        _activeTab.value = tab
    }

    fun setMarketFilter(filter: String) {
        _marketFilter.value = filter
    }

    fun toggleMarketFilterStarline() {
        _marketFilter.value = if (_marketFilter.value == "starline") "main" else "starline"
    }

    fun selectPlayMarket(market: MarketItem?) {
        if (market != null && !isApkGameActive.value) {
            triggerToast("Game Status is currently OFF.")
            return
        }
        if (market != null) {
            val openWindow = InitialMarketData.isOpenBidWindowOpen(market)
            val closeWindow = InitialMarketData.isCloseBidWindowOpen(market)
            if (!market.isLive || (!openWindow && !closeWindow)) {
                triggerToast("${market.name} is currently OFF / Bidding Closed.")
                return
            }
        }
        _selectedPlayMarket.value = market
    }

    fun setDepositOpen(open: Boolean) {
        _isDepositOpen.value = open
        if (open) {
            val user = currentUser.value ?: return
            viewModelScope.launch {
                repository.recordUserCheckedDeposit(
                    user = user,
                    stepNote = "Checked / Clicked Deposit Option • Trying to Deposit",
                    amountTrying = 0
                )
            }
        }
    }

    fun logDepositAttempt(amountTrying: Int, stepNote: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.recordUserCheckedDeposit(
                user = user,
                stepNote = stepNote,
                amountTrying = amountTrying
            )
        }
    }

    fun setWithdrawOpen(open: Boolean) {
        _isWithdrawOpen.value = open
    }

    fun setNotificationsOpen(open: Boolean) {
        _isNotificationsOpen.value = open
        if (open) {
            viewModelScope.launch {
                repository.markAllNotificationsRead()
            }
        }
    }

    fun updateUserProfile(updated: UserAccountEntity) {
        viewModelScope.launch {
            repository.updateUserProfile(updated)
            syncUserToFirestoreIfAuthenticated(updated)
            triggerToast("Profile details saved!")
        }
    }

    fun handleDepositSuccess(amount: Int, method: String, utr: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.recordDeposit(user, amount, method, utr)
            syncUserToFirestoreIfAuthenticated(user.copy(balance = user.balance + amount))
            triggerToast("₹${formatInr(amount)} added to your wallet!")
        }
    }

    fun handleWithdrawSuccess(amount: Int, destination: String, refId: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.recordWithdrawal(user, amount, destination, refId)
            syncUserToFirestoreIfAuthenticated(user.copy(balance = (user.balance - amount).coerceAtLeast(0)))
            triggerToast("Withdrawal request of ₹${formatInr(amount)} submitted!")
        }
    }

    fun handleBatchSubmitBids(draftBids: List<BidRecordEntity>) {
        val user = currentUser.value ?: return
        if (draftBids.isEmpty()) return
        val totalPoints = draftBids.sumOf { it.amount }
        val firstMarket = draftBids.first().marketName
        viewModelScope.launch {
            repository.submitBidsBatch(user, draftBids)
            syncUserToFirestoreIfAuthenticated(user.copy(balance = (user.balance - totalPoints).coerceAtLeast(0)))
            triggerToast("${draftBids.size} bid(s) placed on $firstMarket (₹${formatInr(totalPoints)})")
        }
    }

    fun handleSendSupportMessage(text: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.sendSupportQuestion(user.phone, text)
        }
    }

    companion object {
        fun formatInr(amount: Int): String {
            return try {
                NumberFormat.getNumberInstance(Locale("en", "IN")).format(amount)
            } catch (_: Exception) {
                amount.toString()
            }
        }

        fun provideFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val appContext = context.applicationContext
                    val db = DhanRatanDatabase.getInstance(appContext)
                    val repo = DhanRatanRepository(db.dhanRatanDao())
                    val firestoreRepo = try {
                        FirestoreUserRepository(appContext)
                    } catch (_: Exception) {
                        null
                    }
                    return DhanRatanViewModel(repo, firestoreRepo) as T
                }
            }
        }
    }
}

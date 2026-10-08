package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ActiveTab
import com.example.ui.DhanRatanViewModel
import com.example.ui.components.AuthScreen
import com.example.ui.components.DashboardScreen
import com.example.ui.components.DepositFullScreenModal
import com.example.ui.components.HistoryScreen
import com.example.ui.components.MarketPlayFlowScreen
import com.example.ui.components.MoreGameRatesScreen
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.ProfileScreen
import com.example.ui.components.SplashLoadingScreen
import com.example.ui.components.SupportChatScreen
import com.example.ui.components.SupportContactScreen
import com.example.ui.components.WithdrawFullScreenModal
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DeepDarkBg
import com.example.ui.theme.DhanRatanTheme
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SyneFontFamily

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DhanRatanTheme {
                DhanRatanApp()
            }
        }
    }
}

@Composable
fun DhanRatanApp(
    viewModel: DhanRatanViewModel = viewModel(
        factory = DhanRatanViewModel.provideFactory(LocalContext.current)
    )
) {
    val isStartingApk by viewModel.isStartingApk.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val lastKnownUser by viewModel.lastKnownUser.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val marketFilter by viewModel.marketFilter.collectAsStateWithLifecycle()
    val markets by viewModel.markets.collectAsStateWithLifecycle()
    val bids by viewModel.bids.collectAsStateWithLifecycle()
    val statement by viewModel.statement.collectAsStateWithLifecycle()
    val supportMessages by viewModel.supportMessages.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val isApkGameActive by viewModel.isApkGameActive.collectAsStateWithLifecycle()

    val selectedPlayMarket by viewModel.selectedPlayMarket.collectAsStateWithLifecycle()
    val isDepositOpen by viewModel.isDepositOpen.collectAsStateWithLifecycle()
    val isWithdrawOpen by viewModel.isWithdrawOpen.collectAsStateWithLifecycle()
    val isNotificationsOpen by viewModel.isNotificationsOpen.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    if (isStartingApk) {
        SplashLoadingScreen()
        return
    }

    val context = LocalContext.current
    val activeUser = user
    if (activeUser == null) {
        AuthScreen(
            initialIdentifier = lastKnownUser?.email?.ifEmpty { lastKnownUser?.phone.orEmpty() }.orEmpty(),
            errorMessage = authError,
            onClearError = { viewModel.clearAuthError() },
            onLogin = { identifier, password ->
                viewModel.handleLogin(identifier, password)
            },
            onRegister = { fullName, username, phone, password ->
                viewModel.handleRegister(fullName, username, phone, password)
            },
            onGoogleSignIn = { fullName, username, phone, googleUid ->
                viewModel.handleGoogleSignIn(fullName, username, phone, googleUid)
            }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepDarkBg),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isDepositOpen) {
            DepositFullScreenModal(
                isOpen = true,
                onClose = { viewModel.setDepositOpen(false) },
                user = activeUser,
                onLogDepositAttempt = { amount, note ->
                    viewModel.logDepositAttempt(amount, note)
                },
                onDepositSuccess = { amount, method, utr ->
                    viewModel.handleDepositSuccess(amount, method, utr)
                }
            )
        } else if (isWithdrawOpen) {
            WithdrawFullScreenModal(
                isOpen = true,
                onClose = { viewModel.setWithdrawOpen(false) },
                user = activeUser,
                onUpdateUserBank = { updatedUser ->
                    viewModel.updateUserProfile(updatedUser)
                },
                onWithdrawSuccess = { amount, dest, ref ->
                    viewModel.handleWithdrawSuccess(amount, dest, ref)
                }
            )
        } else if (selectedPlayMarket != null) {
            MarketPlayFlowScreen(
                market = selectedPlayMarket!!,
                userPhone = activeUser.phone,
                userBalance = activeUser.balance,
                onBackToMarkets = { viewModel.selectPlayMarket(null) },
                onSubmitBids = { submittedBids ->
                    viewModel.handleBatchSubmitBids(submittedBids)
                },
                onOpenDeposit = { viewModel.setDepositOpen(true) }
            )
        } else {
            Scaffold(
                containerColor = ObsidianBg,
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 560.dp),
                topBar = {
                    DhanRatanTopBar(
                        balance = activeUser.balance,
                        unreadNotificationCount = unreadNotificationCount,
                        onWordmarkClick = { viewModel.setActiveTab(ActiveTab.DASHBOARD) },
                        onBellClick = { viewModel.setNotificationsOpen(true) },
                        onWalletClick = { viewModel.setDepositOpen(true) }
                    )
                },
                bottomBar = {
                    DhanRatanBottomNavBar(
                        activeTab = activeTab,
                        onSelectTab = { viewModel.setActiveTab(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (activeTab) {
                        ActiveTab.DASHBOARD -> {
                            DashboardScreen(
                                markets = markets,
                                marketFilter = marketFilter,
                                currentUserPhone = activeUser.phone,
                                notifications = notifications,
                                isApkGameActive = isApkGameActive,
                                onSetMarketFilter = { viewModel.setMarketFilter(it) },
                                onToggleStarlineFilter = { viewModel.toggleMarketFilterStarline() },
                                onOpenDeposit = { viewModel.setDepositOpen(true) },
                                onOpenWithdraw = { viewModel.setWithdrawOpen(true) },
                                onOpenSupport = { viewModel.setActiveTab(ActiveTab.SUPPORT) },
                                onSelectPlayMarket = { viewModel.selectPlayMarket(it) }
                            )
                        }

                        ActiveTab.PROFILE -> {
                            ProfileScreen(
                                user = activeUser,
                                onUpdateUser = { viewModel.updateUserProfile(it) },
                                onChangePassword = { oldPass, newPass, onResult ->
                                    viewModel.handleChangePassword(oldPass, newPass, onResult)
                                },
                                onLogout = { viewModel.handleLogout(context) },
                                onBackToDashboard = { viewModel.setActiveTab(ActiveTab.DASHBOARD) }
                            )
                        }

                        ActiveTab.HISTORY -> {
                            HistoryScreen(
                                bids = bids,
                                statement = statement,
                                onGoToMarkets = { viewModel.setActiveTab(ActiveTab.DASHBOARD) },
                                onOpenDeposit = { viewModel.setDepositOpen(true) },
                                onOpenWithdraw = { viewModel.setWithdrawOpen(true) }
                            )
                        }

                        ActiveTab.SUPPORT -> {
                            SupportContactScreen(
                                onBackToDashboard = { viewModel.setActiveTab(ActiveTab.DASHBOARD) },
                                onOpenChatBot = { viewModel.setActiveTab(ActiveTab.CHAT) }
                            )
                        }

                        ActiveTab.CHAT -> {
                            SupportChatScreen(
                                messages = supportMessages,
                                onSendMessage = { viewModel.handleSendSupportMessage(it) }
                            )
                        }

                        ActiveTab.MORE -> {
                            MoreGameRatesScreen()
                        }
                    }
                }
            }
        }

        // Notifications Dialog
        NotificationsDialog(
            isOpen = isNotificationsOpen,
            currentUserPhone = activeUser.phone,
            notifications = notifications,
            onClose = { viewModel.setNotificationsOpen(false) }
        )

        // Floating Toast Banner
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 }),
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 64.dp, start = 20.dp, end = 20.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurface)
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = toastMessage.orEmpty(),
                    color = SlateTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DhanRatanTopBar(
    balance: Int,
    unreadNotificationCount: Int,
    onWordmarkClick: () -> Unit,
    onBellClick: () -> Unit,
    onWalletClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ObsidianBg)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = SlateTextPrimary)) { append("Dhan") }
                    withStyle(SpanStyle(color = GoldPrimary)) { append("Ratan ") }
                    withStyle(SpanStyle(color = EmeraldLight)) { append("Games") }
                },
                fontFamily = SyneFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp,
                modifier = Modifier
                    .clickable(onClick = onWordmarkClick)
                    .testTag("top_wordmark")
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Notification Bell
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
                        .clickable(onClick = onBellClick)
                        .testTag("top_notifications_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = SlateTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    if (unreadNotificationCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 7.dp, end = 7.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurface)
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable(onClick = onWalletClick)
                        .padding(horizontal = 12.dp)
                        .testTag("top_wallet_btn"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet Balance",
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "₹${DhanRatanViewModel.formatInr(balance)}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SlateTextPrimary
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(CardBorderSubtle)
        )
    }
}

private data class BottomNavItem(
    val tab: ActiveTab,
    val label: String,
    val icon: ImageVector
)

@Composable
private fun DhanRatanBottomNavBar(
    activeTab: ActiveTab,
    onSelectTab: (ActiveTab) -> Unit
) {
    val navItems = listOf(
        BottomNavItem(ActiveTab.DASHBOARD, "Markets", Icons.Default.SportsEsports),
        BottomNavItem(ActiveTab.PROFILE, "Profile", Icons.Default.Person),
        BottomNavItem(ActiveTab.HISTORY, "History", Icons.Default.AccessTime),
        BottomNavItem(ActiveTab.CHAT, "Chat", Icons.Default.ChatBubbleOutline),
        BottomNavItem(ActiveTab.MORE, "Game Rates", Icons.Default.EmojiEvents)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ObsidianBg)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(CardBorderSubtle)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val selected = activeTab == item.tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectTab(item.tab) }
                        .testTag("bottom_nav_${item.tab.name.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) GoldPrimary else SlateTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = item.label,
                        color = if (selected) GoldPrimary else SlateTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
    }
}

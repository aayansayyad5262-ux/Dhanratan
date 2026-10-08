package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BidRecordEntity
import com.example.data.model.StatementTransactionEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.DhanRatanViewModel
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.SlateDarkText
import com.example.ui.theme.SlateTextDim
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SyneFontFamily
import java.util.Locale

@Composable
fun ProfileScreen(
    user: UserAccountEntity,
    onUpdateUser: (UserAccountEntity) -> Unit,
    onChangePassword: (oldPass: String, newPass: String, onResult: (Boolean, String) -> Unit) -> Unit = { _, _, _ -> },
    onLogout: () -> Unit = {},
    onBackToDashboard: () -> Unit
) {
    var subPage by remember { mutableStateOf("main") } // "main" | "bank" | "password"

    var fullName by remember(user) { mutableStateOf(user.fullName) }
    var upiId by remember(user) { mutableStateOf(user.upiId) }
    var bankName by remember(user) { mutableStateOf(user.bankName) }
    var accountNumber by remember(user) { mutableStateOf(user.accountNumber) }
    var ifscCode by remember(user) { mutableStateOf(user.ifscCode) }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordFeedback by remember { mutableStateOf<String?>(null) }

    BackHandler {
        if (subPage != "main") {
            subPage = "main"
            passwordFeedback = null
        } else {
            onBackToDashboard()
        }
    }

    val firstWord = user.fullName
        .trim()
        .split(Regex("\\s+"))
        .firstOrNull()
        .orEmpty()
        .lowercase(Locale.US)
        .filter { it.isLetterOrDigit() }
        .ifEmpty { "member" }
    val phoneSuffix = user.phone.takeLast(4).ifEmpty { "0000" }
    val usernameHandle = user.email.trim().ifEmpty { "$firstWord$phoneSuffix" }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = ObsidianBg,
        unfocusedContainerColor = ObsidianBg,
        focusedBorderColor = GoldPrimary,
        unfocusedBorderColor = CardBorderSubtle,
        focusedTextColor = SlateTextPrimary,
        unfocusedTextColor = SlateTextPrimary
    )

    when (subPage) {
        "bank" -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SubPageHeader(
                    title = "Update Bank Details",
                    onBack = { subPage = "main" }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ProfileFormField("Account Holder Name", fullName, { fullName = it }, fieldColors)
                    ProfileFormField("Bank Name", bankName, { bankName = it }, fieldColors)
                    ProfileFormField("Account Number", accountNumber, { accountNumber = it.filter { c -> c.isDigit() }.take(18) }, fieldColors, isNumeric = true)
                    ProfileFormField("IFSC Code", ifscCode, { ifscCode = it.uppercase(Locale.US).take(11) }, fieldColors)
                    ProfileFormField("Registered UPI ID", upiId, { upiId = it }, fieldColors)

                    Button(
                        onClick = {
                            onUpdateUser(
                                user.copy(
                                    fullName = fullName.trim().ifEmpty { user.fullName },
                                    bankName = bankName.trim(),
                                    accountNumber = accountNumber.trim(),
                                    ifscCode = ifscCode.trim().uppercase(Locale.US),
                                    upiId = upiId.trim()
                                )
                            )
                            subPage = "main"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = SlateDarkText
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Save Bank Details", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        "password" -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SubPageHeader(
                    title = "Change Password",
                    onBack = {
                        subPage = "main"
                        passwordFeedback = null
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (passwordFeedback != null) {
                        Text(
                            text = passwordFeedback!!,
                            color = RoseLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    ProfileFormField("Current Password", oldPassword, { oldPassword = it; passwordFeedback = null }, fieldColors, isPassword = true)
                    ProfileFormField("New Password", newPassword, { newPassword = it; passwordFeedback = null }, fieldColors, isPassword = true)
                    ProfileFormField("Confirm New Password", confirmPassword, { confirmPassword = it; passwordFeedback = null }, fieldColors, isPassword = true)

                    Button(
                        onClick = {
                            when {
                                oldPassword.isBlank() -> passwordFeedback = "Please enter your current password."
                                newPassword.length < 4 -> passwordFeedback = "New password must be at least 4 characters."
                                newPassword != confirmPassword -> passwordFeedback = "New passwords do not match."
                                else -> {
                                    onChangePassword(oldPassword, newPassword) { success, msg ->
                                        if (success) {
                                            oldPassword = ""
                                            newPassword = ""
                                            confirmPassword = ""
                                            passwordFeedback = null
                                            subPage = "main"
                                        } else {
                                            passwordFeedback = msg
                                        }
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = SlateDarkText
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Update Password", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SubPageHeader(
                    title = "Profile Details",
                    onBack = onBackToDashboard
                )

                // Centered Logo Box + Username + Name + Phone
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(CardSurface)
                            .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        DhanRatanLogo(sizeDp = 70.dp, showText = false, useGeneratedIcon = true)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = usernameHandle,
                        fontFamily = SyneFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = user.fullName,
                        color = SlateTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "+91${user.phone}",
                        fontFamily = JetBrainsMonoFontFamily,
                        color = GoldPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // UPDATE BANK DETAILS
                ActionMenuCard(
                    title = "UPDATE BANK DETAILS",
                    subtitle = "Configure Name, Bank A/C, IFSC & UPI for withdrawals",
                    icon = Icons.Default.AccountBalance,
                    onClick = { subPage = "bank" },
                    testTag = "profile_update_bank_btn"
                )

                // CHANGE PASSWORD
                ActionMenuCard(
                    title = "CHANGE PASSWORD",
                    subtitle = "Update your account login password",
                    icon = Icons.Default.Key,
                    onClick = { subPage = "password" },
                    testTag = "profile_change_password_btn"
                )

                // LOGOUT
                ActionMenuCard(
                    title = "LOGOUT",
                    subtitle = "Sign out and return to the login screen",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    onClick = onLogout,
                    isDestructive = true,
                    testTag = "profile_logout_btn"
                )
            }
        }
    }
}

@Composable
private fun ProfileFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colors: androidx.compose.material3.TextFieldColors,
    isPassword: Boolean = false,
    isNumeric: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = SlateTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isNumeric) KeyboardType.Number else KeyboardType.Text
            ),
            shape = RoundedCornerShape(14.dp),
            colors = colors,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun HistoryScreen(
    bids: List<BidRecordEntity>,
    statement: List<StatementTransactionEntity>,
    onGoToMarkets: () -> Unit,
    onOpenDeposit: () -> Unit,
    onOpenWithdraw: () -> Unit
) {
    var section by remember { mutableStateOf("menu") } // "menu" | "market-bids" | "starline-bids" | "fund-history"
    var bidStatusFilter by remember { mutableStateOf("all") } // "all" | "Active" | "Won" | "Lost"
    var fundTypeFilter by remember { mutableStateOf("all") } // "all" | "deposit" | "withdraw"

    BackHandler(enabled = section != "menu") {
        section = "menu"
    }

    fun isStarlineBid(b: BidRecordEntity): Boolean {
        return b.marketId.startsWith("dhanratan-starline") ||
            b.marketName.lowercase(Locale.US).contains("starline")
    }

    when (section) {
        "market-bids", "starline-bids" -> {
            val isStarlineView = section == "starline-bids"
            val filteredBids = bids
                .filter { if (isStarlineView) isStarlineBid(it) else !isStarlineBid(it) }
                .filter { bidStatusFilter == "all" || it.status == bidStatusFilter }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SubPageHeader(
                            title = if (isStarlineView) "Starline Bid History" else "Market Bid History",
                            onBack = { section = "menu" }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardSurface)
                                .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "all" to "All",
                                "Active" to "Pending",
                                "Won" to "Won",
                                "Lost" to "Lost"
                            ).forEach { (id, lbl) ->
                                val selected = bidStatusFilter == id
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) GoldPrimary else Color.Transparent)
                                        .clickable { bidStatusFilter = id }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lbl,
                                        color = if (selected) SlateDarkText else SlateTextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (filteredBids.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(CardSurface)
                                .border(1.dp, CardBorderSubtle, RoundedCornerShape(24.dp))
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "No ${if (isStarlineView) "DhanRatan Starline" else "Main Market"} bids found.",
                                color = SlateTextMuted,
                                fontSize = 13.sp
                            )
                            Button(
                                onClick = onGoToMarkets,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = SlateDarkText
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Go to Markets", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(filteredBids, key = { it.id }) { bid ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(CardSurface)
                                .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(18.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = bid.marketName,
                                    fontFamily = SyneFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = SlateTextPrimary
                                )
                                Text(
                                    text = "${bid.session} • ${bid.gameType}",
                                    color = SlateTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Text(
                                text = "Digit: #${bid.digit} • Points: ₹${DhanRatanViewModel.formatInr(bid.amount)} • ${bid.timestamp}",
                                fontFamily = JetBrainsMonoFontFamily,
                                color = SlateTextSecondary,
                                fontSize = 11.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = when (bid.status) {
                                            "Won" -> "Winning Payout Credited"
                                            "Lost" -> "Payout"
                                            else -> "Potential Win"
                                        },
                                        color = SlateTextMuted,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = if (bid.status == "Lost") "₹0" else "₹${DhanRatanViewModel.formatInr(bid.potentialPayout)}",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        color = when (bid.status) {
                                            "Won" -> EmeraldLight
                                            "Lost" -> RoseLight
                                            else -> GoldPrimary
                                        },
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                }

                                val badgeColor = when (bid.status) {
                                    "Won" -> EmeraldLight
                                    "Lost" -> RoseLight
                                    else -> GoldPrimary
                                }
                                val badgeLabel = when (bid.status) {
                                    "Won" -> "Won"
                                    "Lost" -> "Better Luck Next Time"
                                    else -> "Awaiting Result"
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(badgeColor.copy(alpha = 0.14f))
                                        .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = badgeLabel,
                                        color = badgeColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        "fund-history" -> {
            val fundTransactions = statement
                .filter { it.type == "deposit" || it.type == "withdraw" }
                .filter { fundTypeFilter == "all" || it.type == fundTypeFilter }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SubPageHeader(
                            title = "Fund History",
                            onBack = { section = "menu" }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmeraldPrimary)
                                    .clickable { onOpenDeposit() }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("+ Deposit", color = SlateDarkText, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GoldPrimary)
                                    .clickable { onOpenWithdraw() }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("Withdraw", color = SlateDarkText, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorderSubtle, RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "all" to "All Funds",
                            "deposit" to "Deposits",
                            "withdraw" to "Withdrawals"
                        ).forEach { (id, lbl) ->
                            val selected = fundTypeFilter == id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) GoldPrimary else Color.Transparent)
                                    .clickable { fundTypeFilter = id }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lbl,
                                    color = if (selected) SlateDarkText else SlateTextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                if (fundTransactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(CardSurface)
                                .border(1.dp, CardBorderSubtle, RoundedCornerShape(22.dp))
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No deposit or withdrawal records found.",
                                color = SlateTextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(fundTransactions, key = { it.id }) { tx ->
                        val isCredit = tx.direction == "credit"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(CardSurface)
                                .border(1.dp, CardBorderSubtle, RoundedCornerShape(18.dp))
                                .padding(15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isCredit) EmeraldPrimary.copy(alpha = 0.16f)
                                            else GoldPrimary.copy(alpha = 0.16f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isCredit) Icons.Default.SouthWest else Icons.Default.NorthEast,
                                        contentDescription = null,
                                        tint = if (isCredit) EmeraldLight else GoldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.title,
                                        color = SlateTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${tx.subtitle} • Ref: ${tx.referenceId}",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        color = SlateTextMuted,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = tx.timestamp,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        color = SlateTextDim,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (isCredit) "+" else "-"}₹${DhanRatanViewModel.formatInr(tx.amount)}",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    color = if (isCredit) EmeraldLight else GoldPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = tx.status,
                                    color = when {
                                        tx.status.contains("Accepted") || tx.status.contains("Approved") || tx.status == "Completed" -> EmeraldLight
                                        tx.status.contains("Rejected") -> RoseLight
                                        else -> GoldPrimary
                                    },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SubPageHeader(
                    title = "History Dashboard",
                    onBack = onGoToMarkets
                )

                // 1. MARKET BID HISTORY
                ActionMenuCard(
                    title = "MARKET BID HISTORY",
                    subtitle = "You can view your market bid history",
                    icon = Icons.Default.CalendarToday,
                    onClick = {
                        bidStatusFilter = "all"
                        section = "market-bids"
                    },
                    testTag = "history_market_bids_btn"
                )

                // 2. DHANRATAN STARLINE BID HISTORY
                ActionMenuCard(
                    title = "DHANRATAN STARLINE BID HISTORY",
                    subtitle = "You can view your starline bid history",
                    icon = Icons.Default.SportsEsports,
                    onClick = {
                        bidStatusFilter = "all"
                        section = "starline-bids"
                    },
                    testTag = "history_starline_bids_btn"
                )

                // 3. FUND HISTORY
                ActionMenuCard(
                    title = "FUND HISTORY",
                    subtitle = "You can view your Deposit and Withdrawal history",
                    icon = Icons.Default.AccountBalanceWallet,
                    onClick = {
                        fundTypeFilter = "all"
                        section = "fund-history"
                    },
                    testTag = "history_fund_btn"
                )
            }
        }
    }
}

@Composable
fun SubPageHeader(
    title: String,
    onBack: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = SlateDarkText,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = title,
            fontFamily = SyneFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = SlateTextPrimary
        )
    }
}

@Composable
fun ActionMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    testTag: String
) {
    val accentGradient = if (isDestructive) {
        Brush.linearGradient(listOf(RosePrimary, RoseLight))
    } else {
        Brush.linearGradient(listOf(GoldPrimary, GoldSecondary))
    }
    val accentTint = if (isDestructive) RoseLight else GoldPrimary
    val borderColor = if (isDestructive) RosePrimary.copy(alpha = 0.35f) else Color(0x1FFFFFFF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(accentGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isDestructive) Color.White else SlateDarkText,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = SyneFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = if (isDestructive) RoseLight else SlateTextPrimary
                )
                Text(
                    text = subtitle,
                    color = SlateTextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(accentTint.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = accentTint,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

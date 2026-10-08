package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.InitialMarketData
import com.example.data.model.AppNotificationEntity
import com.example.data.model.MarketItem
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RoseDark
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SlateDarkText
import com.example.ui.theme.SlateTextDim
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SyneFontFamily

@Composable
fun DashboardScreen(
    markets: List<MarketItem>,
    marketFilter: String,
    currentUserPhone: String,
    notifications: List<AppNotificationEntity>,
    isApkGameActive: Boolean = true,
    onSetMarketFilter: (String) -> Unit,
    onToggleStarlineFilter: () -> Unit,
    onOpenDeposit: () -> Unit,
    onOpenWithdraw: () -> Unit,
    onOpenSupport: () -> Unit,
    onSelectPlayMarket: (MarketItem) -> Unit
) {
    val filteredMarkets = markets.filter { it.category == marketFilter }
    val welcomeNoticeRecord = notifications.firstOrNull {
        it.id == "permanent-welcome-notice" || it.type == "WELCOME_NOTICE_BOARD" || it.type == "WELCOME_NOTICE_ERASED"
    }
    val isWelcomeErased = welcomeNoticeRecord?.type == "WELCOME_NOTICE_ERASED"
    val welcomeTitle = welcomeNoticeRecord?.title?.ifBlank { InitialMarketData.DEFAULT_WELCOME_NOTICE_TITLE }
        ?: InitialMarketData.DEFAULT_WELCOME_NOTICE_TITLE
    val welcomeBody = welcomeNoticeRecord?.message?.ifBlank { InitialMarketData.DEFAULT_WELCOME_NOTICE_BODY }
        ?: InitialMarketData.DEFAULT_WELCOME_NOTICE_BODY

    val activeAllUserNotices = notifications.filter { it.type == "ADMIN_NOTICE" }
    val activeUserNotices = notifications.filter {
        it.type == "USER_NOTICE" && (it.marketName == currentUserPhone || it.marketName.isBlank())
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!isApkGameActive) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(RosePrimary.copy(alpha = 0.22f))
                        .border(1.dp, RosePrimary, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "APK GAME STATUS: OFF",
                            fontFamily = SyneFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Bidding is temporarily paused by Admin. Markets will resume once Game Status is set to Active.",
                            color = SlateTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Permanent APK Welcome Notice Board (Visible until Admin erases it)
        if (!isWelcomeErased) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    GoldPrimary.copy(alpha = 0.16f),
                                    CardSurface
                                )
                            )
                        )
                        .border(1.dp, GoldPrimary.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = welcomeTitle,
                            fontFamily = SyneFontFamily,
                            color = GoldPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = welcomeBody,
                            color = SlateTextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }
        }

        // All-User Admin Notices (Kept in APK until Admin erases them)
        items(activeAllUserNotices, key = { "dash_all_${it.id}" }) { notice ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldPrimary.copy(alpha = 0.12f))
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ALL USER NOTICE • ${notice.title}",
                        color = EmeraldLight,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = notice.message,
                        color = SlateTextPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Particular User Notices for this User (Kept in APK until Admin erases them)
        items(activeUserNotices, key = { "dash_usr_${it.id}" }) { notice ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SkyPrimary.copy(alpha = 0.12f))
                    .border(1.dp, SkyPrimary.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = SkyPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PERSONAL ADMIN NOTICE • ${notice.title}",
                        color = SkyPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = notice.message,
                        color = SlateTextPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Top Quick Action Deck (DhanRatan Starline • Deposit • Withdraw • Support)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardSurface)
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(24.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionTile(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_starline"),
                        label = "DhanRatan\nStarline",
                        icon = Icons.Default.SportsEsports,
                        iconBg = PurplePrimary.copy(alpha = 0.22f),
                        iconTint = PurpleLight,
                        borderColor = if (marketFilter == "starline") PurplePrimary.copy(alpha = 0.6f) else CardBorderSubtle,
                        containerBrush = Brush.verticalGradient(
                            if (marketFilter == "starline") {
                                listOf(PurplePrimary.copy(alpha = 0.22f), ObsidianBg)
                            } else {
                                listOf(ObsidianBg, ObsidianBg)
                            }
                        ),
                        labelColor = SlateTextPrimary,
                        onClick = onToggleStarlineFilter
                    )

                    QuickActionTile(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_deposit"),
                        label = "Deposit",
                        icon = Icons.Default.CurrencyRupee,
                        iconBg = EmeraldPrimary,
                        iconTint = SlateDarkText,
                        borderColor = EmeraldPrimary.copy(alpha = 0.4f),
                        containerBrush = Brush.verticalGradient(
                            listOf(EmeraldPrimary.copy(alpha = 0.22f), ObsidianBg)
                        ),
                        labelColor = EmeraldLight,
                        onClick = onOpenDeposit
                    )

                    QuickActionTile(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_withdraw"),
                        label = "Withdraw",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        iconBg = GoldPrimary,
                        iconTint = SlateDarkText,
                        borderColor = GoldPrimary.copy(alpha = 0.4f),
                        containerBrush = Brush.verticalGradient(
                            listOf(GoldPrimary.copy(alpha = 0.22f), ObsidianBg)
                        ),
                        labelColor = GoldPrimary,
                        onClick = onOpenWithdraw
                    )

                    QuickActionTile(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_support"),
                        label = "Support",
                        icon = Icons.Default.HeadsetMic,
                        iconBg = SkyPrimary.copy(alpha = 0.22f),
                        iconTint = SkyPrimary,
                        borderColor = CardBorderSubtle,
                        containerBrush = Brush.verticalGradient(
                            listOf(ObsidianBg, ObsidianBg)
                        ),
                        labelColor = SlateTextPrimary,
                        onClick = onOpenSupport
                    )
                }
            }
        }

        // Strict 2-Option Market Switcher: Main Markets & DhanRatan Starline Only
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurface)
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "main" to "Main Markets",
                    "starline" to "DhanRatan Starline"
                ).forEach { (filterKey, title) ->
                    val selected = marketFilter == filterKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) GoldPrimary else Color.Transparent)
                            .clickable { onSetMarketFilter(filterKey) }
                            .testTag("market_switcher_$filterKey"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (selected) SlateDarkText else SlateTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        if (marketFilter == "starline") {
            item {
                Text(
                    text = "DhanRatan Starline Games",
                    fontFamily = SyneFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = SlateTextPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            items(filteredMarkets, key = { it.id }) { market ->
                StarlineMarketCard(
                    market = market,
                    onClick = { onSelectPlayMarket(market) }
                )
            }
        } else {
            items(filteredMarkets, key = { it.id }) { market ->
                MainMarketCard(
                    market = market,
                    onCardClick = { onSelectPlayMarket(market) },
                    onPlayClick = { onSelectPlayMarket(market) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickActionTile(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    borderColor: Color,
    containerBrush: Brush,
    labelColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerBrush)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = labelColor,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MarketStatusLight(
    isOn: Boolean,
    modifier: Modifier = Modifier
) {
    val outerBg = if (isOn) EmeraldPrimary.copy(alpha = 0.28f) else RosePrimary.copy(alpha = 0.28f)
    val outerBorder = if (isOn) EmeraldLight.copy(alpha = 0.85f) else RoseLight.copy(alpha = 0.85f)
    val innerCore = if (isOn) EmeraldLight else RosePrimary

    Box(
        modifier = modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(outerBg)
            .border(1.5.dp, outerBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(innerCore)
        )
    }
}

@Composable
private fun MainMarketCard(
    market: MarketItem,
    onCardClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    val openWindowOpen = InitialMarketData.isOpenBidWindowOpen(market)
    val closeWindowOpen = InitialMarketData.isCloseBidWindowOpen(market)
    val effectiveMarketOn = market.isLive && (openWindowOpen || closeWindowOpen)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardSurface)
            .border(
                1.dp,
                if (effectiveMarketOn) EmeraldPrimary.copy(alpha = 0.32f) else RosePrimary.copy(alpha = 0.28f),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onCardClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("market_card_${market.id}")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MarketStatusLight(isOn = effectiveMarketOn)
                Text(
                    text = market.name,
                    fontFamily = SyneFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = SlateTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = market.resultCode,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = GoldPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color(0x14FFFFFF))
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Bid Open Time :",
                        color = SlateTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = market.openTime,
                        fontFamily = JetBrainsMonoFontFamily,
                        color = if (openWindowOpen) SlateTextPrimary else SlateTextDim,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Bid Close Time :",
                        color = SlateTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = market.closeTime,
                        fontFamily = JetBrainsMonoFontFamily,
                        color = if (closeWindowOpen) SlateTextPrimary else SlateTextDim,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = when {
                        !effectiveMarketOn -> "Market is OFF"
                        !openWindowOpen && closeWindowOpen -> "Close Bids Running"
                        else -> "Betting is Running"
                    },
                    color = if (effectiveMarketOn) EmeraldLight else RosePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (effectiveMarketOn) EmeraldPrimary else RoseDark)
                        .clickable(onClick = onPlayClick)
                        .testTag("play_btn_${market.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (effectiveMarketOn) Icons.Default.PlayArrow else Icons.Default.Lock,
                        contentDescription = if (effectiveMarketOn) "Play ${market.name}" else "${market.name} Closed",
                        tint = if (effectiveMarketOn) SlateDarkText else SlateTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StarlineMarketCard(
    market: MarketItem,
    onClick: () -> Unit
) {
    val openWindowOpen = InitialMarketData.isOpenBidWindowOpen(market)
    val effectiveMarketOn = market.isLive && openWindowOpen

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardSurface)
            .border(
                1.dp,
                if (effectiveMarketOn) EmeraldPrimary.copy(alpha = 0.32f) else RosePrimary.copy(alpha = 0.28f),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("starline_card_${market.id}")
    ) {
        Text(
            text = market.resultCode,
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = GoldPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MarketStatusLight(isOn = effectiveMarketOn)
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = SlateTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = market.openTime,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = SlateTextPrimary
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = SlateTextDim,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = market.closeTime,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 12.sp,
                        color = SlateTextMuted
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (effectiveMarketOn) "Betting is Running" else "Market is OFF",
                    color = if (effectiveMarketOn) EmeraldLight else RosePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (effectiveMarketOn) EmeraldPrimary else RoseDark)
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (effectiveMarketOn) Icons.Default.PlayArrow else Icons.Default.Lock,
                        contentDescription = market.openTime,
                        tint = if (effectiveMarketOn) SlateDarkText else SlateTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationsDialog(
    isOpen: Boolean,
    currentUserPhone: String,
    notifications: List<AppNotificationEntity>,
    onClose: () -> Unit
) {
    if (!isOpen) return

    val welcomeNoticeRecord = notifications.firstOrNull {
        it.id == "permanent-welcome-notice" || it.type == "WELCOME_NOTICE_BOARD" || it.type == "WELCOME_NOTICE_ERASED"
    }
    val isWelcomeErased = welcomeNoticeRecord?.type == "WELCOME_NOTICE_ERASED"
    val welcomeTitle = welcomeNoticeRecord?.title?.ifBlank { InitialMarketData.DEFAULT_WELCOME_NOTICE_TITLE }
        ?: InitialMarketData.DEFAULT_WELCOME_NOTICE_TITLE
    val welcomeBody = welcomeNoticeRecord?.message?.ifBlank { InitialMarketData.DEFAULT_WELCOME_NOTICE_BODY }
        ?: InitialMarketData.DEFAULT_WELCOME_NOTICE_BODY

    // Show Market Result Announcements, All-User Notices, and Particular User Notices for this user
    val validNotifications = notifications.filter {
        it.type == "MARKET_RESULT" ||
            it.type == "ADMIN_NOTICE" ||
            (it.type == "USER_NOTICE" && (it.marketName == currentUserPhone || it.marketName.isBlank()))
    }

    Dialog(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(CardSurface)
                .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(22.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Results & Official Notices",
                    fontFamily = SyneFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = SlateTextPrimary
                )
                IconButton(onClick = onClose, modifier = Modifier.size(30.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SlateTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (!isWelcomeErased) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(GoldPrimary.copy(alpha = 0.12f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "WELCOME NOTICE BOARD • $welcomeTitle",
                        color = GoldPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = welcomeBody,
                        color = SlateTextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                HorizontalDivider(color = CardBorderSubtle)
            }

            if (validNotifications.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "No Result or Notice Alerts Yet",
                        color = SlateTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "You will receive notifications when a Market Result is declared or when Admin sends an official Notice.",
                        color = SlateTextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(validNotifications, key = { it.id }) { item ->
                        val isResult = item.type == "MARKET_RESULT"
                        val isUserNotice = item.type == "USER_NOTICE"
                        val accentColor = when {
                            isResult -> EmeraldLight
                            isUserNotice -> SkyPrimary
                            else -> GoldPrimary
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(ObsidianBg)
                                .border(
                                    1.dp,
                                    accentColor.copy(alpha = 0.35f),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (isResult) Icons.Default.EmojiEvents else Icons.Default.Campaign,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = item.message,
                                    fontFamily = if (isResult) JetBrainsMonoFontFamily else null,
                                    color = SlateTextPrimary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = item.timestamp,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    color = SlateTextDim,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

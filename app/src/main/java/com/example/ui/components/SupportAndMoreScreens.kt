package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InitialMarketData
import com.example.data.model.SupportMessageEntity
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SkyDark
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SlateDarkText
import com.example.ui.theme.SlateTextDim
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SyneFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SupportContactScreen(
    onBackToDashboard: () -> Unit,
    onOpenChatBot: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var copiedNotice by remember { mutableStateOf<String?>(null) }

    BackHandler { onBackToDashboard() }

    fun copyAndLaunch(label: String, value: String, url: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, value))
        copiedNotice = "$label ($value) copied!"
        scope.launch {
            delay(2500)
            copiedNotice = null
        }
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
            // Safely handled if external app is not installed
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SubPageHeader(
            title = "Support",
            onBack = onBackToDashboard
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(CardSurface)
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(22.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(ObsidianBg)
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                DhanRatanLogo(sizeDp = 50.dp, showText = false)
            }
            Text(
                text = "DhanRatan Official Support",
                fontFamily = SyneFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = SlateTextPrimary
            )
            Text(
                text = "Connect with us on WhatsApp or Telegram for instant support",
                color = SlateTextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        if (copiedNotice != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(EmeraldPrimary.copy(alpha = 0.16f))
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = copiedNotice.orEmpty(),
                    color = EmeraldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // 1. WHATSAPP SUPPORT
        SupportChannelCard(
            title = "WHATSAPP SUPPORT",
            subtitle = "Chat with us on WhatsApp (+91 ${InitialMarketData.SUPPORT_WHATSAPP_NUMBER})",
            icon = Icons.Default.Chat,
            badgeColors = listOf(EmeraldLight, EmeraldPrimary),
            accentTint = EmeraldLight,
            onClick = {
                copyAndLaunch(
                    "WhatsApp Support Number",
                    InitialMarketData.SUPPORT_WHATSAPP_NUMBER,
                    "https://wa.me/919021786641?text=Hello%20DhanRatan%20Support"
                )
            }
        )

        // 2. TELEGRAM SUPPORT
        SupportChannelCard(
            title = "TELEGRAM SUPPORT",
            subtitle = "Connect on Telegram (@${InitialMarketData.SUPPORT_TELEGRAM_HANDLE})",
            icon = Icons.AutoMirrored.Filled.Send,
            badgeColors = listOf(SkyPrimary, SkyDark),
            accentTint = SkyPrimary,
            onClick = {
                copyAndLaunch(
                    "Telegram Support ID",
                    InitialMarketData.SUPPORT_TELEGRAM_HANDLE,
                    "https://t.me/I_0g_00"
                )
            }
        )

        // 3. GAME SUPPORT CHATBOT
        SupportChannelCard(
            title = "GAME SUPPORT CHATBOT",
            subtitle = "Instant automated help for game rules & bids",
            icon = Icons.Default.SmartToy,
            badgeColors = listOf(GoldPrimary, GoldSecondary),
            accentTint = GoldPrimary,
            onClick = onOpenChatBot
        )
    }
}

@Composable
private fun SupportChannelCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColors: List<Color>,
    accentTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
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
                    .background(Brush.linearGradient(badgeColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = SlateDarkText,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = SyneFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = SlateTextPrimary
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

@Composable
fun SupportChatScreen(
    messages: List<SupportMessageEntity>,
    onSendMessage: (String) -> Unit
) {
    var isBotOpen by rememberSaveable { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    BackHandler(enabled = isBotOpen) {
        isBotOpen = false
    }

    val quickTopics = listOf(
        "Bid Issue / How to Place Bid",
        "Deposit Not Added",
        "Withdrawal & Bank Details",
        "DhanRatan Starline Rules",
        "Game Rates & Win Ratio",
        "Rules & Regulations"
    )

    if (!isBotOpen) {
        // Closed Support Bot state: user clicks on the Bot whenever help is needed
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardSurface)
                    .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                    .clickable { isBotOpen = true }
                    .padding(24.dp)
                    .testTag("open_support_bot_card"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(GoldPrimary, GoldSecondary)))
                        .border(2.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Support Bot",
                        tint = SlateDarkText,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Text(
                    text = "DhanRatan Support Bot",
                    fontFamily = SyneFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = SlateTextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Support Bot is currently closed. Tap on the Bot below whenever you need help with bids, deposits, withdrawals, or game rules.",
                    color = SlateTextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = { isBotOpen = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = SlateDarkText
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("open_support_bot_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Click to Open Support Bot",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(CardSurface)
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        // Chatbot Header with Close Bot button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(GoldPrimary, GoldSecondary))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = SlateDarkText,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "DhanRatan Game Support Chatbot",
                        fontFamily = SyneFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Automated Game Assistant • Online",
                        color = EmeraldLight,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianBg)
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(10.dp))
                    .clickable { isBotOpen = false }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("close_support_bot_btn"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Support Bot",
                    tint = SlateTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Close Bot",
                    color = SlateTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        HorizontalDivider(color = CardBorderSubtle)

        // Messages Feed
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.sender == "user"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 310.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isUser) GoldPrimary else ObsidianBg)
                            .border(
                                width = 1.dp,
                                color = if (isUser) GoldPrimary else CardBorderSubtle,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = if (isUser) SlateDarkText else SlateTextSecondary,
                            fontWeight = if (isUser) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                    Text(
                        text = msg.timestamp,
                        fontFamily = JetBrainsMonoFontFamily,
                        color = SlateTextDim,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 3.dp, start = 4.dp, end = 4.dp)
                    )
                }
            }
        }

        // Quick Topic Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickTopics.forEach { topic ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianBg)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onSendMessage(topic) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = topic,
                        color = GoldPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        HorizontalDivider(color = CardBorderSubtle)

        // Input Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = {
                    Text(
                        "Describe your game, bid, deposit, or withdrawal issue...",
                        color = SlateTextDim,
                        fontSize = 12.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ObsidianBg,
                    unfocusedContainerColor = ObsidianBg,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorderSubtle,
                    focusedTextColor = SlateTextPrimary,
                    unfocusedTextColor = SlateTextPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("support_chat_input")
            )

            Button(
                onClick = {
                    val trimmed = input.trim()
                    if (trimmed.isNotEmpty()) {
                        onSendMessage(trimmed)
                        input = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = SlateDarkText
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(50.dp)
                    .testTag("support_chat_send_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Ask Bot",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text("Ask Bot", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MoreGameRatesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Game Rates & Rules",
                fontFamily = SyneFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = SlateTextPrimary
            )
        }

        // BOX 1: Main Game Win Ratio For All Bids
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardSurface)
                .border(1.5.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Main Game Win Ratio For All Bids",
                fontFamily = SyneFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GoldPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = CardBorderSubtle)
            InitialMarketData.MAIN_GAME_RATES.forEach { rate ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = rate.label,
                        color = SlateTextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = rate.ratio,
                        fontFamily = JetBrainsMonoFontFamily,
                        color = GoldPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // BOX 2: DhanRatan Starline Game Win Ratio
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardSurface)
                .border(1.5.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "DhanRatan Starline Game Win Ratio",
                fontFamily = SyneFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GoldPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = CardBorderSubtle)
            InitialMarketData.STARLINE_GAME_RATES.forEach { rate ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = rate.label,
                        color = SlateTextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = rate.ratio,
                        fontFamily = JetBrainsMonoFontFamily,
                        color = GoldPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // BOX 3: RULES AND REGULATIONS OF GAME
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardSurface)
                .border(1.5.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Rules And Regulations of Game",
                    fontFamily = SyneFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GoldPrimary
                )
            }
            HorizontalDivider(color = CardBorderSubtle)

            RuleSectionCard(
                title = "1. Cheating - Bets",
                paragraphs = listOf(
                    "If Admin Found Any Cheating, Hacking Or Phishing Activity, Admin Has All Rights To Take Necessary Action And Block The User Immediately."
                )
            )
            RuleSectionCard(
                title = "2. Unfair - Bets",
                paragraphs = listOf(
                    "Blocking Digits, Canning, Match Fix Bets Or Any Unfair Betting Is Strictly Not Allowed.",
                    "If Admin Found Any Unfair Activity, Necessary Action Will Be Taken And User ID May Be Blocked."
                )
            )
            RuleSectionCard(
                title = "3. Withdrawal Information",
                paragraphs = listOf(
                    "DhanRatan GAMES Is Not Responsible For Any Errors Caused By Incorrect Bank Details Entered By Users.",
                    "Before Requesting Withdrawal, Please Re-Check Your Bank Details Carefully.",
                    "• Bet Limit: ₹1 to ₹10,000   • Deposit Limit: ₹100 to ₹10,000\n• Withdrawal Limit: ₹500 to ₹1,00,000   • Timing: 10:00 AM to 09:00 PM"
                )
            )
        }
    }
}

@Composable
private fun RuleSectionCard(
    title: String,
    paragraphs: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ObsidianBg)
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.GppMaybe,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                color = GoldPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
        paragraphs.forEach { p ->
            Text(
                text = p,
                color = SlateTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

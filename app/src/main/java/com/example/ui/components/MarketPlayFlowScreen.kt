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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InitialMarketData
import com.example.data.model.BidRecordEntity
import com.example.data.model.GameVariety
import com.example.data.model.MarketItem
import com.example.data.model.QueuedBidItem
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.SlateDarkText
import com.example.ui.theme.SlateTextDim
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SyneFontFamily
import kotlin.math.roundToInt

@Composable
fun MarketPlayFlowScreen(
    market: MarketItem,
    userPhone: String,
    userBalance: Int,
    onBackToMarkets: () -> Unit,
    onSubmitBids: (List<BidRecordEntity>) -> Unit,
    onOpenDeposit: () -> Unit
) {
    val isStarline = market.category == "starline"
    val openBidAllowed = InitialMarketData.isOpenBidWindowOpen(market)
    val closeBidAllowed = InitialMarketData.isCloseBidWindowOpen(market)
    val defaultSession = if (!isStarline && !openBidAllowed && closeBidAllowed) "CLOSE" else "OPEN"

    var selectedGame by remember { mutableStateOf<GameVariety?>(null) }
    var sessionType by remember(defaultSession) { mutableStateOf(defaultSession) } // "OPEN" | "CLOSE"
    var digitInput by remember { mutableStateOf("") }
    var pointsInput by remember { mutableStateOf("") }
    var openPannaInput by remember { mutableStateOf("") }
    var closePannaOrAnkInput by remember { mutableStateOf("") }
    var queuedBids by remember { mutableStateOf<List<QueuedBidItem>>(emptyList()) }
    var errorMsg by remember { mutableStateOf("") }

    val singleAnkAmounts = remember {
        mutableStateMapOf(
            "0" to "", "1" to "", "2" to "", "3" to "", "4" to "",
            "5" to "", "6" to "", "7" to "", "8" to "", "9" to ""
        )
    }
    val bulkSingleAnkPoints = remember { mutableStateMapOf<String, Int>() }

    val availableGames = if (isStarline) {
        InitialMarketData.STARLINE_GAMES
    } else {
        InitialMarketData.ALL_12_MAIN_GAMES
    }

    fun resetGameScreenState(game: GameVariety?) {
        val currentOpenOk = InitialMarketData.isOpenBidWindowOpen(market)
        val currentCloseOk = InitialMarketData.isCloseBidWindowOpen(market)
        if (game != null && !isStarline && !currentOpenOk &&
            (game == GameVariety.JODI ||
                game == GameVariety.JODI_DIGIT_BULK ||
                game == GameVariety.HALF_SANGAM_A ||
                game == GameVariety.HALF_SANGAM_B ||
                game == GameVariety.FULL_SANGAM)
        ) {
            selectedGame = game
            sessionType = "CLOSE"
            errorMsg = "Open result bidding closed at ${market.openTime}. ${game.displayName} can only be placed before ${market.openTime}."
            return
        }
        selectedGame = game
        sessionType = if (!isStarline && !currentOpenOk && currentCloseOk) "CLOSE" else "OPEN"
        digitInput = ""
        pointsInput = ""
        openPannaInput = ""
        closePannaOrAnkInput = ""
        queuedBids = emptyList()
        errorMsg = ""
        listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9").forEach {
            singleAnkAmounts[it] = ""
        }
        bulkSingleAnkPoints.clear()
    }

    BackHandler {
        if (selectedGame != null) {
            resetGameScreenState(null)
        } else {
            onBackToMarkets()
        }
    }

    val displayMarketTitle = if (isStarline) {
        "DhanRatan Starline (${market.openTime})"
    } else {
        market.name
    }

    val headerTitle = when (selectedGame) {
        null -> "$displayMarketTitle Dashboard"
        GameVariety.SINGLE_ANK -> "$displayMarketTitle Single Ank Dashboard"
        GameVariety.SINGLE_DIGIT_BULK -> "$displayMarketTitle Single Ank Bulk Dashboard"
        GameVariety.JODI -> "$displayMarketTitle Jodi Dashboard"
        GameVariety.JODI_DIGIT_BULK -> "$displayMarketTitle Bulk Jodi Dashboard"
        GameVariety.SINGLE_PATTI -> "$displayMarketTitle Single Pana Dashboard"
        GameVariety.SINGLE_PANA_BULK -> "$displayMarketTitle Single Pana Bulk Dashboard"
        GameVariety.DOUBLE_PATTI -> "$displayMarketTitle Double Pana Dashboard"
        GameVariety.DOUBLE_PANA_BULK -> "$displayMarketTitle Double Pana Bulk Dashboard"
        GameVariety.TRIPLE_PATTI -> "$displayMarketTitle Triple Pana Dashboard"
        GameVariety.HALF_SANGAM_A, GameVariety.HALF_SANGAM_B -> "$displayMarketTitle Half Sangam Dashboard"
        GameVariety.FULL_SANGAM -> "$displayMarketTitle Full Sangam Dashboard"
    }

    val (totalBids, totalPoints) = when (selectedGame) {
        GameVariety.SINGLE_ANK -> {
            var count = 0
            var sum = 0
            singleAnkAmounts.values.forEach { v ->
                val n = v.toIntOrNull() ?: 0
                if (n > 0) {
                    count += 1
                    sum += n
                }
            }
            count to sum
        }
        GameVariety.SINGLE_DIGIT_BULK -> {
            var count = 0
            var sum = 0
            bulkSingleAnkPoints.values.forEach { pts ->
                if (pts > 0) {
                    count += 1
                    sum += pts
                }
            }
            count to sum
        }
        else -> {
            queuedBids.size to queuedBids.sumOf { it.points }
        }
    }

    fun validateSessionTime(targetSession: String): Boolean {
        val nowOpenOk = InitialMarketData.isOpenBidWindowOpen(market)
        val nowCloseOk = InitialMarketData.isCloseBidWindowOpen(market)
        return if (targetSession == "CLOSE") {
            if (!nowCloseOk) {
                errorMsg = "Close result bidding is closed (Bid Close Time: ${market.closeTime}). You can only place Close bids before ${market.closeTime}."
                false
            } else {
                true
            }
        } else {
            if (!nowOpenOk) {
                errorMsg = "Open result bidding is closed (Bid Open Time: ${market.openTime}). You can only place Open bids before ${market.openTime}."
                false
            } else {
                true
            }
        }
    }

    fun handleSingleDigitBulkTap(digit: String) {
        errorMsg = ""
        if (!validateSessionTime(sessionType)) return
        val pts = pointsInput.toIntOrNull() ?: 0
        if (pts < 1 || pts > 10000) {
            errorMsg = "Please enter points between ₹1 (Min) and ₹10,000 (Max) before tapping a digit."
            return
        }
        val current = bulkSingleAnkPoints[digit] ?: 0
        if (current + pts > 10000) {
            errorMsg = "Maximum bet per digit is ₹10,000."
            return
        }
        bulkSingleAnkPoints[digit] = current + pts
    }

    fun handlePanaBulkDigitTap(ank: String, isDouble: Boolean) {
        errorMsg = ""
        if (!validateSessionTime(sessionType)) return
        val pts = pointsInput.toIntOrNull() ?: 0
        if (pts < 1 || pts > 10000) {
            errorMsg = "Please enter points between ₹1 (Min) and ₹10,000 (Max) first."
            return
        }
        val panaList = if (isDouble) {
            InitialMarketData.DOUBLE_PANA_BY_ANK[ank].orEmpty()
        } else {
            InitialMarketData.SINGLE_PANA_BY_ANK[ank].orEmpty()
        }
        val now = System.currentTimeMillis()
        val newItems = panaList.mapIndexed { idx, pana ->
            QueuedBidItem(
                id = "$pana-$sessionType-$now-$idx",
                digit = pana,
                points = pts,
                gameType = sessionType
            )
        }
        queuedBids = newItems + queuedBids
    }

    fun handleAddMore() {
        errorMsg = ""
        val game = selectedGame ?: return
        val targetSess = if (game == GameVariety.JODI ||
            game == GameVariety.JODI_DIGIT_BULK ||
            game == GameVariety.HALF_SANGAM_A ||
            game == GameVariety.HALF_SANGAM_B ||
            game == GameVariety.FULL_SANGAM
        ) {
            "OPEN"
        } else {
            sessionType
        }
        if (!validateSessionTime(targetSess)) return

        val pts = pointsInput.toIntOrNull() ?: 0
        if (pts < 1 || pts > 10000) {
            errorMsg = "Bet points must be between ₹1 (Min) and ₹10,000 (Max)."
            return
        }
        val now = System.currentTimeMillis()

        when (game) {
            GameVariety.HALF_SANGAM_A -> {
                if (openPannaInput.length != 3 || closePannaOrAnkInput.length != 1) {
                    errorMsg = "Enter valid 3-digit Open Panna and 1-digit Close Ank."
                    return
                }
                queuedBids = listOf(
                    QueuedBidItem("hsA-$now", "$openPannaInput-$closePannaOrAnkInput", pts, "SANGAM")
                ) + queuedBids
                openPannaInput = ""
                closePannaOrAnkInput = ""
            }
            GameVariety.HALF_SANGAM_B -> {
                if (openPannaInput.length != 1 || closePannaOrAnkInput.length != 3) {
                    errorMsg = "Enter valid 1-digit Open Ank and 3-digit Close Panna."
                    return
                }
                queuedBids = listOf(
                    QueuedBidItem("hsB-$now", "$openPannaInput-$closePannaOrAnkInput", pts, "SANGAM")
                ) + queuedBids
                openPannaInput = ""
                closePannaOrAnkInput = ""
            }
            GameVariety.FULL_SANGAM -> {
                if (openPannaInput.length != 3 || closePannaOrAnkInput.length != 3) {
                    errorMsg = "Enter valid 3-digit Open Panna and 3-digit Close Panna."
                    return
                }
                queuedBids = listOf(
                    QueuedBidItem("fs-$now", "$openPannaInput-$closePannaOrAnkInput", pts, "SANGAM")
                ) + queuedBids
                openPannaInput = ""
                closePannaOrAnkInput = ""
            }
            else -> {
                val expectedLen = if (game == GameVariety.JODI) 2 else 3
                if (digitInput.length != expectedLen) {
                    errorMsg = "Please enter a valid $expectedLen-digit number."
                    return
                }
                if (game == GameVariety.DOUBLE_PATTI && !InitialMarketData.isValidDoublePatti(digitInput)) {
                    errorMsg = "Invalid Double Patti! Double Patti must repeat 2 numbers (e.g. 112, 220, 448)."
                    return
                }
                if (game == GameVariety.SINGLE_PATTI && !InitialMarketData.isValidSinglePatti(digitInput)) {
                    errorMsg = "Invalid Single Patti! All 3 digits must be different (e.g. 127, 235, 468)."
                    return
                }
                if (game == GameVariety.TRIPLE_PATTI && !InitialMarketData.isValidTriplePatti(digitInput)) {
                    errorMsg = "Invalid Triple Patti! All 3 digits must be identical (e.g. 000, 111, 777)."
                    return
                }
                queuedBids = listOf(
                    QueuedBidItem(
                        id = "bid-$now",
                        digit = digitInput,
                        points = pts,
                        gameType = if (game == GameVariety.JODI) "JODI" else sessionType
                    )
                ) + queuedBids
                digitInput = ""
            }
        }
    }

    fun handleFinalSubmit() {
        errorMsg = ""
        val game = selectedGame ?: return
        val requiresOpenWindow = game == GameVariety.JODI ||
            game == GameVariety.JODI_DIGIT_BULK ||
            game == GameVariety.HALF_SANGAM_A ||
            game == GameVariety.HALF_SANGAM_B ||
            game == GameVariety.FULL_SANGAM ||
            ((game == GameVariety.SINGLE_ANK || game == GameVariety.SINGLE_DIGIT_BULK) && sessionType == "OPEN") ||
            queuedBids.any { it.gameType != "CLOSE" }

        val requiresCloseWindow = ((game == GameVariety.SINGLE_ANK || game == GameVariety.SINGLE_DIGIT_BULK) && sessionType == "CLOSE") ||
            queuedBids.any { it.gameType == "CLOSE" }

        if (requiresOpenWindow && !validateSessionTime("OPEN")) return
        if (requiresCloseWindow && !validateSessionTime("CLOSE")) return

        if (totalBids == 0 || totalPoints <= 0) {
            errorMsg = "Please add at least one bid before submitting."
            return
        }
        if (totalPoints > userBalance) {
            errorMsg = "Insufficient wallet balance (Required: ₹$totalPoints, Available: ₹$userBalance)."
            return
        }

        val mult = InitialMarketData.getMultiplier(game, isStarline)
        val recordsToSubmit = mutableListOf<BidRecordEntity>()

        when (game) {
            GameVariety.SINGLE_ANK -> {
                val invalid = singleAnkAmounts.values.any { v ->
                    if (v.isBlank()) false
                    else {
                        val amt = v.toIntOrNull() ?: 0
                        amt < 1 || amt > 10000
                    }
                }
                if (invalid) {
                    errorMsg = "Each bet amount must be between ₹1 (Min) and ₹10,000 (Max)."
                    return
                }
                singleAnkAmounts.forEach { (ank, valStr) ->
                    val amt = valStr.toIntOrNull() ?: 0
                    if (amt in 1..10000) {
                        recordsToSubmit.add(
                            BidRecordEntity(
                                id = "",
                                userPhone = userPhone,
                                marketId = market.id,
                                marketName = displayMarketTitle,
                                gameType = GameVariety.SINGLE_ANK.displayName,
                                session = sessionType,
                                digit = ank,
                                amount = amt,
                                potentialPayout = (amt * mult).roundToInt(),
                                status = "Active",
                                timestamp = "Just now"
                            )
                        )
                    }
                }
            }
            GameVariety.SINGLE_DIGIT_BULK -> {
                bulkSingleAnkPoints.forEach { (ank, amt) ->
                    if (amt > 0) {
                        recordsToSubmit.add(
                            BidRecordEntity(
                                id = "",
                                userPhone = userPhone,
                                marketId = market.id,
                                marketName = displayMarketTitle,
                                gameType = GameVariety.SINGLE_DIGIT_BULK.displayName,
                                session = sessionType,
                                digit = ank,
                                amount = amt,
                                potentialPayout = (amt * mult).roundToInt(),
                                status = "Active",
                                timestamp = "Just now"
                            )
                        )
                    }
                }
            }
            else -> {
                queuedBids.forEach { q ->
                    recordsToSubmit.add(
                        BidRecordEntity(
                            id = "",
                            userPhone = userPhone,
                            marketId = market.id,
                            marketName = displayMarketTitle,
                            gameType = game.displayName,
                            session = if (q.gameType == "CLOSE") "CLOSE" else "OPEN",
                            digit = q.digit,
                            amount = q.points,
                            potentialPayout = (q.points * mult).roundToInt(),
                            status = "Active",
                            timestamp = "Just now"
                        )
                    )
                }
            }
        }

        onSubmitBids(recordsToSubmit)
        resetGameScreenState(null)
    }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface)
                    .statusBarsPadding()
                    .height(60.dp)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                if (selectedGame != null) resetGameScreenState(null)
                                else onBackToMarkets()
                            }
                            .testTag("play_flow_back_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Go Back",
                            tint = SlateDarkText,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = headerTitle,
                        fontFamily = SyneFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SlateTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Wallet Badge in Top Right
                Row(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(CircleShape)
                        .background(ObsidianBg)
                        .border(1.dp, GoldPrimary.copy(alpha = 0.45f), CircleShape)
                        .clickable { onOpenDeposit() }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet Balance",
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "₹$userBalance",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SlateTextPrimary
                    )
                }
            }
        },
        bottomBar = {
            if (selectedGame != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurface)
                        .border(1.dp, CardBorderSubtle)
                        .navigationBarsPadding()
                        .height(72.dp)
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("BIDS", color = SlateTextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "$totalBids",
                                fontFamily = JetBrainsMonoFontFamily,
                                color = SlateTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Points", color = SlateTextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "$totalPoints",
                                fontFamily = JetBrainsMonoFontFamily,
                                color = GoldPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Button(
                        onClick = { handleFinalSubmit() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = SlateDarkText
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .width(142.dp)
                            .height(44.dp)
                            .testTag("submit_bids_button")
                    ) {
                        Text(
                            text = "Submit",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (selectedGame == null) {
                // SCREEN 1: GAME SELECTION GRID
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp)
                ) {
                    items(availableGames, key = { it.name }) { game ->
                        Column(
                            modifier = Modifier
                                .height(142.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(CardSurface, CardSurfaceElevated, Color(0xFF0F172A))
                                    )
                                )
                                .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(24.dp))
                                .clickable { resetGameScreenState(game) }
                                .padding(14.dp)
                                .testTag("game_card_${game.name}"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(ObsidianBg)
                                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                GameVarietyIcon(variety = game)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = game.displayName,
                                fontFamily = SyneFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SlateTextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // SCREEN 2: INDIVIDUAL GAME DASHBOARD PAGES
                val game = selectedGame!!
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp)
                ) {
                    if (errorMsg.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(RosePrimary.copy(alpha = 0.16f))
                                    .border(1.dp, RosePrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = errorMsg,
                                    color = RoseLight,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (totalPoints > userBalance) {
                                    Text(
                                        text = "+ Add Funds",
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .padding(start = 10.dp)
                                            .clickable { onOpenDeposit() }
                                    )
                                }
                            }
                        }
                    }

                    when (game) {
                        GameVariety.SINGLE_ANK -> {
                            item {
                                SessionSelectorRow(
                                    sessionType = sessionType,
                                    isStarline = isStarline,
                                    isOpenAllowed = openBidAllowed,
                                    isCloseAllowed = closeBidAllowed,
                                    openTime = market.openTime,
                                    closeTime = market.closeTime,
                                    onSessionBlocked = { errorMsg = it },
                                    onSessionChange = { sessionType = it }
                                )
                            }
                            item {
                                val digits = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    digits.chunked(2).forEach { pair ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            pair.forEach { digit ->
                                                Row(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(50.dp)
                                                        .clip(CircleShape)
                                                        .background(CardSurface)
                                                        .border(1.dp, Color(0x22FFFFFF), CircleShape)
                                                        .padding(horizontal = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                Brush.linearGradient(
                                                                    listOf(GoldPrimary, OrangeAccent)
                                                                )
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = digit,
                                                            fontFamily = JetBrainsMonoFontFamily,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = SlateDarkText,
                                                            fontSize = 15.sp
                                                        )
                                                    }

                                                    BasicTextField(
                                                        value = singleAnkAmounts[digit].orEmpty(),
                                                        onValueChange = { v ->
                                                            singleAnkAmounts[digit] = v.filter { it.isDigit() }.take(5)
                                                        },
                                                        singleLine = true,
                                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                        textStyle = TextStyle(
                                                            fontFamily = JetBrainsMonoFontFamily,
                                                            color = SlateTextPrimary,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.sp
                                                        ),
                                                        cursorBrush = SolidColor(GoldPrimary),
                                                        decorationBox = { innerTextField ->
                                                            if (singleAnkAmounts[digit].isNullOrEmpty()) {
                                                                Text(
                                                                    text = "1 - 10000",
                                                                    color = SlateTextDim,
                                                                    fontSize = 12.sp,
                                                                    fontFamily = JetBrainsMonoFontFamily
                                                                )
                                                            }
                                                            innerTextField()
                                                        },
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .testTag("single_ank_input_$digit")
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        GameVariety.SINGLE_DIGIT_BULK -> {
                            item {
                                SessionSelectorRow(
                                    sessionType = sessionType,
                                    isStarline = isStarline,
                                    isOpenAllowed = openBidAllowed,
                                    isCloseAllowed = closeBidAllowed,
                                    openTime = market.openTime,
                                    closeTime = market.closeTime,
                                    onSessionBlocked = { errorMsg = it },
                                    onSessionChange = { sessionType = it }
                                )
                            }
                            item {
                                LabeledPillInputRow(
                                    label = "ENTER POINTS :",
                                    value = pointsInput,
                                    placeholder = "Min ₹1 - Max ₹10k",
                                    maxLength = 5,
                                    onValueChange = { pointsInput = it.filter { c -> c.isDigit() } }
                                )
                            }
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    listOf(
                                        listOf("1", "2", "3"),
                                        listOf("4", "5", "6"),
                                        listOf("7", "8", "9")
                                    ).forEach { rowDigits ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            rowDigits.forEach { digit ->
                                                val addedPts = bulkSingleAnkPoints[digit] ?: 0
                                                BulkDigitButton(
                                                    digit = digit,
                                                    addedPts = addedPts,
                                                    modifier = Modifier.weight(1f),
                                                    onClick = { handleSingleDigitBulkTap(digit) }
                                                )
                                            }
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        BulkDigitButton(
                                            digit = "0",
                                            addedPts = bulkSingleAnkPoints["0"] ?: 0,
                                            modifier = Modifier.fillMaxWidth(0.33f),
                                            onClick = { handleSingleDigitBulkTap("0") }
                                        )
                                    }
                                }
                            }
                        }

                        GameVariety.JODI_DIGIT_BULK -> {
                            item {
                                LabeledPillInputRow(
                                    label = "ENTER POINTS :",
                                    value = pointsInput,
                                    placeholder = "Min ₹1 - Max ₹10k",
                                    maxLength = 5,
                                    onValueChange = { pointsInput = it.filter { c -> c.isDigit() } }
                                )
                            }
                            item {
                                LabeledPillInputRow(
                                    label = "ENTER DIGIT :",
                                    value = digitInput,
                                    placeholder = "00 - 99",
                                    maxLength = 2,
                                    onValueChange = { raw ->
                                        val clean = raw.filter { it.isDigit() }.take(2)
                                        digitInput = clean
                                        errorMsg = ""
                                        if (clean.length == 2) {
                                            val pts = pointsInput.toIntOrNull() ?: 0
                                            if (pts < 1 || pts > 10000) {
                                                errorMsg = "Please enter points between ₹1 (Min) and ₹10,000 (Max) first."
                                            } else {
                                                queuedBids = listOf(
                                                    QueuedBidItem(
                                                        id = "jodi-$clean-${System.currentTimeMillis()}",
                                                        digit = clean,
                                                        points = pts,
                                                        gameType = "JODI"
                                                    )
                                                ) + queuedBids
                                                digitInput = ""
                                            }
                                        }
                                    }
                                )
                            }
                            item {
                                QueuedBidsHeader()
                            }
                            if (queuedBids.isEmpty()) {
                                item { EmptyBidsPlaceholder() }
                            } else {
                                items(queuedBids, key = { it.id }) { item ->
                                    QueuedBidRow(
                                        item = item,
                                        onDelete = { queuedBids = queuedBids.filterNot { it.id == item.id } }
                                    )
                                }
                            }
                        }

                        GameVariety.SINGLE_PANA_BULK, GameVariety.DOUBLE_PANA_BULK -> {
                            item {
                                SessionSelectorRow(
                                    sessionType = sessionType,
                                    isStarline = isStarline,
                                    isOpenAllowed = openBidAllowed,
                                    isCloseAllowed = closeBidAllowed,
                                    openTime = market.openTime,
                                    closeTime = market.closeTime,
                                    onSessionBlocked = { errorMsg = it },
                                    onSessionChange = { sessionType = it }
                                )
                            }
                            item {
                                LabeledPillInputRow(
                                    label = "ENTER POINTS :",
                                    value = pointsInput,
                                    placeholder = "Min ₹1 - Max ₹10k",
                                    maxLength = 5,
                                    onValueChange = { pointsInput = it.filter { c -> c.isDigit() } }
                                )
                            }
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    listOf(
                                        listOf("0", "1", "2", "3", "4"),
                                        listOf("5", "6", "7", "8", "9")
                                    ).forEach { rowAnks ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            rowAnks.forEach { ank ->
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(46.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            Brush.horizontalGradient(
                                                                listOf(GoldPrimary, GoldSecondary)
                                                            )
                                                        )
                                                        .clickable {
                                                            handlePanaBulkDigitTap(
                                                                ank = ank,
                                                                isDouble = game == GameVariety.DOUBLE_PANA_BULK
                                                            )
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = ank,
                                                        fontFamily = JetBrainsMonoFontFamily,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = SlateDarkText,
                                                        fontSize = 15.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            item { QueuedBidsHeader() }
                            if (queuedBids.isEmpty()) {
                                item { EmptyBidsPlaceholder() }
                            } else {
                                items(queuedBids, key = { it.id }) { item ->
                                    QueuedBidRow(
                                        item = item,
                                        onDelete = { queuedBids = queuedBids.filterNot { it.id == item.id } }
                                    )
                                }
                            }
                        }

                        else -> {
                            // Jodi, Single Patti, Double Patti, Triple Patti, Half Sangam A/B, Full Sangam
                            if (game == GameVariety.SINGLE_PATTI ||
                                game == GameVariety.DOUBLE_PATTI ||
                                game == GameVariety.TRIPLE_PATTI
                            ) {
                                item {
                                    SessionSelectorRow(
                                        sessionType = sessionType,
                                        isStarline = isStarline,
                                        isOpenAllowed = openBidAllowed,
                                        isCloseAllowed = closeBidAllowed,
                                        openTime = market.openTime,
                                        closeTime = market.closeTime,
                                        onSessionBlocked = { errorMsg = it },
                                        onSessionChange = { sessionType = it }
                                    )
                                }
                            }

                            if (game == GameVariety.HALF_SANGAM_A ||
                                game == GameVariety.HALF_SANGAM_B ||
                                game == GameVariety.FULL_SANGAM
                            ) {
                                item {
                                    val openLen = if (game == GameVariety.HALF_SANGAM_B) 1 else 3
                                    LabeledPillInputRow(
                                        label = if (game == GameVariety.HALF_SANGAM_B) "OPEN ANK :" else "OPEN PANNA :",
                                        value = openPannaInput,
                                        placeholder = if (openLen == 1) "0 - 9" else "3 Digits",
                                        maxLength = openLen,
                                        onValueChange = { openPannaInput = it.filter { c -> c.isDigit() }.take(openLen) }
                                    )
                                }
                                item {
                                    val closeLen = if (game == GameVariety.HALF_SANGAM_A) 1 else 3
                                    LabeledPillInputRow(
                                        label = if (game == GameVariety.HALF_SANGAM_A) "CLOSE ANK :" else "CLOSE PANNA :",
                                        value = closePannaOrAnkInput,
                                        placeholder = if (closeLen == 1) "0 - 9" else "3 Digits",
                                        maxLength = closeLen,
                                        onValueChange = { closePannaOrAnkInput = it.filter { c -> c.isDigit() }.take(closeLen) }
                                    )
                                }
                            } else {
                                item {
                                    val maxLen = if (game == GameVariety.JODI) 2 else 3
                                    val placeholderText = when (game) {
                                        GameVariety.JODI -> "00 - 99"
                                        GameVariety.DOUBLE_PATTI -> "2 Repeat (e.g. 112)"
                                        GameVariety.TRIPLE_PATTI -> "3 Same (e.g. 777)"
                                        else -> "3 Digits"
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        LabeledPillInputRow(
                                            label = if (game == GameVariety.DOUBLE_PATTI || game == GameVariety.TRIPLE_PATTI) {
                                                "ENTER DIGITS :"
                                            } else {
                                                "ENTER DIGIT :"
                                            },
                                            value = digitInput,
                                            placeholder = placeholderText,
                                            maxLength = maxLen,
                                            onValueChange = {
                                                errorMsg = ""
                                                digitInput = it.filter { c -> c.isDigit() }.take(maxLen)
                                            }
                                        )

                                        if (game == GameVariety.DOUBLE_PATTI) {
                                            val isValidNow = digitInput.length == 3 && InitialMarketData.isValidDoublePatti(digitInput)
                                            val isInvalidNow = digitInput.length == 3 && !isValidNow
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        when {
                                                            isInvalidNow -> RosePrimary.copy(alpha = 0.15f)
                                                            isValidNow -> EmeraldLight.copy(alpha = 0.14f)
                                                            else -> CardSurface
                                                        }
                                                    )
                                                    .border(
                                                        1.dp,
                                                        when {
                                                            isInvalidNow -> RosePrimary.copy(alpha = 0.45f)
                                                            isValidNow -> EmeraldLight.copy(alpha = 0.4f)
                                                            else -> CardBorderSubtle
                                                        },
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = when {
                                                        isInvalidNow -> "Must repeat 2 digits (e.g. 112, 220, 448)"
                                                        isValidNow -> "Valid Double Patti (#$digitInput has 2 repeating digits)"
                                                        else -> "Rule: 3 digits with 2 repeating numbers (e.g. 112, 220, 550)"
                                                    },
                                                    color = when {
                                                        isInvalidNow -> RoseLight
                                                        isValidNow -> EmeraldLight
                                                        else -> GoldPrimary
                                                    },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                LabeledPillInputRow(
                                    label = "ENTER POINTS :",
                                    value = pointsInput,
                                    placeholder = "Min ₹1 - Max ₹10k",
                                    maxLength = 5,
                                    onValueChange = { pointsInput = it.filter { c -> c.isDigit() }.take(5) }
                                )
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { handleAddMore() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = GoldPrimary,
                                            contentColor = SlateDarkText
                                        ),
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .width(190.dp)
                                            .height(44.dp)
                                    ) {
                                        Text(
                                            text = "Add More",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            item { QueuedBidsHeader() }
                            if (queuedBids.isEmpty()) {
                                item { EmptyBidsPlaceholder() }
                            } else {
                                items(queuedBids, key = { it.id }) { item ->
                                    QueuedBidRow(
                                        item = item,
                                        onDelete = { queuedBids = queuedBids.filterNot { it.id == item.id } }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionSelectorRow(
    sessionType: String,
    isStarline: Boolean,
    isOpenAllowed: Boolean = true,
    isCloseAllowed: Boolean = true,
    openTime: String = "",
    closeTime: String = "",
    onSessionBlocked: (String) -> Unit = {},
    onSessionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "SELECT GAME TYPE :",
            color = SlateTextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        Box {
            Row(
                modifier = Modifier
                    .width(190.dp)
                    .height(44.dp)
                    .clip(CircleShape)
                    .background(CardSurface)
                    .border(1.dp, Color(0x26FFFFFF), CircleShape)
                    .clickable { if (!isStarline) expanded = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = sessionType,
                    color = SlateTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select Session",
                    tint = GoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(CardSurface)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (isOpenAllowed) "OPEN" else "OPEN (Closed at $openTime)",
                            color = if (isOpenAllowed) SlateTextPrimary else RoseLight,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onClick = {
                        expanded = false
                        if (isOpenAllowed) {
                            onSessionChange("OPEN")
                        } else {
                            onSessionBlocked("Open result bidding closed at $openTime. Only Close bidding is open until $closeTime.")
                        }
                    }
                )
                if (!isStarline) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isCloseAllowed) "CLOSE" else "CLOSE (Closed at $closeTime)",
                                color = if (isCloseAllowed) SlateTextPrimary else RoseLight,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            expanded = false
                            if (isCloseAllowed) {
                                onSessionChange("CLOSE")
                            } else {
                                onSessionBlocked("Close result bidding closed at $closeTime.")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LabeledPillInputRow(
    label: String,
    value: String,
    placeholder: String,
    maxLength: Int,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = SlateTextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        Box(
            modifier = Modifier
                .width(190.dp)
                .height(44.dp)
                .clip(CircleShape)
                .background(CardSurface)
                .border(1.dp, Color(0x26FFFFFF), CircleShape)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = value,
                onValueChange = { onValueChange(it.take(maxLength)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontFamily = JetBrainsMonoFontFamily,
                    color = SlateTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(GoldPrimary),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = SlateTextDim,
                            fontSize = 12.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BulkDigitButton(
    digit: String,
    addedPts: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(56.dp)
            .clip(CircleShape)
            .background(Brush.horizontalGradient(listOf(GoldPrimary, GoldSecondary)))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = digit,
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = SlateDarkText,
            fontSize = 17.sp
        )
        if (addedPts > 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SlateDarkText)
                    .padding(horizontal = 8.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "₹$addedPts",
                    fontFamily = JetBrainsMonoFontFamily,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun QueuedBidsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Digit", color = SlateTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("Points", color = SlateTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        Text("Game Type", color = SlateTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun EmptyBidsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No Bids Added.!",
            color = SlateTextDim,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun QueuedBidRow(
    item: QueuedBidItem,
    onDelete: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.digit,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${item.points}",
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = SlateTextPrimary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.gameType,
                    fontFamily = JetBrainsMonoFontFamily,
                    color = EmeraldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete bid",
                        tint = RosePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        HorizontalDivider(color = Color(0x12FFFFFF))
    }
}

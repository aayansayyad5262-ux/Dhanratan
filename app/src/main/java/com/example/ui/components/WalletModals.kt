package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InitialMarketData
import com.example.data.model.UserAccountEntity
import com.example.ui.DhanRatanViewModel
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.CardSurfaceHeader
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldPrimary
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun DepositFullScreenModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    user: UserAccountEntity,
    onLogDepositAttempt: (amount: Int, note: String) -> Unit = { _, _ -> },
    onDepositSuccess: (amount: Int, method: String, utr: String) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var amount by remember { mutableStateOf("") }
    var depositMode by remember { mutableStateOf("Payment Gateway") } // "Payment Gateway" | "UPI"
    var step by remember { mutableStateOf("form") } // "form" | "gateway" | "receipt"
    var utrInput by remember { mutableStateOf("") }
    var timeLeft by remember { mutableIntStateOf(300) }
    var isVerifying by remember { mutableStateOf(false) }
    var receiptAmount by remember { mutableIntStateOf(0) }
    var receiptUtr by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(step) {
        if (step == "gateway") {
            timeLeft = 300
            while (timeLeft > 0) {
                delay(1000)
                timeLeft -= 1
            }
        }
    }

    fun resetAndClose() {
        step = "form"
        utrInput = ""
        error = ""
        onClose()
    }

    BackHandler {
        if (step == "gateway") {
            error = ""
            step = "form"
        } else {
            resetAndClose()
        }
    }

    val numAmount = amount.toIntOrNull() ?: 0
    // Payment gateway link attached directly to the merchant UPI ID under the hood (not displayed as raw UPI text in UI)
    val paymentGatewayUri = "upi://pay?pa=${Uri.encode(InitialMarketData.MERCHANT_UPI_ID)}&pn=${Uri.encode(InitialMarketData.MERCHANT_NAME)}&am=$numAmount&cu=INR&tn=${Uri.encode("DhanRatan Add Fund ${user.phone}")}"

    fun launchPaymentGatewayLink() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentGatewayUri))
            context.startActivity(Intent.createChooser(intent, "Complete Payment via Gateway"))
        } catch (_: Exception) {
            // Safe fallback on emulator if no UPI handler is installed
        }
    }

    fun launchExternalUri(uriString: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
            context.startActivity(intent)
        } catch (_: Exception) {
            // Ignore if unavailable
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .systemBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // APK Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                            .clickable {
                                if (step == "gateway") {
                                    error = ""
                                    step = "form"
                                } else {
                                    resetAndClose()
                                }
                            },
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
                        text = if (step == "gateway") "Payment Gateway" else "Add Fund",
                        fontFamily = SyneFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SlateTextPrimary
                    )
                }

                if (step == "gateway") {
                    val m = timeLeft / 60
                    val s = timeLeft % 60
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.16f))
                            .border(1.dp, GoldPrimary.copy(alpha = 0.4f), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = String.format(Locale.US, "%02d:%02d", m, s),
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GoldPrimary
                        )
                    }
                }
            }

            // Top User & Wallet Balance Card
            UserWalletSummaryCard(user = user)

            when (step) {
                "receipt" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorderSubtle, RoundedCornerShape(20.dp))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.2f))
                                .border(1.dp, EmeraldPrimary.copy(alpha = 0.45f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            text = "Deposit Added Successfully",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "+₹${DhanRatanViewModel.formatInr(receiptAmount)}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "Amount has been credited to your DhanRatan Wallet",
                            color = EmeraldLight,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Paid via: DhanRatan Payment Gateway",
                            fontFamily = JetBrainsMonoFontFamily,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Ref: $receiptUtr",
                            fontFamily = JetBrainsMonoFontFamily,
                            color = SlateTextMuted,
                            fontSize = 11.sp
                        )
                        Button(
                            onClick = { resetAndClose() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = SlateDarkText
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Back to Dashboard", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                "gateway" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardSurface)
                            .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = EmeraldLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "SECURE PAYMENT GATEWAY",
                                        color = EmeraldLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = InitialMarketData.MERCHANT_NAME,
                                    color = SlateTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Amount Payable", color = SlateTextMuted, fontSize = 11.sp)
                                Text(
                                    text = "₹${DhanRatanViewModel.formatInr(numAmount)}",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                            }
                        }

                        HorizontalDivider(color = CardBorderSubtle)

                        // Payment Gateway QR Code (encodes the UPI payment link without showing raw UPI ID text)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UpiQrCodeBox(dataString = paymentGatewayUri)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Scan QR or use the Payment Gateway link below",
                                    color = SlateTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Direct Payment Gateway Link Button attached to the merchant UPI ID
                        Button(
                            onClick = { launchPaymentGatewayLink() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = SlateDarkText
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("open_payment_gateway_link_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Pay ₹${DhanRatanViewModel.formatInr(numAmount)} via Payment Gateway Link",
                                fontFamily = SyneFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    if (error.isNotEmpty()) {
                        ErrorBanner(error)
                    }

                    // Verification Card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardSurface)
                            .border(1.dp, CardBorderSubtle, RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Enter 12-Digit Payment Ref / UTR No. (After Payment)",
                            color = SlateTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = utrInput,
                            onValueChange = { utrInput = it.filter { c -> c.isDigit() }.take(12) },
                            placeholder = {
                                Text(
                                    "12-digit UTR number (or tap Verify below)",
                                    color = SlateTextDim,
                                    fontSize = 12.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Complete your ₹${DhanRatanViewModel.formatInr(numAmount)} payment via the Payment Gateway link above and submit your Payin request below for Admin verification.",
                            color = SlateTextMuted,
                            fontSize = 11.sp
                        )
                        Button(
                            onClick = {
                                error = ""
                                val cleanUtr = utrInput.filter { it.isDigit() }
                                if (cleanUtr.isNotEmpty() && cleanUtr.length < 12) {
                                    error = "Please enter a valid 12-digit Reference / UTR number, or leave blank for gateway ref."
                                } else {
                                    isVerifying = true
                                    scope.launch {
                                        delay(450)
                                        val finalUtr = if (cleanUtr.length == 12) {
                                            "UTR$cleanUtr"
                                        } else {
                                            "UTR${(100000000000L..999999999999L).random()}"
                                        }
                                        isVerifying = false
                                        onDepositSuccess(
                                            numAmount,
                                            if (depositMode == "UPI") "UPI Payment Link" else "Online Payment Gateway",
                                            finalUtr
                                        )
                                        receiptAmount = numAmount
                                        receiptUtr = finalUtr
                                        step = "receipt"
                                    }
                                }
                            },
                            enabled = !isVerifying,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = SlateDarkText
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("confirm_deposit_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = if (isVerifying) {
                                    "Sending Payin Request..."
                                } else {
                                    "Submit Deposit Request (₹${DhanRatanViewModel.formatInr(numAmount)})"
                                },
                                fontFamily = SyneFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                else -> {
                    // STEP 1: AMOUNT ENTRY & DIRECT PAYMENT GATEWAY LINK
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(CardSurface)
                            .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = EmeraldLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "INSTANT PAYMENT GATEWAY",
                                        color = EmeraldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "Min: ₹100 • Max: ₹10,000",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmeraldPrimary.copy(alpha = 0.2f))
                                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            launchExternalUri("https://wa.me/919021786641?text=Hello%20DhanRatan%20Add%20Fund%20Help")
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubbleOutline,
                                        contentDescription = "WhatsApp",
                                        tint = EmeraldLight,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text("WhatsApp", color = EmeraldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GoldPrimary.copy(alpha = 0.16f))
                                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            launchExternalUri("tel:+919021786641")
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Call",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text("Call", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (error.isNotEmpty()) {
                        ErrorBanner(error)
                    }

                    // Enter Amount Input Field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(CardSurface)
                            .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(18.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = SlateDarkText,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        BasicTextField(
                            value = amount,
                            onValueChange = { amount = it.filter { c -> c.isDigit() }.take(5) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                color = SlateTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            cursorBrush = SolidColor(GoldPrimary),
                            decorationBox = { innerTextField ->
                                if (amount.isEmpty()) {
                                    Text(
                                        text = "Enter Amount (₹100 - ₹10,000)",
                                        color = SlateTextMuted,
                                        fontSize = 14.sp,
                                        fontFamily = JetBrainsMonoFontFamily
                                    )
                                }
                                innerTextField()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("deposit_amount_input")
                        )
                    }

                    // Quick Amount Preset Grid (100, 500, 1000, 2000, 5000, 10000)
                    val presets = listOf(100, 500, 1000, 2000, 5000, 10000)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.chunked(3).forEach { rowPresets ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPresets.forEach { preset ->
                                    val selected = numAmount == preset
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (selected) GoldPrimary else CardSurface)
                                            .border(
                                                1.dp,
                                                if (selected) GoldPrimary else CardBorderSubtle,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { amount = preset.toString() }
                                            .testTag("deposit_preset_$preset"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "₹${DhanRatanViewModel.formatInr(preset)}",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            color = if (selected) SlateDarkText else SlateTextPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Deposit Mode Selector (Payment Gateway vs UPI Link)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Payment Gateway", "UPI").forEach { modeOption ->
                            val isSelected = depositMode == modeOption
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) GoldPrimary.copy(alpha = 0.16f) else CardSurface)
                                    .border(
                                        1.dp,
                                        if (isSelected) GoldPrimary else CardBorderSubtle,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { depositMode = modeOption }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { depositMode = modeOption },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = GoldPrimary,
                                        unselectedColor = SlateTextMuted
                                    ),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (modeOption == "UPI") "Pay by UPI Link" else "Payment Gateway",
                                    color = if (isSelected) GoldPrimary else SlateTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Proceed & Launch Payment Gateway Link Button
                    Button(
                        onClick = {
                            error = ""
                            if (numAmount < 100) {
                                error = "Minimum deposit amount is ₹100."
                            } else if (numAmount > 10000) {
                                error = "Maximum deposit limit is ₹10,000."
                            } else {
                                onLogDepositAttempt(
                                    numAmount,
                                    "Trying to Deposit ₹${DhanRatanViewModel.formatInr(numAmount)} via $depositMode"
                                )
                                step = "gateway"
                                launchPaymentGatewayLink()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = SlateDarkText
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("proceed_add_cash_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Proceed to Payment Gateway",
                            fontFamily = SyneFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WithdrawFullScreenModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    user: UserAccountEntity,
    onUpdateUserBank: (UserAccountEntity) -> Unit = {},
    onWithdrawSuccess: (amount: Int, destination: String, refId: String) -> Unit
) {
    if (!isOpen) return

    var amount by remember { mutableStateOf("") }
    var channel by remember { mutableStateOf("Bank Account") }
    var bankNameInput by remember(user) { mutableStateOf(user.bankName) }
    var accountNumberInput by remember(user) { mutableStateOf(user.accountNumber) }
    var ifscInput by remember(user) { mutableStateOf(user.ifscCode) }
    var upiInput by remember(user) { mutableStateOf(user.upiId) }
    var error by remember { mutableStateOf("") }
    var receiptAmount by remember { mutableIntStateOf(0) }
    var receiptDest by remember { mutableStateOf("") }
    var receiptRef by remember { mutableStateOf("") }
    var showReceipt by remember { mutableStateOf(false) }

    fun resetAndClose() {
        showReceipt = false
        error = ""
        onClose()
    }

    BackHandler { resetAndClose() }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = ObsidianBg,
        unfocusedContainerColor = ObsidianBg,
        focusedBorderColor = GoldPrimary,
        unfocusedBorderColor = CardBorderSubtle,
        focusedTextColor = SlateTextPrimary,
        unfocusedTextColor = SlateTextPrimary
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .systemBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        .clickable { resetAndClose() },
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
                    text = "Withdraw Fund",
                    fontFamily = SyneFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SlateTextPrimary
                )
            }

            UserWalletSummaryCard(user = user)

            if (showReceipt) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f))
                            .border(1.dp, GoldPrimary.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "Withdrawal Request Submitted (Pending Admin Approval)",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "₹${DhanRatanViewModel.formatInr(receiptAmount)}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Payout To: $receiptDest\nRef ID: $receiptRef",
                        fontFamily = JetBrainsMonoFontFamily,
                        color = SlateTextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Your withdrawal is queued for IMPS/UPI bank settlement. Track status in History -> Fund History.",
                        color = SlateTextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { resetAndClose() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = SlateDarkText
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Back to Dashboard", fontWeight = FontWeight.ExtraBold)
                    }
                }
            } else {
                // Withdrawal Timing & Limits Info Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Withdrawal Timing : 10:00 AM to 09:00 PM",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Min Withdrawal : ₹500 • Max Withdrawal : ₹1,00,000",
                        fontFamily = JetBrainsMonoFontFamily,
                        color = SlateTextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Winnings are settled via IMPS / UPI after Admin verification",
                        color = SlateTextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }

                if (error.isNotEmpty()) {
                    ErrorBanner(error)
                }

                // Enter Withdrawal Amount
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CardSurface)
                        .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(18.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CurrencyRupee,
                            contentDescription = null,
                            tint = SlateDarkText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    BasicTextField(
                        value = amount,
                        onValueChange = { amount = it.filter { c -> c.isDigit() }.take(6) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(
                            fontFamily = JetBrainsMonoFontFamily,
                            color = SlateTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        cursorBrush = SolidColor(GoldPrimary),
                        decorationBox = { innerTextField ->
                            if (amount.isEmpty()) {
                                Text(
                                    text = "Enter Withdrawal Amount (₹500 - ₹1,00,000)",
                                    color = SlateTextMuted,
                                    fontSize = 13.sp,
                                    fontFamily = JetBrainsMonoFontFamily
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("withdraw_amount_input")
                    )
                }

                // Payout Channel Selection & Realistic Bank / UPI Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Select Payout Mode",
                        color = SlateTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Bank Account", "Registered UPI ID").forEach { mode ->
                            val selected = channel == mode
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) GoldPrimary.copy(alpha = 0.16f) else ObsidianBg)
                                    .border(
                                        1.dp,
                                        if (selected) GoldPrimary else CardBorderSubtle,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { channel = mode }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = { channel = mode },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = GoldPrimary,
                                        unselectedColor = SlateTextDim
                                    ),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = mode,
                                    color = if (selected) GoldPrimary else SlateTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    if (channel == "Bank Account") {
                        OutlinedTextField(
                            value = bankNameInput,
                            onValueChange = { bankNameInput = it },
                            placeholder = { Text("Bank Name (e.g. State Bank of India)", color = SlateTextDim, fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = accountNumberInput,
                            onValueChange = { accountNumberInput = it.filter { c -> c.isDigit() }.take(18) },
                            placeholder = { Text("Bank Account Number (9 - 18 digits)", color = SlateTextDim, fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = ifscInput,
                            onValueChange = { ifscInput = it.uppercase(Locale.US).take(11) },
                            placeholder = { Text("11-Character IFSC Code (e.g. SBIN0001234)", color = SlateTextDim, fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = upiInput,
                            onValueChange = { upiInput = it.trim() },
                            placeholder = { Text("Enter Your Receiving UPI ID (e.g. name@upi)", color = SlateTextDim, fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Button(
                    onClick = {
                        error = ""
                        val numAmt = amount.toIntOrNull() ?: 0
                        if (numAmt < 500) {
                            error = "Minimum withdrawal amount is ₹500."
                        } else if (numAmt > 100000) {
                            error = "Maximum withdrawal limit is ₹1,00,000 (1 Lakh)."
                        } else if (numAmt > user.balance) {
                            error = "Insufficient balance. Your available wallet balance is ₹${DhanRatanViewModel.formatInr(user.balance)}."
                        } else if (channel == "Bank Account" && (bankNameInput.trim().length < 3 || accountNumberInput.length < 9 || ifscInput.trim().length < 11)) {
                            error = "Please enter valid Bank Name, Account Number (min 9 digits), and 11-character IFSC Code."
                        } else if (channel == "Registered UPI ID" && (!upiInput.contains("@") || upiInput.length < 5)) {
                            error = "Please enter a valid receiving UPI ID (containing '@')."
                        } else {
                            // Persist updated bank/UPI details on the user profile as well
                            val updatedUser = user.copy(
                                bankName = bankNameInput.trim().ifEmpty { user.bankName },
                                accountNumber = accountNumberInput.trim().ifEmpty { user.accountNumber },
                                ifscCode = ifscInput.trim().ifEmpty { user.ifscCode },
                                upiId = upiInput.trim().ifEmpty { user.upiId }
                            )
                            onUpdateUserBank(updatedUser)

                            val dest = if (channel == "Bank Account") {
                                "${bankNameInput.trim()} (A/C ••${accountNumberInput.takeLast(4)} • IFSC ${ifscInput.trim()})"
                            } else {
                                "UPI (${upiInput.trim()})"
                            }
                            val ref = "IMPS${(1000000000L..9999999999L).random()}"
                            onWithdrawSuccess(numAmt, dest, ref)
                            receiptAmount = numAmt
                            receiptDest = dest
                            receiptRef = ref
                            showReceipt = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = SlateDarkText
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("send_withdraw_request_btn")
                ) {
                    Text(
                        text = "Submit Withdrawal Request",
                        fontFamily = SyneFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun UserWalletSummaryCard(user: UserAccountEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSurfaceHeader)
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = user.fullName,
                fontFamily = SyneFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = SlateTextPrimary
            )
            Text(
                text = "+91${user.phone}",
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = GoldPrimary
            )
        }
        HorizontalDivider(color = CardBorderSubtle)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSurfaceElevated)
                .padding(vertical = 14.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = SlateDarkText,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.size(14.dp))
            Column {
                Text("Available Balance", color = SlateTextSecondary, fontSize = 12.sp)
                Text(
                    text = "₹ ${DhanRatanViewModel.formatInr(user.balance)}",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = SlateTextPrimary
                )
            }
        }
    }
}

@Composable
private fun ErrorBanner(error: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RosePrimary.copy(alpha = 0.16f))
            .border(1.dp, RosePrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = error,
            color = RoseLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

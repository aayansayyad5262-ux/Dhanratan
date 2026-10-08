package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.GameVariety
import com.example.ui.theme.DeepDarkBg
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.SlateDarkText
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SyneFontFamily

@Composable
fun DhanRatanLogo(
    sizeDp: Dp = 44.dp,
    showText: Boolean = true,
    useGeneratedIcon: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp)
                .clip(RoundedCornerShape(sizeDp * 0.26f))
                .background(
                    Brush.linearGradient(
                        colors = listOf(ObsidianBg, EmeraldDark, Color(0xFF111827))
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(listOf(GoldLight, GoldSecondary)),
                    shape = RoundedCornerShape(sizeDp * 0.26f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (useGeneratedIcon) {
                Image(
                    painter = painterResource(id = R.drawable.img_launcher_emblem_1791407374496),
                    contentDescription = "DhanRatan Games Official Icon",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Canvas(modifier = Modifier.fillMaxSize().padding(sizeDp * 0.1f)) {
                    val w = size.width
                    val h = size.height
                    val outerOctagon = Path().apply {
                        moveTo(w * 0.5f, h * 0.06f)
                        lineTo(w * 0.82f, h * 0.22f)
                        lineTo(w * 0.92f, h * 0.52f)
                        lineTo(w * 0.76f, h * 0.82f)
                        lineTo(w * 0.5f, h * 0.92f)
                        lineTo(w * 0.24f, h * 0.82f)
                        lineTo(w * 0.08f, h * 0.52f)
                        lineTo(w * 0.18f, h * 0.22f)
                        close()
                    }
                    drawPath(
                        path = outerOctagon,
                        brush = Brush.linearGradient(
                            colors = listOf(EmeraldLight, EmeraldPrimary, EmeraldDark)
                        )
                    )
                    drawPath(
                        path = outerOctagon,
                        brush = Brush.linearGradient(
                            colors = listOf(GoldLight, GoldSecondary, GoldDark)
                        ),
                        style = Stroke(width = w * 0.045f)
                    )
                }
                Text(
                    text = "₹",
                    color = GoldPrimary,
                    fontFamily = SyneFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (sizeDp.value * 0.42f).sp
                )
            }
        }

        if (showText) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = SlateTextPrimary)) {
                        append("Dhan")
                    }
                    withStyle(SpanStyle(color = GoldPrimary)) {
                        append("Ratan ")
                    }
                    withStyle(SpanStyle(color = EmeraldLight)) {
                        append("Games")
                    }
                },
                fontFamily = SyneFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp
            )
        }
    }
}

@Composable
fun GameVarietyIcon(
    variety: GameVariety,
    modifier: Modifier = Modifier.size(46.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val goldGrad = Brush.linearGradient(listOf(GoldSecondary, OrangeAccent))

        when (variety) {
            GameVariety.SINGLE_ANK, GameVariety.SINGLE_DIGIT_BULK -> {
                val hex = Path().apply {
                    moveTo(w * 0.5f, h * 0.12f)
                    lineTo(w * 0.85f, h * 0.30f)
                    lineTo(w * 0.85f, h * 0.70f)
                    lineTo(w * 0.5f, h * 0.88f)
                    lineTo(w * 0.15f, h * 0.70f)
                    lineTo(w * 0.15f, h * 0.30f)
                    close()
                }
                drawPath(hex, brush = goldGrad)
                drawPath(hex, color = GoldLight, style = Stroke(width = w * 0.04f))
                drawLine(
                    color = ObsidianBg,
                    start = Offset(w * 0.15f, h * 0.30f),
                    end = Offset(w * 0.5f, h * 0.5f),
                    strokeWidth = w * 0.04f
                )
                drawLine(
                    color = ObsidianBg,
                    start = Offset(w * 0.85f, h * 0.30f),
                    end = Offset(w * 0.5f, h * 0.5f),
                    strokeWidth = w * 0.04f
                )
                drawLine(
                    color = ObsidianBg,
                    start = Offset(w * 0.5f, h * 0.5f),
                    end = Offset(w * 0.5f, h * 0.88f),
                    strokeWidth = w * 0.04f
                )
                drawCircle(color = ObsidianBg, radius = w * 0.055f, center = Offset(w * 0.5f, h * 0.28f))
                drawCircle(color = ObsidianBg, radius = w * 0.045f, center = Offset(w * 0.31f, h * 0.54f))
                drawCircle(color = ObsidianBg, radius = w * 0.045f, center = Offset(w * 0.68f, h * 0.58f))
            }

            GameVariety.JODI, GameVariety.JODI_DIGIT_BULK -> {
                rotate(degrees = 14f, pivot = Offset(w * 0.62f, h * 0.34f)) {
                    drawRoundRect(
                        color = OrangeAccent,
                        topLeft = Offset(w * 0.42f, h * 0.14f),
                        size = Size(w * 0.38f, h * 0.38f),
                        cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
                    )
                    drawRoundRect(
                        color = GoldLight,
                        topLeft = Offset(w * 0.42f, h * 0.14f),
                        size = Size(w * 0.38f, h * 0.38f),
                        cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                        style = Stroke(width = w * 0.03f)
                    )
                }
                drawRoundRect(
                    color = GoldSecondary,
                    topLeft = Offset(w * 0.18f, h * 0.40f),
                    size = Size(w * 0.42f, h * 0.42f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
                )
                drawRoundRect(
                    color = GoldLight,
                    topLeft = Offset(w * 0.18f, h * 0.40f),
                    size = Size(w * 0.42f, h * 0.42f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                    style = Stroke(width = w * 0.03f)
                )
                drawCircle(color = ObsidianBg, radius = w * 0.04f, center = Offset(w * 0.30f, h * 0.52f))
                drawCircle(color = ObsidianBg, radius = w * 0.04f, center = Offset(w * 0.48f, h * 0.52f))
                drawCircle(color = ObsidianBg, radius = w * 0.04f, center = Offset(w * 0.39f, h * 0.61f))
                drawCircle(color = ObsidianBg, radius = w * 0.04f, center = Offset(w * 0.30f, h * 0.70f))
                drawCircle(color = ObsidianBg, radius = w * 0.04f, center = Offset(w * 0.48f, h * 0.70f))
            }

            GameVariety.SINGLE_PATTI, GameVariety.SINGLE_PANA_BULK -> {
                drawRoundRect(
                    color = GoldSecondary,
                    topLeft = Offset(w * 0.26f, h * 0.14f),
                    size = Size(w * 0.48f, h * 0.72f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
                )
                drawRoundRect(
                    color = GoldLight,
                    topLeft = Offset(w * 0.26f, h * 0.14f),
                    size = Size(w * 0.48f, h * 0.72f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                    style = Stroke(width = w * 0.035f)
                )
                drawCircle(color = ObsidianBg, radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.42f))
                drawCircle(color = ObsidianBg, radius = w * 0.07f, center = Offset(w * 0.43f, h * 0.52f))
                drawCircle(color = ObsidianBg, radius = w * 0.07f, center = Offset(w * 0.57f, h * 0.52f))
            }

            GameVariety.DOUBLE_PATTI, GameVariety.DOUBLE_PANA_BULK -> {
                rotate(degrees = -12f, pivot = Offset(w * 0.4f, h * 0.5f)) {
                    drawRoundRect(
                        color = OrangeAccent,
                        topLeft = Offset(w * 0.20f, h * 0.20f),
                        size = Size(w * 0.38f, h * 0.58f),
                        cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                    )
                }
                rotate(degrees = 8f, pivot = Offset(w * 0.6f, h * 0.5f)) {
                    drawRoundRect(
                        color = GoldSecondary,
                        topLeft = Offset(w * 0.40f, h * 0.20f),
                        size = Size(w * 0.38f, h * 0.58f),
                        cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                    )
                    val diamond = Path().apply {
                        moveTo(w * 0.59f, h * 0.36f)
                        lineTo(w * 0.68f, h * 0.49f)
                        lineTo(w * 0.59f, h * 0.62f)
                        lineTo(w * 0.50f, h * 0.49f)
                        close()
                    }
                    drawPath(diamond, color = ObsidianBg)
                }
            }

            GameVariety.TRIPLE_PATTI -> {
                rotate(degrees = -16f, pivot = Offset(w * 0.32f, h * 0.5f)) {
                    drawRoundRect(
                        color = Color(0xFFC2410C),
                        topLeft = Offset(w * 0.14f, h * 0.22f),
                        size = Size(w * 0.34f, h * 0.54f),
                        cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                    )
                }
                drawRoundRect(
                    color = OrangeAccent,
                    topLeft = Offset(w * 0.33f, h * 0.19f),
                    size = Size(w * 0.34f, h * 0.54f),
                    cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                )
                rotate(degrees = 15f, pivot = Offset(w * 0.68f, h * 0.5f)) {
                    drawRoundRect(
                        color = GoldSecondary,
                        topLeft = Offset(w * 0.48f, h * 0.22f),
                        size = Size(w * 0.34f, h * 0.54f),
                        cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                    )
                }
            }

            GameVariety.HALF_SANGAM_A, GameVariety.HALF_SANGAM_B -> {
                drawArc(
                    color = GoldSecondary,
                    startAngle = 90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.2f, h * 0.16f),
                    size = Size(w * 0.64f, h * 0.68f),
                    style = Stroke(width = w * 0.08f)
                )
                drawArc(
                    color = OrangeAccent,
                    startAngle = 90f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(w * 0.32f, h * 0.28f),
                    size = Size(w * 0.40f, h * 0.44f)
                )
            }

            GameVariety.FULL_SANGAM -> {
                drawCircle(
                    color = GoldSecondary,
                    radius = w * 0.33f,
                    center = Offset(w * 0.5f, h * 0.5f),
                    style = Stroke(width = w * 0.08f)
                )
                drawCircle(
                    color = OrangeAccent,
                    radius = w * 0.19f,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
                val innerDiamond = Path().apply {
                    moveTo(w * 0.5f, h * 0.38f)
                    lineTo(w * 0.58f, h * 0.5f)
                    lineTo(w * 0.5f, h * 0.62f)
                    lineTo(w * 0.42f, h * 0.5f)
                    close()
                }
                drawPath(innerDiamond, color = ObsidianBg)
            }
        }
    }
}

@Composable
fun UpiQrCodeBox(
    dataString: String,
    modifier: Modifier = Modifier.size(176.dp)
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridCount = 21
            val cellSize = size.width / gridCount
            val hash = dataString.hashCode()

            fun isFinderPattern(r: Int, c: Int): Boolean {
                val topLeft = r in 0..6 && c in 0..6
                val topRight = r in 0..6 && c in (gridCount - 7) until gridCount
                val bottomLeft = r in (gridCount - 7) until gridCount && c in 0..6
                return topLeft || topRight || bottomLeft
            }

            fun drawFinder(startR: Int, startC: Int) {
                for (dr in 0..6) {
                    for (dc in 0..6) {
                        val isBorder = dr == 0 || dr == 6 || dc == 0 || dc == 6
                        val isCenter = dr in 2..4 && dc in 2..4
                        if (isBorder || isCenter) {
                            drawRect(
                                color = SlateDarkText,
                                topLeft = Offset((startC + dc) * cellSize, (startR + dr) * cellSize),
                                size = Size(cellSize * 1.02f, cellSize * 1.02f)
                            )
                        }
                    }
                }
            }

            drawFinder(0, 0)
            drawFinder(0, gridCount - 7)
            drawFinder(gridCount - 7, 0)

            for (r in 0 until gridCount) {
                for (c in 0 until gridCount) {
                    if (isFinderPattern(r, c)) continue
                    if (r == 6 || c == 6) {
                        if ((r + c) % 2 == 0) {
                            drawRect(
                                color = SlateDarkText,
                                topLeft = Offset(c * cellSize, r * cellSize),
                                size = Size(cellSize * 1.02f, cellSize * 1.02f)
                            )
                        }
                        continue
                    }
                    val charCode = dataString[(r * gridCount + c) % dataString.length].code
                    val bit = ((charCode * 31 + r * 17 + c * 13 + hash) and 0xFF) % 3 != 0
                    if (bit) {
                        drawRect(
                            color = SlateDarkText,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize * 1.02f, cellSize * 1.02f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SplashLoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_bounce")
    val dot1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(450), RepeatMode.Reverse),
        label = "d1"
    )
    val dot2Offset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(450), RepeatMode.Reverse),
        label = "d2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF102A26), ObsidianBg, DeepDarkBg)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box {
                DhanRatanLogo(sizeDp = 96.dp, showText = false, useGeneratedIcon = true)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = SlateTextPrimary)) { append("Dhan") }
                    withStyle(SpanStyle(color = GoldPrimary)) { append("Ratan ") }
                    withStyle(SpanStyle(color = EmeraldLight)) { append("Games") }
                },
                fontFamily = SyneFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "OFFICIAL MATKA PLAY APK",
                color = EmeraldLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = dot1Offset.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                )
                Box(
                    modifier = Modifier
                        .offset(y = dot2Offset.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EmeraldLight)
                )
                Box(
                    modifier = Modifier
                        .offset(y = dot1Offset.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                )
            }
        }
    }
}

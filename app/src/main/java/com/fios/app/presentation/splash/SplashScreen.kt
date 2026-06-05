package com.fios.app.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fios.app.ui.components.FiosLogo
import com.fios.app.ui.theme.HackerGreenBackground
import com.fios.app.ui.theme.HackerGreenPrimary
import com.fios.app.ui.theme.HackerGreenSurface
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onLoadingComplete: () -> Unit) {
    var loadingStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(800)
        loadingStep = 1
        delay(800)
        loadingStep = 2
        delay(800)
        loadingStep = 3
        delay(1200)
        onLoadingComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HackerGreenBackground), 
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Oficial
            FiosLogo(size = 80.dp, tint = HackerGreenPrimary)
            
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Financial Operating System",
                color = Color(0xFF8B949E),
                fontSize = 18.sp,
                letterSpacing = 1.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Caja de Terminal de Carga
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(HackerGreenSurface)
                    .border(1.dp, Color(0xFF30363D), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    // Botones de ventana (dots)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF30363D))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF30363D)))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Logs de Carga
                    LoadingLogItem(
                        icon = Icons.Default.Wallet,
                        text = "Initializing Wallet...",
                        show = loadingStep >= 1,
                        isProcessing = loadingStep == 1
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LoadingLogItem(
                        icon = Icons.Default.Analytics,
                        text = "Loading Analytics...",
                        show = loadingStep >= 2,
                        isProcessing = loadingStep == 2
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LoadingLogItem(
                        icon = Icons.Default.Refresh,
                        text = "Loading Savings Engine...",
                        show = loadingStep >= 3,
                        isProcessing = loadingStep == 3
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingLogItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    show: Boolean,
    isProcessing: Boolean
) {
    if (!show) return

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isProcessing) Icons.Default.Refresh else icon,
            contentDescription = null,
            tint = if (isProcessing) HackerGreenPrimary else Color(0xFF8B949E),
            modifier = Modifier
                .size(16.dp)
                .then(if (isProcessing) Modifier.rotate(rotation) else Modifier)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "> $text",
            color = if (isProcessing) Color.White else Color(0xFF8B949E),
            fontSize = 14.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}

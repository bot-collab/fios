package com.fios.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Logo oficial de FIOS recreado con precisión geométrica (Path/GenericShape)
 * para coincidir con la identidad visual "F" aerodinámica.
 */
@Composable
fun FiosLogo(
    size: Dp = 40.dp,
    tint: Color = Color(0xFF00FF88)
) {
    Box(modifier = Modifier.size(size)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Fila Superior
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Bloque superior izquierdo (con curva superior izquierda)
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(0.5.dp)
                    .clip(GenericShape { size, _ ->
                        moveTo(size.width * 0.4f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        lineTo(0f, size.height * 0.4f)
                        quadraticBezierTo(0f, 0f, size.width * 0.4f, 0f)
                    })
                    .background(tint))
                
                // Bloque superior derecho (con inclinación/slant)
                Box(modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .padding(0.5.dp)
                    .clip(GenericShape { size, _ ->
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                    })
                    .background(tint))
            }
            
            // Fila Media
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(0.5.dp)
                    .background(tint))
                
                // Bloque medio derecho (con el slant característico)
                Box(modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .padding(0.5.dp)
                    .clip(GenericShape { size, _ ->
                        moveTo(0f, 0f)
                        lineTo(size.width * 0.85f, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                    })
                    .background(tint))
            }
            
            // Fila Inferior (Pie de la F con inclinación)
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .width(size / 2.5f)
                    .padding(0.5.dp)
                    .clip(GenericShape { size, _ ->
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width * 0.8f, size.height)
                        lineTo(0f, size.height)
                    })
                    .background(tint))
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

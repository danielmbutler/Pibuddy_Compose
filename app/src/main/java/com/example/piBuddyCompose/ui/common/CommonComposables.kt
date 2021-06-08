package com.example.piBuddyCompose.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoundedBox(
    shape: Shape,
    color: Brush,
    width: Float,
    TitleText: String,
    clickable: Boolean? = null,
    clickFunction: (() -> Unit?)? = null,
    commandOutput: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(width),
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(color)
                .fillMaxWidth()
                .padding(6.dp)
                .clickable(clickable ?: false, onClick = {
                    if (clickFunction != null) {
                        clickFunction()
                    }
                }),
            contentAlignment = Alignment.BottomCenter
        ){
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                ResultTextBox(title = TitleText, 12.sp)
                if (commandOutput != null) {
                    ResultTextBox(title = commandOutput, 24.sp)
                }
            }

        }
    }
}

@Composable
fun ResultTextBox(title: String, fontSize: TextUnit){
    Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
        modifier = Modifier.padding(6.dp)
    )
}
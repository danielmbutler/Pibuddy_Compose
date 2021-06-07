package com.example.myapplication.ui.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.common.RoundedBox
import com.example.myapplication.ui.main.MainViewModel
import com.example.myapplication.ui.theme.*

@Composable
fun ResultScreenContent(viewModel: MainViewModel, addIcon: Int){
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 28.dp)
            .verticalScroll(ScrollState(0), true),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            text = "Results",
            fontFamily = piBuddyFontFamily,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = secondary,
        )
        // CPU AND MEMORY USAGE
        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(secondary_dark, secondary)),
                width = 0.49F,
                TitleText = "Cpu Usage",
                commandOutput = "83%"
            )
            Box(modifier = Modifier.fillMaxWidth(0.01F))
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(secondary_dark, secondary)),
                width = 1F,
                TitleText = "Memory Usage",
                commandOutput = "60%"
            )

        }

        // DISK SPACE AND LOGGED IN USERS

        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(secondary_dark, secondary)),
                width = 0.49F,
                TitleText = "Disk Space Remaining",
                commandOutput = "83%"
            )
            Box(modifier = Modifier.fillMaxWidth(0.01F))
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(secondary_dark, secondary)),
                width = 1F,
                TitleText = "Logged In Users",
                commandOutput = "PI, Daniel"
            )

        }

        // If custom command

        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(secondary_dark, secondary)),
                width = 1F,
                TitleText = "Custom Command",
                commandOutput = "Hello World"
            )
        }

        // Restart and Shut Down Buttons

        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(primary_dark, primary )),
                width = 0.49F,
                TitleText = "POWER OFF",
                clickable = true,
                clickFunction = { viewModel.showToast("Powering Off ....") }
            )
            Box(modifier = Modifier.fillMaxWidth(0.01F))
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(restartButtonDark, restartButtonLight)),
                width = 1F,
                TitleText = "RESTART",
                clickable = true,
                clickFunction = { viewModel.showToast("Restarting ....") }
            )

        }

        // Add custom command button
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = addIcon),
                contentDescription ="Add Custom Command",
                modifier = Modifier
                    .clickable(true) {
                        // add custom command
                    }
                    .size(24.dp)
            )
        }

    }
}
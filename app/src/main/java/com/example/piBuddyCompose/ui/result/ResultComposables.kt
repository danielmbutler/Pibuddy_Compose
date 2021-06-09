package com.example.piBuddyCompose.ui.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.piBuddyCompose.models.CommandResults
import com.example.piBuddyCompose.ui.common.AlertDialog
import com.example.piBuddyCompose.ui.common.RoundedBox
import com.example.piBuddyCompose.ui.main.MainViewModel
import com.example.piBuddyCompose.ui.theme.*

@Composable
fun ResultScreenContent(viewModel: ResultViewModel, addIcon: Int, outputs: CommandResults?) {
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
                commandOutput = outputs?.cpuUsage
            )
            Box(modifier = Modifier.fillMaxWidth(0.01F))
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(secondary_dark, secondary)),
                width = 1F,
                TitleText = "Memory Usage",
                commandOutput = outputs?.memUsage
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
                commandOutput = outputs?.diskSpace
            )
            Box(modifier = Modifier.fillMaxWidth(0.01F))
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(secondary_dark, secondary)),
                width = 1F,
                TitleText = "Logged In Users",
                commandOutput = outputs?.loggedInUsers
            )

        }

        // If custom command
        outputs?.customCommand?.let {
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
                    commandOutput = it
                )
            }
        }

        // Restart and Shut Down

        // powerOff dialog
        val showPowerOffDialog = rememberSaveable{ mutableStateOf(false)}
        AlertDialog(function = { viewModel.powerOffDevice(
            ipAddress = outputs?.ipAddress!!,
            username = outputs.username!!,
            password = outputs.password!!
        )}, openDialog = showPowerOffDialog, title = "ShutDown", taskMessage = "Are you sure you want to shutdown your device ?..")


        //restart dialog
        val showRestartDialog = rememberSaveable{ mutableStateOf(false)}
        AlertDialog(function = { viewModel.restartDevice(
            ipAddress = outputs?.ipAddress!!,
            username = outputs.username!!,
            password = outputs.password!!
        )}, openDialog = showRestartDialog, title = "Restart", taskMessage = "Are you sure you want to restart your device ?..")

        // dialog settings
        fun showPowerOffDialogMessage(){
            showPowerOffDialog.value = true
        }
        fun showRestartOffDialogMessage(){
            showRestartDialog.value = true
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(primary_dark, primary)),
                width = 0.49F,
                TitleText = "POWER OFF",
                clickable = true,
                clickFunction = {
                    showPowerOffDialogMessage()
                }
            )
            Box(modifier = Modifier.fillMaxWidth(0.01F))
            RoundedBox(
                shape = RoundedCornerShape(32.dp),
                color = Brush.verticalGradient(listOf(restartButtonDark, restartButtonLight)),
                width = 1F,
                TitleText = "RESTART",
                clickable = true,
                clickFunction = {
                    showRestartOffDialogMessage()
                }
            )

        }

        // Add custom command button
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = addIcon),
                contentDescription = "Add Custom Command",
                modifier = Modifier
                    .clickable(true) {
                        // add custom command
                    }
                    .size(24.dp)
            )
        }

    }
}
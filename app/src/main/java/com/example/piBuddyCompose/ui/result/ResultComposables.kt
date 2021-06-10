package com.example.piBuddyCompose.ui.result

import android.widget.TextView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.piBuddyCompose.models.CommandResults
import com.example.piBuddyCompose.models.ValidConnection
import com.example.piBuddyCompose.ui.common.AlertDialog
import com.example.piBuddyCompose.ui.common.RoundedBox
import com.example.piBuddyCompose.ui.main.ConnectionTextField
import com.example.piBuddyCompose.ui.main.MainViewModel
import com.example.piBuddyCompose.ui.theme.*
import com.example.piBuddyCompose.utils.Constants

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
        val showPowerOffDialog = rememberSaveable { mutableStateOf(false) }
        AlertDialog(
            function = {
                viewModel.powerOffDevice(
                    ipAddress = outputs?.ipAddress!!,
                    username = outputs.username!!,
                    password = outputs.password!!
                )
            },
            openDialog = showPowerOffDialog,
            title = "ShutDown",
            taskMessage = "Are you sure you want to shutdown your device ?.."
        )


        //restart dialog
        val showRestartDialog = rememberSaveable { mutableStateOf(false) }
        AlertDialog(
            function = {
                viewModel.restartDevice(
                    ipAddress = outputs?.ipAddress!!,
                    username = outputs.username!!,
                    password = outputs.password!!
                )
            },
            openDialog = showRestartDialog,
            title = "Restart",
            taskMessage = "Are you sure you want to restart your device ?.."
        )

        // dialog settings
        fun showPowerOffDialogMessage() {
            showPowerOffDialog.value = true
        }

        fun showRestartOffDialogMessage() {
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
        val showCustomCommandDialog = rememberSaveable { mutableStateOf(false) }
        // valid connection object to get sent to custom dialog
        val validConnection = ValidConnection(
            ipAddress = outputs?.ipAddress!!,
            username = outputs.username!!,
            password = outputs.password!!
        )
        ResultDialog(showDialog = showCustomCommandDialog, onClose = { /*TODO*/ }, viewModel, validConnection =validConnection )
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
                        showCustomCommandDialog.value = true
                    }
                    .size(24.dp)
            )
        }

    }
}

@Composable
fun ResultDialog(showDialog: MutableState<Boolean>,
                 onClose: () -> Unit,
                 viewModel: ResultViewModel,
                 validConnection: ValidConnection) {
    if (showDialog.value) {
        Dialog(
            onDismissRequest = onClose,
            properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = primary_light
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.verticalScroll(
                        enabled = true,
                        state = rememberScrollState()
                    )
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {

                        // form states (rememberSaveAble to persist date on rotation
                        // if valid connection is now passed in bundle then show place holder
                        val storedCommandFieldState = rememberSaveable { mutableStateOf("") }
                        val errorState = rememberSaveable { mutableStateOf(false) }


                        //form fields

                        Text(text = "Add Additional Command", textAlign = TextAlign.Center)
                        ConnectionTextField(
                            errorState = errorState,
                            textValue = storedCommandFieldState,
                            text = "CustomCommand"
                        )

                        Row(
                            Modifier.padding(16.dp)
                        ) {
                            Button(
                                onClick = { showDialog.value = false },
                                colors = ButtonDefaults.buttonColors(backgroundColor = secondary),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = "Close",
                                    color = text_on_secondary
                                )
                            }
                            Button(
                                onClick = {
                                    if (storedCommandFieldState.value.isNotEmpty()){
                                        validConnection.storedCommand = storedCommandFieldState.value
                                        viewModel.saveStoredCommand(validConnection)
                                        showDialog.value = false
                                    } else {
                                        viewModel.postError("Please add Command")
                                    }


                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = secondary)
                            ) {
                                Text(
                                    text = "Add Custom Command",
                                    color = text_on_secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
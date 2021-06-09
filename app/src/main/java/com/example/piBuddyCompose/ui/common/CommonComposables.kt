package com.example.piBuddyCompose.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.piBuddyCompose.ui.theme.primary
import com.example.piBuddyCompose.ui.theme.secondary

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
@Composable
fun AlertDialog(
    function: () -> Unit,
    openDialog: MutableState<Boolean>,
    title: String,
    taskMessage: String
) {
    MaterialTheme {
        Column {

            if (openDialog.value) {

                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onCloseRequest.
                        openDialog.value = false
                    },
                    title = {
                        Text(text = title)
                    },
                    text = {
                        Text(taskMessage)
                    },
                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = secondary,
                                contentColor = Color.White
                            ),
                            onClick = {
                                openDialog.value = false
                                function.invoke()
                            }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = secondary,
                                contentColor = Color.White
                            ),
                            onClick = {
                                openDialog.value = false
                            }) {
                            Text("No")
                        }
                    },

                )

            }
        }

    }
}
package com.example.myapplication.ui.dialog

import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.ui.theme.primary_light
import com.example.myapplication.ui.theme.secondary
import com.example.myapplication.ui.theme.text_on_secondary
import com.example.myapplication.utils.Constants


@Composable
fun FullScreenDialog(showDialog: MutableState<Boolean>, onClose: () -> Unit) {
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
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = primary_light
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.verticalScroll(enabled = true, state =  rememberScrollState())
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        // Adds Android view to Compose
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth()// Occupy the max size in the Compose UI tree
                                .padding(6.dp),
                            factory = { context ->
                                // Creates custom view
                                TextView(context)
                            },
                            update = { view ->
                                // View's been inflated or state read in this block has been updated
                                // Add logic here if necessary
                                view.text = Constants.helpText
                            }
                        )
                        Column(
                            Modifier.padding(16.dp)
                           ) {
                            Button(
                                onClick = { showDialog.value = false },
                                colors = ButtonDefaults.buttonColors(backgroundColor = secondary)
                            ) {
                                Text(
                                    text = "Close",
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
package com.example.myapplication.ui.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.ui.theme.*
import com.example.myapplication.utils.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PiBuddyAppBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    dialogState: MutableState<Boolean>,
    mainViewModel: MainViewModel
) {
    val appBarStatus  by mainViewModel.appBarStatus.observeAsState()
    Log.d("appbar", "PiBuddyAppBar: $appBarStatus ")
    TopAppBar(
        title = { Text(text = "PiBuddy") },
        backgroundColor = primary,
        contentColor = Color.White,
        navigationIcon = {
            if (appBarStatus!!) {
                // if main screen is open enable sidedraw
                IconButton(onClick = {
                    // open draw
                    scope.launch { scaffoldState.drawerState.open() }
                },
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu Image",
                        )
                    }
                )

            }
        },
        actions = {
            IconButton(onClick = {
                dialogState.value = true
            }) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "App Info",
                    tint = Color.White
                )
            }
        })
}

@Composable
fun PiBuddyDrawContent(drawableId: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(primary_light)
            .fillMaxWidth()
    ) {

        Text(
            text = "Previous Connections",
            color = Color.White,
            fontSize = 36.sp,
        )

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Computer Image",
                modifier = Modifier.size(70.dp)
            )
        }
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(secondary),
            shape = RectangleShape
        )
        {
            Text(text = "Clear Connections", color = Color.White)
        }
    }
}


@Composable
fun MainScreenContent(
    navHostController: NavHostController,
    mainViewModel: MainViewModel
) {
    Column(
        Modifier
            .padding(top = 28.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MainTitle()
        DeviceForm()
        NavButtons(navHostController = navHostController) { mainViewModel.setAppBarStatus(it) }

    }
}


@Composable
private fun MainTitle() {
    Text(
        text = "PiBuddy",
        fontFamily = piBuddyFontFamily,
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = secondary,
    )
}

@Composable
private fun DeviceForm() {
    Column(Modifier.padding(16.dp)) {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            label = { Text(text = "IP Address....") },
            colors = pibuddyTextFieldColors()
        )

    }
    Column(Modifier.padding(16.dp)) {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            label = { Text(text = "Username....") },
            colors = pibuddyTextFieldColors()
        )


    }
    Column(Modifier.padding(16.dp)) {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            label = { Text(text = "Password....") },
            visualTransformation = PasswordVisualTransformation(), //Password Field
            colors = pibuddyTextFieldColors(),
        )

    }
}

@Composable
private fun NavButtons(
    navHostController: NavHostController,
    onScanButtonClick: (Boolean) -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(backgroundColor = secondary)
        ) {
            Text(
                text = "Connect",
                color = text_on_secondary
            )
        }

    }
    Column(Modifier.padding(16.dp)) {
        Button(
            onClick = {
                onScanButtonClick(false) // post livedata in viewModel to disable side drawer
                navHostController.navigate("scan_fragment")
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = secondary)
        ) {
            Text(
                text = "SCAN FOR AVAILABLE DEVICES....",
                color = text_on_secondary
            )
        }
    }
}


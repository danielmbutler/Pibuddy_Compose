package com.example.piBuddyCompose.ui.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.piBuddyCompose.models.ValidConnection
import com.example.piBuddyCompose.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PiBuddyAppBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    dialogState: MutableState<Boolean>,
    mainViewModel: MainViewModel
) {
    val appBarStatus by mainViewModel.appBarStatus.observeAsState()
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

// side Drawer Content

@Composable
fun PiBuddyDrawerContent(
    drawableId: Int,
    mainViewModel: MainViewModel,
    deleteDrawable: Int,
    navHostController: NavHostController,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope
) {
    val validConnectionsList = mainViewModel.validConnectionsList.observeAsState()

    Log.d("DrawerContent", validConnectionsList.value.toString())
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(primary_light)
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
            onClick = { mainViewModel.deleteAllValidConnections() },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(secondary),
            shape = RectangleShape
        )
        {
            Text(text = "Clear Connections", color = Color.White)
        }

        /// list Items
        LazyColumn(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            validConnectionsList.value?.let { list ->
                for (i in list) {
                    item {
                        ValidConnectionItem(
                            validConnection = i,
                            computerDrawable = drawableId,
                            deleteDrawable = deleteDrawable,
                            navHostController = navHostController,
                            scaffoldState = scaffoldState,
                            coroutineScope = scope,
                            mainViewModel = mainViewModel
                        )
                    }

                }
            }

        }
    }
}

// Side DrawerList Item

@Composable
fun ValidConnectionItem(
    validConnection: ValidConnection,
    computerDrawable: Int,
    deleteDrawable: Int,
    navHostController: NavHostController,
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope,
    mainViewModel: MainViewModel
) {

    Log.d(TAG, "ValidConnectionItem: $validConnection")
    Row(
        Modifier
            .padding(6.dp)
            .fillMaxWidth(),
    ) {
        Column {
            Text(
                text = "Available Device Found",
                modifier = Modifier
                    .padding(6.dp),
                color = secondary
            )
            Row() {
                Row(modifier = Modifier
                    .clickable(true) {
                        // navigate to main activity and pass parcelable
                        navHostController.currentBackStackEntry?.arguments?.putParcelable(
                            "validConnection",
                            validConnection
                        )
                        navHostController.navigate("main/{validConnection}") //pass bundle
                        coroutineScope.launch {
                            scaffoldState.drawerState.close() //close draw
                        }
                    }
                    .weight(0.75f)) {
                    Image(
                        painter = painterResource(id = computerDrawable),
                        contentDescription = "computer Image",
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(corner = CornerSize(32.dp)))
                    )
                    Text(
                        text = validConnection.ipAddress,
                        modifier = Modifier
                            .padding(6.dp),
                        color = primary_dark,
                        textAlign = TextAlign.Center
                    )
                }

                Image(
                    painter = painterResource(id = deleteDrawable),
                    contentDescription = "Delete Image",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(32.dp)))
                        .clickable(
                            true,
                            onClick = {
                                mainViewModel.deleteIndividualValidConnection(validConnection)
                            }),
                    alignment = Alignment.CenterEnd
                )
            }

        }
    }
}

@Composable
fun MainScreenContent(
    navHostController: NavHostController,
    mainViewModel: MainViewModel,
    validConnection: ValidConnection?
) {
    Column(
        Modifier
            .padding(top = 28.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MainTitle()
        DeviceForm(
            navHostController = navHostController,
            { mainViewModel.setAppBarStatus(it) },
            mainViewModel = mainViewModel,
            validConnection
        )

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
private fun DeviceForm(
    navHostController: NavHostController,
    shouldShowSideDrawerButton: (Boolean) -> Unit,
    mainViewModel: MainViewModel,
    validConnection: ValidConnection?
) {

    // form states (rememberSaveAble to persist date on rotation
    // if valid connection is now passed in bundle then show place holder
    val ipAddressFieldState = rememberSaveable { mutableStateOf(validConnection?.ipAddress ?: "") }
    val usernameFieldState = rememberSaveable { mutableStateOf(validConnection?.username ?: "") }
    val passwordFieldState = rememberSaveable { mutableStateOf(validConnection?.password ?: "") }
    val errorState = rememberSaveable { mutableStateOf(false) }

    //form fields
    ConnectionTextField(
        errorState = errorState,
        textValue = ipAddressFieldState,
        text = "IP Address...."
    )

    ConnectionTextField(
        errorState = errorState,
        textValue = usernameFieldState,
        text = "Username...."
    )
    ConnectionTextField(
        errorState = errorState,
        textValue = passwordFieldState,
        text = "Password....",
        transformation = PasswordVisualTransformation()
    )

    Column(Modifier.padding(16.dp)) {
        Button(
            onClick = {
                shouldShowSideDrawerButton(false) // post livedata in viewModel to disable side drawer
                // validate form fields

                if (ipAddressFieldState.value.isEmpty() ||
                    usernameFieldState.value.isEmpty() ||
                    passwordFieldState.value.isEmpty()
                ) {
                    errorState.value = true // show error
                    mainViewModel.showToast("please fill out all forms")// show toast
                } else {
                    // attempt connection
                    mainViewModel.attemptConnection(
                        ipAddress = ipAddressFieldState.value,
                        username = usernameFieldState.value,
                        password = passwordFieldState.value,
                    )
                }

            },
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
                shouldShowSideDrawerButton(false) // post livedata in viewModel to disable side drawer
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


@Composable
fun ConnectionTextField(
    errorState: MutableState<Boolean>,
    textValue: MutableState<String>,
    text: String,
    transformation: VisualTransformation? = null
) {
    Column(Modifier.padding(16.dp)) {
        if (transformation != null) {
            TextField(
                value = textValue.value,
                onValueChange = { textValue.value = it },
                label = { Text(text = text) },
                colors = pibuddyTextFieldColors(),
                isError = errorState.value,
                singleLine = true,
                visualTransformation = transformation,
            )
        } else {
            TextField(
                value = textValue.value,
                onValueChange = { textValue.value = it },
                label = { Text(text = text) },
                colors = pibuddyTextFieldColors(),
                isError = errorState.value,
                singleLine = true,
            )
        }
    }
}

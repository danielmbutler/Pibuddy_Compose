package com.example.myapplication


import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.models.ScanResult
import com.example.myapplication.ui.dialog.FullScreenDialog
import com.example.myapplication.ui.main.MainScreenContent
import com.example.myapplication.ui.main.MainViewModel
import com.example.myapplication.ui.main.PiBuddyAppBar
import com.example.myapplication.ui.main.PiBuddyDrawContent
import com.example.myapplication.ui.scan.ScanViewModel
import com.example.myapplication.ui.theme.*
import com.example.myapplication.utils.NetworkUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import java.lang.NullPointerException


@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {

    lateinit var dialogState: MutableState<Boolean>
    private val mainViewModel: MainViewModel by viewModels()
    val scanViewModel: ScanViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //Update the system bars to be translucent
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setSystemBarsColor(primary_dark)
            }
            // set Theme and parent layout
            PibuddyTheme {
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()
                dialogState = remember { mutableStateOf(false) }
                AppScaffold(
                    scaffoldState = scaffoldState,
                    scope = scope,
                    dialogState = dialogState,
                    mainViewModel,
                    scanViewModel
                )


            }

        }
        getClientWifiAddress()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getClientWifiAddress() {
        try {
            val connectivityManager =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val availableAddress = mutableListOf<String>()
            val addresses =
                connectivityManager.getLinkProperties(connectivityManager.activeNetwork)!!.linkAddresses
            addresses.forEach {
                if (NetworkUtils.validate(it.address.toString().replace("/", ""))) {
                    //Log.d("wifi", "${it.toString()} validated")
                    // set IP Address in ScanViewmodel
                    scanViewModel.setCurrentDeviceIp(it.toString())
                    availableAddress.add(it.toString())
                }
            }
            if (availableAddress.isEmpty()) {
                Toast.makeText(this, "no Wifi found please check Wifi", Toast.LENGTH_LONG).show()
            }
        } catch (ce: NullPointerException) {
            Toast.makeText(this, "no Wifi found please check Wifi", Toast.LENGTH_LONG).show()
        }

    }

}


// App Scaffold
@InternalCoroutinesApi
@Composable
fun AppScaffold(
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    dialogState: MutableState<Boolean>,
    mainViewModel: MainViewModel,
    scanViewModel: ScanViewModel
) {

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = { PiBuddyDrawContent(R.drawable.ic_baseline_computer_24) },
        topBar = { PiBuddyAppBar(scope, scaffoldState, dialogState, mainViewModel) },
        content = {
            // Help Dialog
            FullScreenDialog(showDialog = dialogState, onClose = {})
            //Navigation Host
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "main") {
                composable("main") { MainFragment(navController, mainViewModel) }
                composable("scan_fragment") { ScanFragment(scanViewModel) }
                composable("result_fragment") {}
            }
        }
    )
}

// Main Fragment
@Composable
private fun MainFragment(navHostController: NavHostController, viewModel: MainViewModel) {
    // MainFragment triggered reshowing menu in app bar
    viewModel.setAppBarStatus(true)
    MainScreenContent(navHostController, viewModel)
}

//ScanFragment
@InternalCoroutinesApi
@Composable
private fun ScanFragment(viewModel: ScanViewModel) {
    val addressCount by viewModel.addressCount.observeAsState()
    val address by viewModel.ips.observeAsState()
    val addressList = remember { mutableStateListOf<ScanResult>() }
    address?.let {
        addressList.add(it)
    }

    val shouldScan = remember { mutableStateOf(true) }
    if (shouldScan.value) {
        shouldScan.value = false
        viewModel.scanIPs()
    }

    Log.d("scanFragment", address.toString())
    Column(modifier = Modifier.background(primary_dark)) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.80F)
        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.White)
            ) {
                for (i in addressList) {
                    if (i.IP.isNotEmpty()) {
                        item { ScanResultItem(scanResult = i) }
                    }
                }
            }
        }
        Row() {
            ScanBottomSection(viewModel, addressCount)
        }
    }


}


@Composable
fun ScanResultItem(scanResult: ScanResult) {
    Log.d("scanItem", "ScanResultItem: $scanResult ")
    Card(
        Modifier
            .padding(6.dp)
            .fillMaxWidth(),
    ) {
        Row(modifier = Modifier.fillMaxWidth()){
            Column() {
                Text(
                    text = "Available Device Found",
                    modifier = Modifier
                        .padding(6.dp),
                    color = secondary
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_computer_24),
                    contentDescription = "computer Image",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(32.dp)))
                        .clickable(true, onClick = { }),
                )
            }
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = scanResult.IP,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start),
                    color = primary_dark
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_add_circle_outline_24),
                    contentDescription = "computer Image",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(32.dp)))
                        .clickable(true, onClick = { })
                        .align(Alignment.End)
                    ,
                )
            }
        }

    }

}

@Composable
private fun ScanBottomSection(viewModel: ScanViewModel, addressCount: Int?) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(primary_dark)
    ) {
        Text(
            text = "Scanning for Devices with port 22 open ......${addressCount ?: 0} addresses remaining",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(6.dp)
        )
        Button(
            onClick = { viewModel.cancelScan() },
            colors = ButtonDefaults.buttonColors(backgroundColor = secondary),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Text(
                text = "Stop Scan",
                color = text_on_secondary
            )
        }
        // invisible row to stop white space issue
        Row(
            modifier = Modifier
                .size(40.dp, 40.dp)
                .background(primary_dark)
        ) {

        }
    }

}


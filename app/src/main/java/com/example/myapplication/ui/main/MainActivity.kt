package com.example.myapplication.ui.main


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
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.models.ScanResult
import com.example.myapplication.ui.dialog.FullScreenDialog
import com.example.myapplication.ui.result.ResultScreenContent
import com.example.myapplication.ui.scan.ScanScreenContent
import com.example.myapplication.ui.scan.ScanViewModel
import com.example.myapplication.ui.theme.*
import com.example.myapplication.utils.NetworkUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import java.lang.NullPointerException


@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val scanViewModel: ScanViewModel by viewModels()

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
                val dialogState = remember { mutableStateOf(false) }
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
        initErrorStateObserver()
    }

    private fun initErrorStateObserver() {
        mainViewModel.appErrorStatus.observe(this, Observer {
            //show toast with error message if not already shown
            if (!it.hasBeenHandled){
                Toast.makeText(this, it.getContentIfNotHandled(), Toast.LENGTH_SHORT).show()
            }
        })
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

    override fun onBackPressed() {
        super.onBackPressed()
        // whenever backButton is pressed cancel Scanning operation
        scanViewModel.cancelScan()
    }

}




// UI Components


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
        drawerGesturesEnabled = false, // disable swipe on drawer
        topBar = { PiBuddyAppBar(scope, scaffoldState, dialogState, mainViewModel) },
        content = {
            // Help Dialog
            FullScreenDialog(showDialog = dialogState, onClose = {})
            //Navigation Host
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "main") {
                composable("main") { MainFragment(navController, mainViewModel) }
                composable("scan_fragment") { ScanFragment(scanViewModel) }
                composable("result_fragment") { ResultFragment(mainViewModel)}
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

    ScanScreenContent(
        addressList = addressList,
        viewModel = viewModel,
        addressCount = addressCount,
        computerDrawable = R.drawable.ic_baseline_computer_24,
        addButtonDrawable = R.drawable.ic_baseline_add_circle_outline_24
    )

    Log.d("scanFragment", address.toString())


}


@Composable
private fun ResultFragment(viewModel: MainViewModel) {
        ResultScreenContent(viewModel = viewModel, R.drawable.ic_baseline_add_circle_outline_24)
}


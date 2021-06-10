package com.example.piBuddyCompose.ui.main


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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.piBuddyCompose.R
import com.example.piBuddyCompose.models.CommandResults
import com.example.piBuddyCompose.models.ScanResult
import com.example.piBuddyCompose.models.ValidConnection
import com.example.piBuddyCompose.ui.dialog.FullScreenDialog
import com.example.piBuddyCompose.ui.result.ResultScreenContent
import com.example.piBuddyCompose.ui.result.ResultViewModel
import com.example.piBuddyCompose.ui.scan.ScanScreenContent
import com.example.piBuddyCompose.ui.scan.ScanViewModel
import com.example.piBuddyCompose.ui.theme.*
import com.example.piBuddyCompose.utils.NetworkUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import java.lang.NullPointerException


@InternalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val scanViewModel: ScanViewModel by viewModels()
    private val resultViewModel: ResultViewModel by viewModels()


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
                val dialogState = rememberSaveable { mutableStateOf(false) }
                AppScaffold(
                    scaffoldState = scaffoldState,
                    scope = scope,
                    dialogState = dialogState,
                    mainViewModel,
                    scanViewModel,
                    resultViewModel
                )


            }

        }
        getClientWifiAddress()
        initStateObservers()
    }

    private fun initStateObservers() {
        mainViewModel.appErrorStatus.observe(this, {
            //show toast with error message if not already shown
            if (!it.hasBeenHandled) {
                Toast.makeText(this, it.getContentIfNotHandled(), Toast.LENGTH_SHORT).show()
            }
        })

        // display messages from result view
        resultViewModel.appCommandStatus.observe(this, {
            //show toast with message if not already shown
            if (!it.hasBeenHandled) {
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
                    // set IP Address in ScanViewModel
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
    scanViewModel: ScanViewModel,
    resultViewModel: ResultViewModel,
) {
    //Navigation Host
    val navController = rememberNavController()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            PiBuddyDrawerContent(
                R.drawable.ic_baseline_computer_24,
                mainViewModel,
                R.drawable.ic_baseline_delete_24,
                navHostController = navController,
                scaffoldState = scaffoldState,
                scope = scope
            )
        },
        drawerGesturesEnabled = false, // disable swipe on drawer
        topBar = { PiBuddyAppBar(scope, scaffoldState, dialogState, mainViewModel) },
        content = {
            // Help Dialog
            FullScreenDialog(showDialog = dialogState, onClose = {})

            NavHost(navController = navController, startDestination = "main/{validConnection}") {
                composable("main/{validConnection}") //  nav argument for main fragment
                {
                    val validConnection = navController.previousBackStackEntry?.arguments?.getParcelable<ValidConnection>("validConnection")
                    MainFragment(navController, mainViewModel, validConnection)
                }
                composable("scan_fragment") { ScanFragment(scanViewModel, navController) }
                composable("result_fragment/{outputs}") {
                    val outputs = navController.previousBackStackEntry?.arguments?.getParcelable<CommandResults>("outputs")
                    ResultFragment(resultViewModel, outputs)
                }
            }
            /* listen for command results object in mainviewmodel, if this is present it means the app has successful run
                commands against a device, we should then navigate to the results view
             */
            val resultsObject by mainViewModel.deviceConnectionState.observeAsState()
            resultsObject?.let { results ->
                if (!results.hasBeenHandled){
                    // put bundle in backstack
                    navController.currentBackStackEntry?.arguments?.putParcelable(
                        "outputs",
                        results.getContentIfNotHandled()
                    )
                    navController.navigate("result_fragment/{outputs}") //navigate
                }
            }
        }
    )
}


// Main Fragment
@Composable
private fun MainFragment(
    navHostController: NavHostController,
    viewModel: MainViewModel,
    validConnection: ValidConnection?
) {
    Log.d(TAG, "MainFragment: ValidConnection: $validConnection")
    viewModel.setAppBarStatus(true)// MainFragment triggered reshowing menu in app bar
    MainScreenContent(navHostController, viewModel, validConnection)
}

//ScanFragment
@InternalCoroutinesApi
@Composable
private fun ScanFragment(viewModel: ScanViewModel, navHostController: NavHostController) {
    // valid IP address connection that has been found
    val address by viewModel.ips.observeAsState()
    // address list to be added to recyclerview
    val addressList = remember { mutableStateListOf<ScanResult>() }
    address?.let {
        addressList.add(it)
    }
    Log.d("addresslist", "ScanFragment: $address")

    //start scan
    // used to stop scan restarting on rotation
    val shouldScan = rememberSaveable { mutableStateOf(true) }
    if (shouldScan.value) {
        shouldScan.value = false
        viewModel.scanIPs()
    }

    ScanScreenContent(
        addressList = addressList,
        viewModel = viewModel,
        computerDrawable = R.drawable.ic_baseline_computer_24,
        addButtonDrawable = R.drawable.ic_baseline_add_circle_outline_24,
        navHostController = navHostController
    )

    Log.d("scanFragment", address.toString())


}


@Composable
private fun ResultFragment(viewModel: ResultViewModel, outputs: CommandResults?) {
    ResultScreenContent(viewModel = viewModel, R.drawable.ic_baseline_add_circle_outline_24, outputs)
}


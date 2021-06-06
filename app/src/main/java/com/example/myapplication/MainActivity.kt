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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
                composable("result_fragment") { ResultFragment()}
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

@Preview
@Composable
private fun ResultFragment(){
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Results",
            fontFamily = piBuddyFontFamily,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = secondary,
        )
        
        Row(Modifier.fillMaxWidth().padding(6.dp)) {
            Column(
                modifier =
                Modifier
                    .background(secondary_dark)
                    .fillMaxWidth(0.49F) // fill half of width
                    .padding(6.dp)
                    .clip(RoundedCornerShape(33.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
               ResultTextBox(title = "Cpu Usage")
            }
            Box(modifier = Modifier.fillMaxWidth(0.01F))
            Column(
                modifier =
                Modifier
                    .background(secondary_dark)
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
                )
            {
                ResultTextBox(title = "Memory Usage")
            }
        }

    }
}

@Composable
fun RoundedBox(shape: Shape, color: Color, size: Dp) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(color)
        )
    }
}

@Composable
fun ResultTextBox(title: String){
    Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier.padding(6.dp)
    )
}



package com.example.myapplication.ui.scan

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.models.ScanResult
import com.example.myapplication.ui.theme.primary_dark
import com.example.myapplication.ui.theme.secondary
import com.example.myapplication.ui.theme.text_on_secondary

@Composable
fun ScanScreenContent(
    addressList: List<ScanResult>,
    viewModel: ScanViewModel,
    addressCount: Int?,
    computerDrawable: Int,
    addButtonDrawable: Int
) {
    val progressBarState = remember { mutableStateOf(true) }

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

                // scan progress indicator
                item { ProgressItem(computerDrawable, progressBarState) }
                for (i in addressList) {
                    if (i.IP.isNotEmpty()) {
                        item {
                            ScanResultItem(
                                scanResult = i,
                                computerDrawable,
                                addButtonDrawable = addButtonDrawable
                            )
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxHeight()) {
            ScanBottomSection(viewModel, addressCount, progressBarState)
        }
    }
}

@Composable
fun ProgressItem(computerDrawable: Int, progressBarState: MutableState<Boolean>) {
    Card(
        Modifier
            .padding(6.dp)
            .fillMaxWidth(),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Scanning for Devices",
                    modifier = Modifier
                        .padding(6.dp),
                    color = secondary
                )
                Image(
                    painter = painterResource(id = computerDrawable),
                    contentDescription = "computer Image",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(32.dp)))
                        .clickable(true, onClick = { }),
                )
            }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                if (progressBarState.value) {
                    CircularProgressIndicator(
                        color = secondary,
                        strokeWidth = 8.dp,
                        modifier = Modifier
                            .size(45.dp)
                            .padding(8.dp)
                    )
                }

            }
        }

    }
}


@Composable
fun ScanResultItem(
    scanResult: ScanResult,
    computerDrawable: Int,
    addButtonDrawable: Int,
) {
    Log.d("scanItem", "ScanResultItem: $scanResult ")
    Card(
        Modifier
            .padding(6.dp)
            .fillMaxWidth(),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Available Device Found",
                    modifier = Modifier
                        .padding(6.dp),
                    color = secondary
                )
                Image(
                    painter = painterResource(id = computerDrawable),
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
                    painter = painterResource(id = addButtonDrawable),
                    contentDescription = "computer Image",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(32.dp)))
                        .clickable(true, onClick = { })
                        .align(Alignment.End),
                )
            }
        }

    }

}

@Composable
private fun ScanBottomSection(
    viewModel: ScanViewModel,
    addressCount: Int?,
    progressBarState: MutableState<Boolean>
) {

    val scanState = remember { mutableStateOf(true) }
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
            onClick = {
                viewModel.cancelScan()
                progressBarState.value = !progressBarState.value
                scanState.value = !scanState.value
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = secondary),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Text(
                text = if (scanState.value) {
                    "Stop Scan"
                } else {
                    "Restart Scan"
                },
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

package com.example.myapplication.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.models.ScanResult
import com.example.myapplication.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


object Repository {
    private const val TAG = "repository"
    //val _pingTest = MutableStateFlow<Resource<PingResult>>(Resource.Initial())
    val _scanPingTest = MutableStateFlow(ScanResult(""))
    val scanPingTest: StateFlow<ScanResult>
            get() = _scanPingTest
    //val _commandResults = MutableStateFlow<Resource<CommandResults>>(Resource.Initial())

    // value to control whether scan should be running
    var _scanRunning = false

    private val _addressCount = MutableLiveData<Int>()
    val addressCount: LiveData<Int>
        get() = _addressCount






    fun scanIPs(netAddresses: Array<String>, scope: CoroutineScope) {
        // set scan to running
        _scanRunning = true
        var addresscount = netAddresses.count()


        scope.launch(Dispatchers.IO) {

            netAddresses.forEach {
                Log.d(TAG, "loop runs")
                if (_scanRunning) {
                    Log.d(TAG, "scanning : $it")
                    Log.d(TAG, "scanning : scan status: $_scanRunning")

                    val pingtest = async {
                        NetworkUtils.isPortOpen(
                            it,
                            22,
                            1000
                        )

                    }

                    if (pingtest.await()) {
                        _scanPingTest.value = (ScanResult(it))
                        // decrement address count
                        addresscount--
                        //post new address count value
                        _addressCount.postValue(addresscount)
                        Log.d(TAG, "scanIPs: successful : $it, ips left: $addresscount")
                    } else {
                        // decrement address count
                        _scanPingTest.value = (ScanResult(it))
                        addresscount--
                        //post new address count value
                        _addressCount.postValue(addresscount)
                        Log.d(TAG, "scanIPs: unsuccessful : $it, ips left: $addresscount")
                    }
                } else {
                    return@forEach
                }
            }
        }
    }

    fun cancelScan() {
        _scanRunning = false
    }

//    suspend fun pingTest(ip: String, scope: CoroutineScope): Resource<PingResult> {
//        return suspendCoroutine<Resource<PingResult>> { Pingresult ->
//            scope.launch(Dispatchers.IO){
//                val result = NetworkUtils.isPortOpen(ip, 22, 3000)
//
//
//                Pingresult.resume(Resource.Success(PingResult(ip,result)))
//
////                _pingTest.value = Resource.Success(
////                    PingResult(
////                        ip,
////                        result = result.await()
////                    )
////                )
//                Log.d(TAG, "pingTestRepository: $result ")
//            }
//        }
//
//    }


//    suspend fun runPiCommands(
//        ipAddress: String,
//        username: String,
//        password: String,
//        customCommand: String?,
//        scope: CoroutineScope
//    ): Resource<CommandResults> {
//
//        return suspendCoroutine<Resource<CommandResults>> {commandResult ->
//            val resultsObject = CommandResults()
//            scope.launch(Dispatchers.IO) {
//                val testCommand = async {
//                    NetworkUtils.executeRemoteCommand(
//                        username,
//                        password,
//                        ipAddress,
//                        "echo hello"
//                    )
//                }
//
//
//                if (!testCommand.await().contains("hello")) {
//
//                    resultsObject.testCommand = false
//                    commandResult.resume(Resource.Error(message = "failed"))
//                    return@launch
//
//
//                } else {
//
//                    resultsObject.testCommand = true
//                    Log.d(
//                        TAG,
//                        "runPiCommands: testCommand completed successfull ${testCommand.await()}"
//                    )
//
//                    val LoggedInUsers = async {
//                        NetworkUtils.executeRemoteCommand(
//                            username,
//                            password,
//                            ipAddress,
//                            "who | cut -d' ' -f1 | sort | uniq\n"
//                        )
//                    }
//
//                    val DiskSpace = async {
//                        NetworkUtils.executeRemoteCommand(
//                            username,
//                            password,
//                            ipAddress,
//                            "df -hl | grep \'root\' | awk \'BEGIN{print \"\"} {percent+=$5;} END{print percent}\' | column -t"
//                        )
//                    }
//                    //
//                    val MemUsage = async {
//                        NetworkUtils.executeRemoteCommand(
//                            username,
//                            password,
//                            ipAddress,
//                            "awk '/^Mem/ {printf(\"%u%%\", 100*\$3/\$2);}' <(free -m)"
//                        )
//                    }
//                    val CpuUsage = async {
//                        NetworkUtils.executeRemoteCommand(
//                            username,
//                            password,
//                            ipAddress,
//                            "cat <(grep 'cpu ' /proc/stat) <(sleep 1 && grep 'cpu ' /proc/stat) | awk -v RS=\"\" '{print (\$13-\$2+\$15-\$4)*100/(\$13-\$2+\$15-\$4+\$16-\$5)}'"
//
//                        )
//                    }
//                    customCommand?.let {
//                        val CustomCommandRun = async {
//                            NetworkUtils.executeRemoteCommand(
//                                username,
//                                password,
//                                ipAddress,
//                                it
//                            )
//
//                        }
//                        resultsObject.customCommand = CustomCommandRun.await()
//                    }
//
//                    resultsObject.cpuUsage = CpuUsage.await()
//                    resultsObject.diskSpace = DiskSpace.await()
//                    resultsObject.memUsage = MemUsage.await()
//                    resultsObject.loggedInUsers = LoggedInUsers.await()
//                    resultsObject.username = username
//                    resultsObject.password = password
//                    resultsObject.ipAddress = ipAddress
//                    Log.d(TAG, "runPiCommands: $resultsObject")
//                    commandResult.resume(Resource.Success(resultsObject))
//                }
//            }
//        }
//
//
//    }

}

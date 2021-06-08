package com.example.piBuddyCompose.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.piBuddyCompose.models.CommandResults
import com.example.piBuddyCompose.models.ScanResult
import com.example.piBuddyCompose.models.ValidConnection
import com.example.piBuddyCompose.persistence.ConnectionsDao
import com.example.piBuddyCompose.utils.NetworkUtils
import com.example.piBuddyCompose.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class Repository @Inject constructor(
    private val dao: ConnectionsDao
) {
    private val TAG = "repository"

    private val _scanPingTest = MutableStateFlow(ScanResult(""))

    // Flow value for connection text
    val scanPingTest: StateFlow<ScanResult>
            get() = _scanPingTest
    //val _commandResults = MutableStateFlow<Resource<CommandResults>>(Resource.Initial())

    // value to control whether scan should be running
    var _scanRunning = false

    // descending value to show how many addresses are left to test
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

    // test individual IP Address for connection

    suspend fun pingTest(ip: String, scope: CoroutineScope): Resource<ScanResult> {
        return suspendCoroutine { Pingresult ->
            scope.launch(Dispatchers.IO){
                val result = NetworkUtils.isPortOpen(ip, 22, 3000)

                Pingresult.resume(Resource.Success(ScanResult(ip, result)))

                Log.d(TAG, "pingTestRepository: $result ")
            }
        }

    }



    // Run performance Related Commands
    suspend fun runPiCommands(
        ipAddress: String,
        username: String,
        password: String,
        customCommand: String?,
        scope: CoroutineScope
    ): Resource<CommandResults> {

        return suspendCoroutine<Resource<CommandResults>> {commandResult ->
            val resultsObject = CommandResults()
            scope.launch(Dispatchers.IO) {
                // test command , if we receive hello back then the rest of the commands are assumed to work
                val testCommand = async {
                    NetworkUtils.executeRemoteCommand(
                        username,
                        password,
                        ipAddress,
                        "echo hello"
                    )
                }


                if (!testCommand.await().contains("hello")) {

                    resultsObject.testCommand = false
                    commandResult.resume(Resource.Error(message = "failed", data = resultsObject))
                    return@launch


                } else {

                    resultsObject.testCommand = true
                    Log.d(
                        TAG,
                        "runPiCommands: testCommand completed successfull ${testCommand.await()}"
                    )

                    val LoggedInUsers = async {
                        NetworkUtils.executeRemoteCommand(
                            username,
                            password,
                            ipAddress,
                            "who | cut -d' ' -f1 | sort | uniq\n"
                        )
                    }

                    val DiskSpace = async {
                        NetworkUtils.executeRemoteCommand(
                            username,
                            password,
                            ipAddress,
                            "df -hl | grep \'root\' | awk \'BEGIN{print \"\"} {percent+=$5;} END{print percent}\' | column -t"
                        )
                    }
                    //
                    val MemUsage = async {
                        NetworkUtils.executeRemoteCommand(
                            username,
                            password,
                            ipAddress,
                            "awk '/^Mem/ {printf(\"%u%%\", 100*\$3/\$2);}' <(free -m)"
                        )
                    }
                    val CpuUsage = async {
                        NetworkUtils.executeRemoteCommand(
                            username,
                            password,
                            ipAddress,
                            "cat <(grep 'cpu ' /proc/stat) <(sleep 1 && grep 'cpu ' /proc/stat) | awk -v RS=\"\" '{print (\$13-\$2+\$15-\$4)*100/(\$13-\$2+\$15-\$4+\$16-\$5)}'"

                        )
                    }
                    customCommand?.let {
                        val CustomCommandRun = async {
                            NetworkUtils.executeRemoteCommand(
                                username,
                                password,
                                ipAddress,
                                it
                            )

                        }
                        resultsObject.customCommand = CustomCommandRun.await()
                    }

                    resultsObject.cpuUsage = CpuUsage.await()
                    resultsObject.diskSpace = DiskSpace.await()
                    resultsObject.memUsage = MemUsage.await()
                    resultsObject.loggedInUsers = LoggedInUsers.await()
                    resultsObject.ipAddress = ipAddress
                    Log.d(TAG, "runPiCommands: $resultsObject")

                    // store result in DB
                    dao.insertValidConnection(ValidConnection(
                        ipAddress, username, password
                    ))
                    commandResult.resume(Resource.Success(resultsObject))
                }
            }
        }


    }

    // DB Methods
    suspend fun getAllValidConnections(): List<ValidConnection> {
       return dao.getAllValidConnections()
    }

    suspend fun getValidConnection(ipAddress: String): ValidConnection{
        return dao.getSpecificValidConnection(ipAddress = ipAddress)
    }

}

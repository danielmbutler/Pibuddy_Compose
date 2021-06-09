package com.example.piBuddyCompose.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.piBuddyCompose.models.CommandResults
import com.example.piBuddyCompose.models.ScanResult
import com.example.piBuddyCompose.models.ValidConnection
import com.example.piBuddyCompose.persistence.ConnectionsDao
import com.example.piBuddyCompose.utils.Event
import com.example.piBuddyCompose.utils.NetworkUtils
import com.example.piBuddyCompose.utils.NetworkUtils.executeRemoteCommand
import com.example.piBuddyCompose.utils.NetworkUtils.isPortOpen
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

    // value to control whether scan should be running
    var _scanRunning = false

    // descending value to show how many addresses are left to test
    private val _addressCount = MutableLiveData<Int>()
    val addressCount: LiveData<Int>
        get() = _addressCount

    //accessed from result viewmodel
    private val _powerOffOrRestartMessage = MutableLiveData<Event<String>>()
    val powerOffOrRestartMessage: LiveData<Event<String>>
        get() = _powerOffOrRestartMessage


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
            scope.launch(Dispatchers.IO) {
                val result = NetworkUtils.isPortOpen(ip, 22, 3000)

                Pingresult.resume(Resource.Success(ScanResult(ip, result)))

                Log.d(TAG, "pingTestRepository: $result ")
            }
        }

    }


    // Run performance Related Commands
    suspend fun runPiCommands(
        validConnection: ValidConnection,
        scope: CoroutineScope
    ): Resource<CommandResults> {

        return suspendCoroutine<Resource<CommandResults>> { commandResult ->
            val resultsObject = CommandResults()
            scope.launch(Dispatchers.IO) {
                // test command , if we receive hello back then the rest of the commands are assumed to work
                val testCommand = async {
                    NetworkUtils.executeRemoteCommand(
                        validConnection.username,
                        validConnection.password,
                        validConnection.ipAddress,
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
                            validConnection.username,
                            validConnection.password,
                            validConnection.ipAddress,
                            "who | cut -d' ' -f1 | sort | uniq\n"
                        )
                    }

                    val DiskSpace = async {
                        NetworkUtils.executeRemoteCommand(
                            validConnection.username,
                            validConnection.password,
                            validConnection.ipAddress,
                            "df -hl | grep \'root\' | awk \'BEGIN{print \"\"} {percent+=$5;} END{print percent}\' | column -t"
                        )
                    }
                    //
                    val MemUsage = async {
                        NetworkUtils.executeRemoteCommand(
                            validConnection.username,
                            validConnection.password,
                            validConnection.ipAddress,
                            "awk '/^Mem/ {printf(\"%u%%\", 100*\$3/\$2);}' <(free -m)"
                        )
                    }
                    val CpuUsage = async {
                        NetworkUtils.executeRemoteCommand(
                            validConnection.username,
                            validConnection.password,
                            validConnection.ipAddress,
                            "cat <(grep 'cpu ' /proc/stat) <(sleep 1 && grep 'cpu ' /proc/stat) | awk -v RS=\"\" '{print (\$13-\$2+\$15-\$4)*100/(\$13-\$2+\$15-\$4+\$16-\$5)}'"

                        )
                    }
                    validConnection.storedCommand?.let {
                        val CustomCommandRun = async {
                            NetworkUtils.executeRemoteCommand(
                                validConnection.username,
                                validConnection.password,
                                validConnection.ipAddress,
                                it
                            )

                        }
                        resultsObject.customCommand = CustomCommandRun.await()
                    }

                    resultsObject.cpuUsage = CpuUsage.await()
                    resultsObject.diskSpace = DiskSpace.await()
                    resultsObject.memUsage = MemUsage.await()
                    resultsObject.loggedInUsers = LoggedInUsers.await()
                    resultsObject.ipAddress = validConnection.ipAddress
                    resultsObject.username = validConnection.username
                    resultsObject.password = validConnection.password
                    Log.d(TAG, "runPiCommands: $resultsObject")

                    // store result in DB
                    dao.insertValidConnection(
                        ValidConnection(
                            ipAddress = validConnection.ipAddress,
                            password = validConnection.password,
                            username = validConnection.username,
                            storedCommand = validConnection.storedCommand
                        )
                    )
                    commandResult.resume(Resource.Success(resultsObject))
                }
            }
        }


    }

    fun restartButtonClick(
        ipaddress: String,
        username: String,
        password: String,
        scope: CoroutineScope
    ) {
        scope.launch(Dispatchers.IO) {

            //pingtest
            val pingtest = async {
                isPortOpen(
                    ipaddress,
                    22,
                    3000
                )
            }
            Log.d("pingtest", pingtest.await().toString() + ipaddress)

            if (!pingtest.await()) {
                _powerOffOrRestartMessage.postValue(Event("Connection Failure Please Retry.."))

            } else {
                // run command

                val testcommand = async {
                    executeRemoteCommand(
                        username,
                        password,
                        ipaddress, "echo hello"
                    )
                }

                //Log.d("testcommand", testcommand.await())

                if (!testcommand.await().contains("hello")) {

                    _powerOffOrRestartMessage.postValue(Event("Device Session failure, Please confirm username and password"))

                } else {

                    //run command

                    val RestartCommand = async {
                        executeRemoteCommand(
                            username,
                            password,
                            ipaddress, "sudo systemctl start reboot.target"
                        )
                    }

                    _powerOffOrRestartMessage.postValue(Event("Your device is now rebooting...."))

                }
            }

        }
    }

    fun powerOffButtonClicked(
        username: String,
        password: String,
        IPAddress: String,
        scope: CoroutineScope
    ) {
        scope.launch(Dispatchers.IO) {

            //pingtest
            val pingtest = async {
                isPortOpen(
                    IPAddress.toString(),
                    22,
                    3000
                )
            }
            //Log.d("pingtest", pingtest.await())

            if (!pingtest.await()) {
                _powerOffOrRestartMessage.postValue(Event("Connection Failure Please Retry.."))

            } else {
                // run command

                val testcommand = async {
                    executeRemoteCommand(
                        username,
                        password,
                        IPAddress, "echo hello"
                    )
                }

                //Log.d("testcommand", testcommand.await())

                if (!testcommand.await().contains("hello")) {
                    _powerOffOrRestartMessage.postValue(Event("Device Session failure, Please confirm username and password"))

                } else {

                    //run command

                    val ShutdownCommand = async {
                        executeRemoteCommand(
                            username,
                            password,
                            IPAddress, "sudo shutdown -P now"
                        )
                    }
                    _powerOffOrRestartMessage.postValue(Event("Your device is now shutting down...."))

                }
            }
        }
    }

    // DB Methods
    fun getAllValidConnections(): LiveData<List<ValidConnection>> {
        return dao.getAllValidConnections()
    }

    suspend fun getValidConnection(ipAddress: String): ValidConnection? {
        return dao.getSpecificValidConnection(ipAddress = ipAddress)
    }

    suspend fun deleteIndividualValidConnection(validConnection: ValidConnection) {
        dao.deleteSpecificValidConnection(validConnection)
    }

    suspend fun deleteAllValidConnections() {
        dao.deleteAllValidConnections()
    }

}

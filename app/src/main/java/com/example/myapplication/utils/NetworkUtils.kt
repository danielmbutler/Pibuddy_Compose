package com.example.myapplication.utils

import android.util.Log
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import org.apache.commons.net.util.SubnetUtils
import java.io.ByteArrayOutputStream
import java.net.*
import java.util.*
import kotlin.concurrent.schedule

/*
Networking Utility functions, validating IP Address, testing ports, and executing remote commands over ssh
 */

object NetworkUtils {

    private const val TAG = "NetworkUtils"
     fun validate(ip: String): Boolean {
        val PATTERN =
            "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN.toRegex());
    }

    fun isPortOpen(ip: String, port: Int, timeout: Int): Boolean {

// validate if IP is properly formated

        val validationResult = validate(ip)

        if (!validationResult) {
            Log.d(TAG, "isPortOpen: validation result : $validationResult")
            return false
        } else {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ip, port), timeout)
                socket.close()
                //Log.d(TAG, "isPortOpen: socketattempt")
                return true

            } catch (ce: ConnectException) {
                ce.printStackTrace()
                Log.d(TAG, "Connect Exception:${ce.message}")
                return false
            } catch (ce: SocketTimeoutException) {
                Log.d(TAG, "Timeout Exception:${ce.message}")
                return false
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d(TAG, " Exception:${ex.message}")
                return false
            }
        }
    }

    suspend fun executeRemoteCommand(
        username: String,
        password: String,
        hostname: String,
        command: String,
        port: Int = 22
    ): String {
        var session: Session? = null
        var channel: ChannelExec? = null
        try {
            session = JSch().getSession(username.toString(), hostname.toString(), port)
            session.setPassword(password.toString())
            session.setConfig("StrictHostKeyChecking", "no")
            session.timeout = 15000
            session.connect()

            channel = session.openChannel("exec") as ChannelExec?
            channel!!.setCommand(command)

            val responseStream = ByteArrayOutputStream()

            channel.outputStream = responseStream
            channel.connect(15000) //set session timeout


            while (channel.isConnected) {
                Thread.sleep(100)
                Timer().schedule(15000) {
                    channel.disconnect() //disconnect channel if command output lasts longer than 15secs
                }
            }


            val responseString = String(responseStream.toByteArray())
            return (responseString)
        } catch (ce: JSchException) {


            return "error - Please check Username/Password"

        } finally {
            if (session != null) {
                session.disconnect()
            }
            channel?.disconnect()
        }
    }

    fun getIPRange(CIDRAddress: String): Array<String> {

        //ping scan test

        val utils = SubnetUtils(CIDRAddress)
        val allIps: Array<String> = utils.info.allAddresses
        //appIps will contain all the ip address in the subnet
        return allIps

    }
}






package com.example.myapplication.persistence

import androidx.room.*
import com.example.myapplication.models.ValidConnection

@Dao
interface ConnectionsDao {

    // get all valid connections
    @Query("SELECT * FROM `valid connections`")
    fun getAllValidConnections() : List<ValidConnection>

    // get specific valid connection
    @Query("SELECT * FROM `Valid Connections` WHERE ipAddress == :ipAddress")
    fun getSpecificValidConnection(ipAddress: String) : ValidConnection

    //Delete
    @Delete()
    fun deleteSpecificValidConnection(validConnection: ValidConnection)

    //Delete
    @Query("DELETE FROM `Valid Connections`")
    fun deleteAllValidConnections()

    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertValidConnection(validConnection: ValidConnection)


}
package com.example.piBuddyCompose.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.piBuddyCompose.models.ValidConnection

@Dao
interface ConnectionsDao {

    // get all valid connections
    @Query("SELECT * FROM `valid connections`")
    fun getAllValidConnections() : LiveData<List<ValidConnection>>

    // get specific valid connection
    @Query("SELECT * FROM `Valid Connections` WHERE ipAddress == :ipAddress")
    suspend fun getSpecificValidConnection(ipAddress: String) : ValidConnection?

    //Delete
    @Delete()
    suspend fun deleteSpecificValidConnection(validConnection: ValidConnection)

    //Delete
    @Query("DELETE FROM `Valid Connections`")
    suspend fun deleteAllValidConnections()

    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertValidConnection(validConnection: ValidConnection)


}
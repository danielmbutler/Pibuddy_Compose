package com.example.piBuddyCompose.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.piBuddyCompose.models.ValidConnection

@Database(entities = [ValidConnection::class], version = 1, exportSchema = false)
abstract class ConnectionDatabase : RoomDatabase() {

    abstract fun getDao(): ConnectionsDao

    companion object {

        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ConnectionDatabase? = null

        fun getDatabase(context: Context): ConnectionDatabase {

            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConnectionDatabase::class.java,
                    "cloud_Status"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
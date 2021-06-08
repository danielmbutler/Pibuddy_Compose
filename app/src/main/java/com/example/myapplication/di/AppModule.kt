package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.persistence.ConnectionDatabase
import com.example.myapplication.persistence.ConnectionsDao
import com.example.myapplication.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// module to provide dependencies for hilt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDao(@ApplicationContext appContext: Context) : ConnectionsDao {
        return ConnectionDatabase.getDatabase(appContext).getDao()
    }


    @Singleton
    @Provides
    fun provideRepository( dao: ConnectionsDao) = Repository(dao)
}
package com.example.piBuddyCompose.di

import android.content.Context
import com.example.piBuddyCompose.persistence.ConnectionDatabase
import com.example.piBuddyCompose.persistence.ConnectionsDao
import com.example.piBuddyCompose.repository.RepositoryImpl
import com.example.piBuddyCompose.repository.defaultRepository
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
    fun provideRepository( dao: ConnectionsDao): defaultRepository = RepositoryImpl(dao)
}
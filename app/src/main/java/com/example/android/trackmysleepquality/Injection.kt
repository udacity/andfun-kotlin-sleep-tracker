package com.example.android.trackmysleepquality

/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Application
import android.content.Context
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.sleepquality.SleepQualityViewModelFactory
import com.example.android.trackmysleepquality.sleeptracker.SleepTrackerViewModelFactory

/**
 * Enables injection of data sources.
 */
object Injection {

    /**
     * The purpose of this Injection is to separate getting a reference to the database from
     * the ViewModel. The ViewModel doesn't actually need to know about the database as it
     * interacts with the database via it's DAO only.
     *
     * A major benefit of this is architectural separation that allows us to write tests.
     * (Testing is not covered in this lesson.)
     *
     * Here is how it works:
     *    1. The Fragment asks for a ViewModelFactory, which it will use to get a reference to
     *       the ViewModel (which the ViewModelProvider will get and create if necessary.
     *    2. provide...ViewModelFactory gets a reference to the data source
     *       with provideUserDataSource.
     *    3. We supply this to the ...ViewModelFactory, that extracts the DAO it needs and
     *       creates the ViewModel for the Fragment.
     */

    private fun provideUserDataSource(context: Context): SleepDatabaseDao {
        val database = SleepDatabase.getInstance(context)
        return database.sleepDatabaseDao()
    }

    fun provideSleepQualityViewModelFactory(
            sleepNightKey: Long,
            context: Context): SleepQualityViewModelFactory {
        val dataSource = provideUserDataSource(context)
        return SleepQualityViewModelFactory(sleepNightKey, dataSource)
    }

    fun provideSleepTrackerViewModelFactory(
            application: Application): SleepTrackerViewModelFactory {
        val dataSource = provideUserDataSource(application)
        return SleepTrackerViewModelFactory(dataSource, application)
    }
}
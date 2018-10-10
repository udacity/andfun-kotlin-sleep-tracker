package com.example.android.trackmysleepquality

import android.content.Context

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

import com.example.android.trackmysleepquality.sleepquality.SleepQualityViewModelFactory
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

/**
 * Enables injection of data sources.
 */
object Injection {

    fun provideUserDataSource(context: Context): SleepDatabaseDao {
        val database = SleepDatabase.getInstance(context)
        return database.sleepQualityDao()
    }

    fun provideViewModelFactory(sleeNightKey: Long, context: Context): SleepQualityViewModelFactory {
        val dataSource = provideUserDataSource(context)
        return SleepQualityViewModelFactory(sleeNightKey, dataSource)
    }
}
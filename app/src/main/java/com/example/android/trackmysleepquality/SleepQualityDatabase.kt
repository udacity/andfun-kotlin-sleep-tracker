/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * A database that stores SleepNight information.
 * And a global method to get access to the database.
 */

@Database(entities = [SleepNight::class], version = 9)
abstract class SleepQualityDatabase : RoomDatabase() {
    abstract val sleepQualityDao: SleepQualityDao
}

/**
 * Variable to hold a reference to the database.
 * If this is set, we return this when the database is requested,
 * otherwise, we build the database
 */
private lateinit var INSTANCE: SleepQualityDatabase

/**
 * Get access to the SleepQualityDatabase singleton.
 *
 * Using a singleton prevents having multiple instances of the database opened
 * at the same time, which would be a bad thing.
 *
 * @param context Application context
 */
fun getDatabase(context: Context): SleepQualityDatabase {
    synchronized(SleepQualityDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    SleepQualityDatabase::class.java, "nights")
                    // This means, every time we change the version number, we delete and
                    // recreate the database, which is what we want during development.
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
    return INSTANCE
}

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

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.experimental.CoroutineScope

/**
 * A database that stores SleepNight information.
 * And a global method to get access to the database.
 */

// TODO: This is copied from Florina's reviewed Room Kotlin codelab. Add comments.

@Database(entities = [SleepNight::class], version = 12, exportSchema = false)
abstract class SleepQualityDatabase : RoomDatabase() {

    abstract fun sleepQualityDao() : SleepQualityDao

    companion object {
        @Volatile
        private var INSTANCE: SleepQualityDatabase? = null
        fun getDatabase(
                context: Context,
                scope: CoroutineScope
        ): SleepQualityDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepQualityDatabase::class.java,
                        "night"
                )
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        // Migration is not part of this codelab.
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}




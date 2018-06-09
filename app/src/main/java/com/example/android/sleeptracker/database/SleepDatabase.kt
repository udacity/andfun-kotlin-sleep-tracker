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

package com.example.android.sleeptracker.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = [SleepEntry::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class SleepDatabase : RoomDatabase() {

    companion object {
        private var sInstance: SleepDatabase? = null
        private const val DATABASE_NAME = "sleep.db"
        private val LOCK: Any = Any()

        fun getInstance(context: Context): SleepDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = Room.databaseBuilder(context.applicationContext,
                            SleepDatabase::class.java, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return sInstance
        }
    }

    abstract fun sleepDao(): SleepDao
}
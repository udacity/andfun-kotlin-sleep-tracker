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

/**
 * A database that stores SleepNight information.
 * And a global method to get access to the database.
 *
 * This pattern is pretty much the same for any database,
 * so you can reuse it.
 */
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepQualityDatabase : RoomDatabase() {

    /**
     * Connect the database to the DAO.
     */
    abstract fun sleepQualityDao(): SleepQualityDao

    /**
     * Define a companion object, this allows us to add functions on the SleepQualityDatabase class.
     *
     * For example, clients can call `SleepQualityDatabase.getDatabase(context)` to instantiate
     * a new SleepQualityDatabase.
     */
    companion object {
        /**
         * INSTANCE will keep a reference to any database returned via getDatabase.
         *
         * This will help us avoid repeatedly initializing the database, which is expensive.
         */
        @Volatile
        private var INSTANCE: SleepQualityDatabase? = null

        /**
         * Helper function to get the database.
         *
         * If a database has already been retrieved, the previous database will be returned.
         * Otherwise, create a new database.
         *
         * This function is threadsafe, and callers should cache the result for multiple database
         * calls to avoid overhead.
         *
         * This is an example of a simple Singleton pattern that takes another Singleton as an
         * argument in Kotlin.
         *
         * To learn more about Singleton read the wikipedia article:
         * https://en.wikipedia.org/wiki/Singleton_pattern
         *
         * @param Context The application context Singleton, used to get access to the filesystem.
         */
        fun getDatabase(
                context: Context
        ): SleepQualityDatabase {
            // multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a
            // time.
            synchronized(this) {

                // copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
                // smart cast is only available to local variables.
                var instance = INSTANCE

                // if instance is `null` make a new database instance
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepQualityDatabase::class.java,
                            "sleep_history_database"
                    )
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            // Migration is not part of this lesson.
                            .fallbackToDestructiveMigration()
                            .build()
                    // assign INSTANCE to the newly created database
                    INSTANCE = instance
                }

                // return instance; smart cast to be non-null
                return instance
            }
        }
    }
}

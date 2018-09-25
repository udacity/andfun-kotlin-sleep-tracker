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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


/**
 * Defines methods for using the SleepNight class with Room.
 */

@Dao
interface SleepQualityDao {

    @Insert
    fun insert(night: SleepNight)

    // When updating a row with a value already set in a column,
    // replace the old value with the new one.
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(night: SleepNight)

    // Select and return the row that matches the supplied start time, which is our key.
    @Query("SELECT * from daily_sleep_quality_table WHERE start_time_milli = :key ")
    fun get(key: Long): SleepNight

    // Delete all values from the table.
    // This does not delete the table, only its contents.
    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    // Select and return all rows in the table,
    // sorted by start time in descending order.
    @Query("SELECT * from daily_sleep_quality_table ORDER BY start_time_milli DESC")
    fun getAllNights(): LiveData<List<SleepNight>>
}
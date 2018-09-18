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

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Defines methods for using the SleepNight class with Room.
 */

@Dao
interface SleepQualityDao {

    @Insert
    fun insert(night: SleepNight)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(night: SleepNight)

    @Query("SELECT * from daily_sleep_quality_table WHERE start_time_milli = :key ")
    fun get(key: Long): SleepNight

    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    @Query("SELECT * from daily_sleep_quality_table ORDER BY start_time_milli DESC")
    fun getAllNights(): LiveData<List<SleepNight>>
}
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

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class that represents one night's sleep through start and end times, and the sleep quality.
 * The time is collected as milliseconds for calculations and accuracy, and as a date-time string
 * for display. Since the date/time don't change once recorded, it is much cheaper to do the
 * conversion once and store the information in the database.
 * The quality rating is numerical, and can be surfaced to the user in any way desired.
 */

@Entity(tableName = "daily_sleep_quality_table")
data class SleepNight(
        @PrimaryKey @NonNull @ColumnInfo(name = "start_time_milli")
        val startTimeMilli: Long = System.currentTimeMillis()) {


    // TODO: Add comment on why we do it this way.

    @ColumnInfo(name = "start_time")
    var startDateTime: String = makeDateTimeString()

    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli: Long = 0

    @ColumnInfo(name = "end_time")
    var endDateTime: String = ""

    @ColumnInfo(name = "quality_rating")
    var sleepQualty: Int = 4
}

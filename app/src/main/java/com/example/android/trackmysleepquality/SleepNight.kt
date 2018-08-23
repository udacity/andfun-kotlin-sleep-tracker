package com.example.android.trackmysleepquality

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "daily_sleep_quality_table")
data class SleepNight(

        @PrimaryKey @NonNull @ColumnInfo(name ="start_time_milli")
        val startTimeMilli: Long = System.currentTimeMillis()) {

    @NonNull @ColumnInfo(name ="start_time")
    var startDateTime: String = makeDateTimeString()

    @NonNull
    @ColumnInfo(name ="end_time_milli")
    var endTimeMilli: Long = 0

    @NonNull
    @ColumnInfo(name ="end_time")
    var endDateTime: String = ""

    @NonNull
    @ColumnInfo(name ="quality_rating")
    var sleepQualty: Int = 4
}

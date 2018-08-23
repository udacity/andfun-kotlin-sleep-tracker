package com.example.android.trackmysleepquality

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SleepQualityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(night: SleepNight)

    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    @Query("SELECT * from daily_sleep_quality_table ORDER BY start_time_milli DESC")
    fun getAllNights(): LiveData<List<SleepNight>>
}
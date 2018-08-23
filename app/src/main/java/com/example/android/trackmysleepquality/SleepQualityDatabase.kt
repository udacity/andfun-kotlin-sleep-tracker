package com.example.android.trackmysleepquality

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


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

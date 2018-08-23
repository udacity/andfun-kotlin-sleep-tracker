package com.example.android.trackmysleepquality

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newFixedThreadPoolContext

class SleepQualityViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)

    var nights = database.sleepQualityDao.getAllNights()

    // This is how the docs say to do it.
    // https://developer.android.com/training/basics/fragments/communicating
    lateinit var tonightCache: SleepNight

    private val backgroundPool = newFixedThreadPoolContext(2, "background-coroutines")
    private fun launchBackground(block: suspend CoroutineScope.() -> Unit): Job {
        return launch(backgroundPool, block = block)
    }

    fun insert(night: SleepNight) {
        launchBackground {
            database.sleepQualityDao.insert(night)
        }
    }

    fun clear() {
        launchBackground {
        database.sleepQualityDao.clear()
        }
    }
}
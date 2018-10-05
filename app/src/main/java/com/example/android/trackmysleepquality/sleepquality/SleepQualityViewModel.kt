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

package com.example.android.trackmysleepquality.sleepquality

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
//import com.example.android.trackmysleepquality.Event
import com.example.android.trackmysleepquality.database.SleepQualityDatabase
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

/**
 * ViewModel for SleepQualityFragment.
 */
class SleepQualityViewModel(
        sleepNightKey: Long = 0L,
        application: Application ) : AndroidViewModel(application) {

    /**  Database related variables. */

    // Our trusty Room database.
    val database = SleepQualityDatabase.getDatabase(application)


    // The key of the current night we are working on.
    // Set when we create the fragment from the fragment arguments.
    private var sleepNightKey = sleepNightKey

    /** Coroutine setup variables */

    // We need a job for our coroutines. The job has references to all coroutines.
    private val viewModelJob = Job()

    // We need a scope to run in, because we don't want to run this on the
    // UI thread. IO is a threadpool for running operations that are not directly
    // UI related.
    private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    // Variable that tells the Event whether it should navigate to SleepTrackerFragment.
    private val _navigateToSleepTrackerEvent = MutableLiveData<Boolean>()
    val navigateToSleepTrackerEvent: LiveData<Boolean>
        get() = _navigateToSleepTrackerEvent
    fun doneNavigating() {_navigateToSleepTrackerEvent.value = false}


    /**
     * Cancel all coroutines when the ViewModel is cleared, so that we
     * don't end up with dangling coroutines. onCleared() gets called when the
     * ViewModel is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun setSleepQualtiy(quality: Int) {
        // Launch a coroutine to update the night in the database.
        scope.launch {
            val tonight = database.sleepQualityDao().get(sleepNightKey)
            tonight.sleepQuality = quality
            database.sleepQualityDao().update(tonight)
        }
    }

    /**
     * Sets the sleep quality and updates the database.
     * Then navigates back to the SleepTrackerFragment.
     */
    fun onSetSleepQuality(quality: Int) {

        setSleepQualtiy(quality)

        // Navigate back to the SleepTracker Fragment using the Event pattern.
        // Setting this variable to true will alert the observer and trigger navigation.
        _navigateToSleepTrackerEvent.value = true
    }
}

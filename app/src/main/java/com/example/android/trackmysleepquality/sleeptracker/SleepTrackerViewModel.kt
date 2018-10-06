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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.text.Spanned
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.database.SleepQualityDatabase.Companion.getDatabase
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlin.coroutines.experimental.CoroutineContext

/**
 * ViewModel for SleepTrackerFragment.
 *
 * AndroidViewModel provides us with an Application Context that we need to
 * get the database.
 */
class SleepTrackerViewModel(application: Application) : AndroidViewModel(application) {

    /**  Database-related variables. */

    val database = getDatabase(application)

    lateinit var tonight: SleepNight
    private val nights: LiveData<List<SleepNight>>
    // Converted nights to string for displaying
    val nightsString: LiveData<Spanned>

    /** Coroutine variables */

    // We need a job for our coroutines.
    // The job has references to all coroutines.
    private var parentJob = Job()

    // Scope for our coroutines.
    // Since we don't need to run on the UI thread, use the IO thread pool.
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    /**
     * Using backing properties for button states.
     * This is a pattern you can copy and adapt.
     * To keep others from changing this MutableLiveData, we make it private and
     * expose a read-only LiveData to the outside world.
     */

    private val _startButtonVisibilityState = MutableLiveData<Boolean>()
    val startButtonVisibilityState: LiveData<Boolean>
        get() = _startButtonVisibilityState

    private val _stopButtonVisibilityState = MutableLiveData<Boolean>()
    val stopButtonVisibilityState: LiveData<Boolean>
        get() = _stopButtonVisibilityState

    private val _clearButtonVisibilityState = MutableLiveData<Boolean>()
    val clearButtonVisibilityState: LiveData<Boolean>
        get() = _clearButtonVisibilityState


    // Variable that specifies whether the Fragment should show the toast.
    private var _showToastEvent = MutableLiveData<Boolean>()
    val showToastEvent: LiveData<Boolean>
        get() = _showToastEvent

    fun doneShowingToast() {
        _showToastEvent.value = false
    }

    // Variable that tells the Fragment whether it should navigate to SleepQualityFragment.
    private val _navigateToSleepQualityEvent = MutableLiveData<Boolean>()
    val navigateToSleepQualityEvent: LiveData<Boolean>
        get() = _navigateToSleepQualityEvent

    fun doneNavigating() {
        _navigateToSleepQualityEvent.value = false
    }

    // WHY is nights.value NULL here???
    // Don't return LiveData if you want the value sync.
    //bLiveData is to watch the data and distribute it to the observers.
    // It won't calculate the value until an active observer is added.
    // So it's not available in init of this ViewModel.
    // And we have to run this in a coroutine.
   // fun getOldNight(): Boolean {
      //  runBlocking {
      //      tonight = database.sleepQualityDao().getTonight()
      //  }
      //  return (tonight.startTimeMilli == tonight.endTimeMilli)
      //          && (tonight.endTimeMilli - tonight.startTimeMilli < 57600000)
      //  return true
   // }

    init {
        // Get all the nights from the database and cache them.
        nights = database.sleepQualityDao().getAllNights()
        nightsString = Transformations.map(nights, { nights -> formatNights(nights, getApplication()) })

        // TODO: HELP How do I do this?????
        // Handling the case of the stopped app or forgotten recording,
        // the start and end times will be the same.
        // And if less than 16 hours have passed since start,
        // we assume we are continuing, otherwise, we assume a new recording.
      //  if (getOldNight()) { // Continue with the previous night
            // Set the initial button visibilities if we are continuing.
      //      _startButtonVisibilityState.value = false
      //      _stopButtonVisibilityState.value = true
       //     _clearButtonVisibilityState.value = true
      //  } else {
            // Set the initial button visibilities if we are starting.
            _startButtonVisibilityState.value = true
            _stopButtonVisibilityState.value = false
            _clearButtonVisibilityState.value = true
       // }
    }

    /**
     * Launches a coroutine to insert the supplied night into the database.
     */
    fun insert(night: SleepNight) = scope.launch {
        database.sleepQualityDao().insert(night)
    }

    /**
     * Launches a coroutine to update the provided night.
     */
    fun update(night: SleepNight) = scope.launch {
        database.sleepQualityDao().update(night)
    }

    /**
     * Launchs a coroutine to clear the database table.
     */
    fun clear() = scope.launch {
        database.sleepQualityDao().clear()
    }

    /**
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.
     */
    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    /**
     * Executes when the START button is clicked.
     */
    fun onStart() {
        _startButtonVisibilityState.value = false
        _stopButtonVisibilityState.value = true
        _clearButtonVisibilityState.value = false

        // Create a new night, which captures the current time,
        // and insert it into the database.
        tonight = SleepNight()
        insert(tonight)
    }

    /**
     * Executes when the STOP button is clicked.
     */
    fun onStop() {
        _startButtonVisibilityState.value = true
        _stopButtonVisibilityState.value = false
        _clearButtonVisibilityState.value = true

        // Update the night in the database to add the end time.
        tonight.endTimeMilli = System.currentTimeMillis()
        update(tonight)

        // Set state to navigate to the SleepQualityFragment.
        _navigateToSleepQualityEvent.value = true
    }

    /**
     * Executes when the CLEAR button is clicked.
     */
    fun onClear() {
        _startButtonVisibilityState.value = true
        _stopButtonVisibilityState.value = false
        _clearButtonVisibilityState.value = false

        // Clear the database table.
        clear()

        // Show a toast because it's friendly.
        _showToastEvent.value = true
    }
}

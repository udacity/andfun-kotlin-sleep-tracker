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
import android.content.Context
import android.text.Html
import android.text.Spanned
import androidx.core.text.toSpanned
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
//import com.example.android.trackmysleepquality.Event
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertLongToDateString
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.database.SleepQualityDatabase.Companion.getDatabase
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(application: Application) : AndroidViewModel(application) {

    /**  Database related variables. */

    val database = getDatabase(application)

    lateinit var tonight: SleepNight
    var nights: LiveData<List<SleepNight>>
    lateinit var nightsString: LiveData<Spanned>

    /** Coroutine setup variables */

    // We need a job for our coroutines. The job has references to all coroutines.
    private var parentJob = Job()

    // Create the scope for our coroutines.
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


    /** Using events (Event class + LiveData + Observers in Fragment)
     * to trigger UI actions in the Fragment.
     */

    // Variable that tells the Event whether it should show the toast.
    private var _showToastEvent = MutableLiveData<Boolean>()
    val showToastEvent: LiveData<Boolean>
        get() = _showToastEvent
    fun doneShowingToast() {_showToastEvent.value = false}

    // Variable that tells the Event whether it should navigate to SleepQualityFragment.
    private val _navigateToSleepQualityEvent = MutableLiveData<Boolean>()
    val navigateToSleepQualityEvent: LiveData<Boolean>
        get() = _navigateToSleepQualityEvent
    fun doneNavigating() {_navigateToSleepQualityEvent.value = false}


    /** Initialization */

    init {
        // Get all the nights from the database and cache them.
        nights = database.sleepQualityDao().getAllNights()
        nightsString =  Transformations.map(nights, {nights -> formatNights(nights, getApplication())})

        // Set the initial button visibilities.
        _startButtonVisibilityState.value = true
        _stopButtonVisibilityState.value = false
        _clearButtonVisibilityState.value = true
    }

    /** Functions that launch the coroutines for database operations. */

    // Launch a coroutine to insert the supplied night into the database.
    fun insert(night: SleepNight) = scope.launch {
        database.sleepQualityDao().insert(night)
    }

    // Launch a coroutine to update the provided night.
    fun update(night: SleepNight) = scope.launch {
        database.sleepQualityDao().update(night)
    }

    // Launch a coroutine to clear the database table.
    fun clear() = scope.launch {
        database.sleepQualityDao().clear()
    }

    // Called when the ViewModel is dismantled. At this point, we want to cancel all coroutines;
    // otherwise we end up with processes that have nowhere to return to using memory and resources.
    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    /** Methods for buttons presses */

    // Called when the START button is clicked.
    fun onStart() {
        // Change button visibility.
        _startButtonVisibilityState.value = false
        _stopButtonVisibilityState.value = true
        _clearButtonVisibilityState.value = false

        // Create a new night, which captures the current time, and insert it into the database.
        tonight = SleepNight()
        insert(tonight)
    }

    // Called when the STOP button is clicked.
    fun onStop() {
        // Change button visibility.
        _startButtonVisibilityState.value = true
        _stopButtonVisibilityState.value = false
        _clearButtonVisibilityState.value = true

        // Update the night in the database to add the end time.
        tonight.endTimeMilli = System.currentTimeMillis()
        update(tonight)

        // Navigate to the SleepQualityFragment to collect a quality rating.
        _navigateToSleepQualityEvent.value = true
    }

    // Called when the CLEAR button is clicked.
    fun onClear() {

        // Set the button visibility.
        _startButtonVisibilityState.value = true
        _stopButtonVisibilityState.value = false
        _clearButtonVisibilityState.value = false

        // Clear the database table.
        clear()

        // Show a toast because it's friendly to let users know.
        _showToastEvent.value = true
    }
}

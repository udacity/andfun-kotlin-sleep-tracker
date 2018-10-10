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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        dataSource: SleepDatabaseDao,
        application: Application) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via SleepDatabaseDao.
     */
    val database = dataSource

    /** Coroutine variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private var viewModelJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     *
     * Because we pass it [viewModelJob], any coroutine started in this uiScope can be cancelled
     * by calling `viewModelJob.cancel()`
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android. This is a sensible default because most coroutines started by
     * a [ViewModel] update the UI after performing some processing.
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // TODO unless you're doing the backing property thing, I say we don't put underscores in front
    private var _tonight = MutableLiveData<SleepNight?>()

    private val _nights = database.getAllNights()

    // TODO why is this a // comment while other use /** ? Pick one - I use // for variables
    // Converted _nights to string for displaying
    val nightsString = Transformations.map(_nights) { nights ->
        formatNights(nights, application.resources)
    }

    // TODO in the next three methods give "it" a name to make it clearer what's going on
    /**
     * True if and only if the start button should be shown.
     */
    val startButtonVisible = Transformations.map(_tonight) {
        null == it
    }

    /**
     * True if and only if the stop button should be shown.
     */
    val stopButtonVisible = Transformations.map(_tonight) {
        // TODO why is this != while the one above is ==? Yet the comments say the opposite
        null != it
    }

    /**
     * True if and only if the clear button should be shown.
     */
    val clearButtonVisible = Transformations.map(_nights) {
        it?.isNotEmpty()
    }


    // TODO made the boolean not a nullable
    /**
     * Request a toast by setting this value to true.
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private var _showToastEvent = MutableLiveData<Boolean>()

    // TODO you're showing a snackbar, not a toast
    /**
     * If this is true, immediately `show()` a toast and call `doneShowingToast()`.
     */
    val showToastEvent: LiveData<Boolean>
        get() = _showToastEvent

    // TODO name should be _eventNavigateToSleepQuality, and changed to non-nullable
    /**
     * Variable that tells the Fragment to navigate to a specific [SleepQualityFragment]
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()

    /**
     * If this is non-null, immediately navigate to [SleepQualityFragment] and call [doneNavigating]
     */
    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    /**
     * Call this immediately after calling `show()` on a toast.
     *
     * It will clear the toast request, so if the user rotates their phone it won't show a duplicate
     * toast.
     */
    fun doneShowingToast() {
        // TODO changed to false
        _showToastEvent.value = false
    }

    /**
     * Call this immediately after navigating to [SleepQualityFragment]
     *
     * It will clear the navigation request, so if the user rotates their phone it won't navigate
     * twice.
     */
    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        uiScope.launch {
            _tonight.value = getTonightFromDatabase()
        }
    }

    /**
     *  Handling the case of the stopped app or forgotten recording,
     *  the start and end times will be the same.
     */
    suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }


    suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    /**
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Executes when the START button is clicked.
     */
    fun onStart() {
        uiScope.launch {
            // Create a new night, which captures the current time,
            // and insert it into the database.
            val newNight = SleepNight()

            _tonight.value = newNight

            insert(newNight)
        }
    }

    // TODO can you add a comment about return@launch?
    /**
     * Executes when the STOP button is clicked.
     */
    fun onStop() {
        uiScope.launch {
            val oldNight = _tonight.value ?: return@launch

            // Update the night in the database to add the end time.
            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)

            // Set state to navigate to the SleepQualityFragment.
            _navigateToSleepQuality.value = oldNight
        }
    }

    /**
     * Executes when the CLEAR button is clicked.
     */
    fun onClear() {
        uiScope.launch {
            // Clear the database table.
            clear()

            // and clear _tonight since it's no longer in the database
            _tonight.value = null

            // TODO toast or snackbar?
            // Show a toast because it's friendly.
            _showToastEvent.value = true
        }
    }
}
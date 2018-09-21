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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.database.SleepQualityDatabase.Companion.getDatabase
import kotlinx.coroutines.experimental.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext

/**
 * ViewModel for SleepTrackerFragment.
 */

class SleepTrackerViewModel(application: Application) : AndroidViewModel(application) {


    private var parentJob = Job()

    // TODO: @Sean: Is this correct?
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    val database = getDatabase(application, scope)

    lateinit var tonight: SleepNight

    var nights: LiveData<List<SleepNight>>

    init {
        nights = database.sleepQualityDao().getAllNights()
    }

    fun insert(night: SleepNight) =
            scope.launch {
                database.sleepQualityDao().insert(night)
            }

    fun update(night: SleepNight) =
            scope.launch {
                database.sleepQualityDao().update(night)
            }

    fun clear() =
            scope.launch {
                database.sleepQualityDao().clear()
            }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    /** Methods for buttons presses **/

    fun onStart() {
        tonight = SleepNight()
        insert(tonight)
    }

    fun onStop() {
        tonight.endTimeMilli = System.currentTimeMillis()
        update(tonight)
    }
}

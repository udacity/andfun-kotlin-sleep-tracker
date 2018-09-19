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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.trackmysleepquality.SleepQualityDatabase.Companion.getDatabase
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.experimental.CoroutineContext

/**
 * ViewModel for SleepTrackerFragment.
 */

class SleepTrackerViewModel(application: Application) : AndroidViewModel(application) {

    //TODO: Add comments. Especially for coroutines.

    private var parentJob = Job()


    // TODO: @Sean: Is this correct?
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    val database = getDatabase(application, scope)

    lateinit var tonight: SleepNight

    //val allNights : List<SleepNight>
    var nights : LiveData<List<SleepNight>>

    init {
        //allNights = database.sleepQualityDao().getAllNights()
        nights = database.sleepQualityDao().getAllNights()
    }

    // TODO: This is not right, but I don't know how to make the magic happen.
    // That is, I can't get the data binding part to work.

    //private val _nights = MutableLiveData<List<SleepNight>>()
    //        val nights: LiveData<List<SleepNight>>
    //           get() = _nights

   // fun setNights() {
   //     _nights.value = allNights
   // }

    // Accessing the database

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

}

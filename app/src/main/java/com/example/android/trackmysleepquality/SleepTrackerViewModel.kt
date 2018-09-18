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
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

/**
 * ViewModel for SleepTrackerFragment.
 */

class SleepTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)

    var nights = database.sleepQualityDao.getAllNights()

    lateinit var tonight: SleepNight

    // TODO: The Style guide says not to use GlobalScope, so how do I set up a scope.
    //val scope = CoroutineScope(Dispatchers.IO)
    //scope.launch

    fun insert(night: SleepNight) =
            GlobalScope.launch {
                database.sleepQualityDao.insert(night)
            }

    fun update(night: SleepNight) =
            GlobalScope.launch {
                database.sleepQualityDao.update(night)
            }

    fun clear() =
            GlobalScope.launch {
                database.sleepQualityDao.clear()
            }

}

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
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.experimental.CoroutineContext

/**
 * ViewModel for SleepQualityFragment.
 */

class SleepQualityViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()

    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    val database = SleepQualityDatabase.getDatabase(application, scope)


    fun getNight(key: Long) = scope.async { database.sleepQualityDao().get(key) }

    fun get(key: Long): SleepNight = runBlocking {
        getNight(key).await()
    }

    fun setSleepQuality(sleepNightKey: Long, quality: Int) {
        var tonight = get(sleepNightKey)

        tonight.sleepQualty = quality

        scope.launch {
            database.sleepQualityDao().update(tonight)
        }
    }
}
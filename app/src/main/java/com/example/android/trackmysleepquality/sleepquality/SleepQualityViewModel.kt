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
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.database.SleepQualityDatabase
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

/**
 * ViewModel for SleepQualityFragment.
 */

class SleepQualityViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    val database = SleepQualityDatabase.getDatabase(application)

    // TODO: Use different pattern?
    fun getNight(key: Long) = scope.async { database.sleepQualityDao().get(key) }

    fun get(key: Long): SleepNight = runBlocking {
        getNight(key).await()
    }

    fun setSleepQuality(sleepNightKey: Long, viewId: Int) {

        var tonight = get(sleepNightKey)

        var quality = 3 // Easy default

        if (-1 != viewId) {
            when (viewId) {
                R.id.quality_zero_image -> quality = 0
                R.id.quality_one_image -> quality = 1
                R.id.quality_two_image -> quality = 2
                R.id.quality_three_image -> quality = 3
                R.id.quality_four_image -> quality = 4
                R.id.quality_five_image -> quality = 5
            }
        }
        tonight.sleepQuality = quality

        scope.launch {
            database.sleepQualityDao().update(tonight)
        }
    }
}
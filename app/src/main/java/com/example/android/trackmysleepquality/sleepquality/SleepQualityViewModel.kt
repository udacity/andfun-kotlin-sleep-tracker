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

    // We need a job for our coroutines
    private val viewModelJob = Job()

    // We need a scope to run in, because we don't want to run this on the
    // UI thread. IO is a threadpool for running operations that are not directly
    // UI related.
    private val scope = CoroutineScope(Dispatchers.IO +  viewModelJob)

    // Our trusty Room database.
    val database = SleepQualityDatabase.getDatabase(application)

    /**
     * Cancel all coroutines when the ViewModel is cleared, so that we
     * don't end up with dangling coroutines. onCleared() gets called when the
     * ViewModel is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    // TODO: I don't think this is right.
    // I am pretty sure this is not the way to do this, but I don't know
    // how to do it right (and keep it simple as this is the introduction of
    // coroutines, and there is already so much in this lesson. ]
    //
    // 1. How do I do this (without acrobatics) to not use async, which I think oyou
    //    say not to use.
    // 2. How do I write all of this as one function, or as some statements inside
    //    setSleepQuality?
    //
    fun getNight(key: Long) = scope.async { database.sleepQualityDao().get(key) }

    fun get(key: Long): SleepNight = runBlocking {
        getNight(key).await()
    }

    /**
     * 1. Get the Night that we need to update. We can't continue until we
     *    have that night, wo we need to wait for it.
     * 2. Determine the sleep quality. NOTE: This code will be moved into the
     *    fragment and the quality will be passed in.
     * 3. Launch a coroutine to update the database.
     */
    fun setSleepQuality(sleepNightKey: Long, viewId: Int) {

        val tonight = get(sleepNightKey)

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
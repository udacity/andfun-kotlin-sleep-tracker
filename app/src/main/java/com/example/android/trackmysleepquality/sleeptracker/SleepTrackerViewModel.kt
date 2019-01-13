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
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    //TODO (01) Declare Job() and cancel jobs in onCleared().

    //TODO (02) Define uiScope for coroutines.

    //TODO (03) Create a MutableLiveData variable tonight for one SleepNight.

    //TODO (04) Define a variable, nights. Then getAllNights() from the database
    //and assign to the nights variable.

    //TODO (05) In an init block, initializeTonight(), and implement it to launch a coroutine
    //to getTonightFromDatabase().

    //TODO (06) Implement getTonightFromDatabase()as a suspend function.

    //TODO (07) Implement the click handler for the Start button, onStartTracking(), using
    //coroutines. Define the suspend function insert(), to insert a new night into the database.

    //TODO (08) Create onStopTracking() for the Stop button with an update() suspend function.

    //TODO (09) For the Clear button, created onClear() with a clear() suspend function.

    //TODO (12) Transform nights into a nightsString using formatNights().

}


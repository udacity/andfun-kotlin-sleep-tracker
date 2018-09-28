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

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertLongToDateString
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

/**
 * Binding Adapter that takes the data stored in nights and converts it to an organized,
 * formatted, an human-pleasant string to display in a TextView.
 *
 * The parameters need to be nullable, because we can't guarantee that the value of "nights"
 * has been set from the coroutine when this adapter is first called. (Actually, it hasn't.)
 *
 * This Adapter, is triggered by this code:
 *     app:nights="@{sleepTrackerViewModel.nights}"
 * in the TextView of the fragment_sleep_tracker.xml
 */
@BindingAdapter("nights")
fun setNights(textView: TextView, nights: List<SleepNight>?) {

    if (nights != null) {
        val sb = StringBuilder()
        val context = textView.context
        sb.apply {
            // Get the context for the string resource from the passed in View.
            append(context.getString(R.string.title))
            nights.forEach {
                append("\n\n")
                append(context.getString(R.string.start_time))
                append("\t${convertLongToDateString(it.startTimeMilli)}\n")
                if (it.endTimeMilli != 0L) {
                    append(context.getString(R.string.end_time))
                    append("\t${convertLongToDateString(it.endTimeMilli)}\n")
                    append(context.getString(R.string.quality))
                    append("\t${convertNumericQualityToString(it.sleepQuality, context)}\n")
                    append(context.getString(R.string.hours_slept))
                    // Hours
                    append("\t ${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60 / 60}:")
                    // Minutes
                    append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60}:")
                    // Seconds
                    append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000}")
                }
            }
        }
        textView.text = sb.toString()
    }
}





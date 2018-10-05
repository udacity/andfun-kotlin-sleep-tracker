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
import android.content.Context
import android.text.Html
import android.text.Spanned
import androidx.core.text.toSpanned
import com.example.android.trackmysleepquality.database.SleepNight
import java.text.SimpleDateFormat

/**
 * These functions create a formatted string that can be set in a TextView.
 * Because we haven't learned about RecyclerView yet.
 * - Works with the Transformation.map
 * - Works with Binding Adapter
 * .... take your pick.
 */

fun convertNumericQualityToString(quality: Int, context: Application): String {
    var qualityString = "OK"
    when (quality) {
        0 -> qualityString = context.getString(R.string.zero_very_bad)
        1 -> qualityString = context.getString(R.string.one_poor)
        2 -> qualityString = context.getString(R.string.two_soso)
        3 -> qualityString = context.getString(R.string.three_ok)
        4 -> qualityString = context.getString(R.string.four_pretty_good)
        5 -> qualityString = context.getString(R.string.five_excellent)
    }
    return qualityString
}

fun _convertNumericQualityToString(quality: Int, context: Context): String {
    var qualityString = "OK"
    when (quality) {
        0 -> qualityString = context.getString(R.string.zero_very_bad)
        1 -> qualityString = context.getString(R.string.one_poor)
        2 -> qualityString = context.getString(R.string.two_soso)
        3 -> qualityString = context.getString(R.string.three_ok)
        4 -> qualityString = context.getString(R.string.four_pretty_good)
        5 -> qualityString = context.getString(R.string.five_excellent)
    }
    return qualityString
}

fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("EEEE MMM-dd-yyyy' Time: 'HH:mm")
            .format(systemTime).toString()
}

fun formatNights(nights: List<SleepNight>, context: Application) : Spanned {
    if (nights != null) {
        val sb = StringBuilder()
        sb.apply {
            // Get the context for the string resource from the passed in View.
            append(context.getString(R.string.title))
            nights.forEach {
                append("<br><br>")
                append(context.getString(R.string.start_time))
                append("\t${convertLongToDateString(it.startTimeMilli)}<br>")
                if (it.endTimeMilli != 0L) {
                    append(context.getString(R.string.end_time))
                    append("\t${convertLongToDateString(it.endTimeMilli)}<br>")
                    append(context.getString(R.string.quality))
                    append("\t${convertNumericQualityToString(it.sleepQuality, context)}<br>")
                    append(context.getString(R.string.hours_slept))
                    // Hours
                    append("\t ${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60 / 60}:")
                    // Minutes
                    append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60}:")
                    // Seconds
                    append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000}<br><br>")
                }
            }
        }
        // fromHtml is deprecated for target API without a flag, but since our minSDK is 19, we
        // can't use the newer version, which requires minSDK of 24
        //https://developer.android.com/reference/android/text/Html#fromHtml(java.lang.String,%20int)
        return Html.fromHtml(sb.toString())
    } else return "".toSpanned()
}


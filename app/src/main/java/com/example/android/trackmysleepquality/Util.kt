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

import android.content.Context
import java.text.SimpleDateFormat

fun convertNumericQualityToString(quality: Int, context: Context): String {
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


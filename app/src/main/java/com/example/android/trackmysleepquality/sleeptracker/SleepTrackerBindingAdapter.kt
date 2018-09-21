package com.example.android.trackmysleepquality.sleeptracker

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.trackmysleepquality.database.SleepNight
import java.text.SimpleDateFormat


// Param needs to be nullable because we can't guaranteed that nights has been set from the
// coroutine. (Actually, it usually hasn't.)
@BindingAdapter("nights")
fun setNights(textView: TextView, nights: List<SleepNight>?) {
    if (nights != null) {
        val sb = StringBuilder()
        sb.append("HERE IS YOUR SLEEP DATA\n\n")

       for (night in nights) {
           sb.append("\n\nStart:\t${convertLongToDateString(night.startTimeMilli)}\n")
           if (night.endTimeMilli != 0L) {
               sb.append("End:\t${convertLongToDateString(night.endTimeMilli)}\n")
               sb.append("Quality:\t${convertNumericQualityToString(night.sleepQualty)}\n")
               sb.append(
                       "Sleep Hours:Minutes:\t ${night.endTimeMilli.minus(night.startTimeMilli) / 360000} " +
                               ": ${night.endTimeMilli.minus(night.startTimeMilli) / 60000}\n\n")
           }

        }
        textView.text = sb.toString()
    }
}

fun convertLongToDateString(systemTime: Long) : String {
    return SimpleDateFormat("yyyy-MM-dd' Time: 'HH:mm")
            .format(systemTime).toString()
}

fun convertNumericQualityToString(quality: Int) : String {
    var qualityString = "OK"
    when (quality) {
        0 -> qualityString = "Very bad"
        1 -> qualityString = "Poor"
        2 -> qualityString = "Not so good"
        3 -> qualityString = "OK"
        4 -> qualityString = "Pretty good"
        5 -> qualityString = "Excellent"
    }
    return qualityString
}
package com.example.android.trackmysleepquality

import java.text.SimpleDateFormat
import java.util.*

fun makeDateTimeString() : String {
    var date = SimpleDateFormat( "yyyy-MM-dd' Time: 'HH:mm" )
            .format(Calendar.getInstance().getTime()).toString()
    return date
}
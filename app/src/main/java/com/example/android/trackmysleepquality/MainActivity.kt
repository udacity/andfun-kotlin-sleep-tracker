package com.example.android.trackmysleepquality

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.android.trackmysleepquality.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =
                DataBindingUtil.setContentView<ActivityMainBinding>(
                        this, R.layout.activity_main)
    }
}

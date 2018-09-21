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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import kotlinx.android.synthetic.main.fragment_sleep_tracker.*

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 * The Clear button will clear all data from the database.
 *
 */
class SleepTrackerFragment : Fragment() {

    private lateinit var sleepTrackerViewModel: SleepTrackerViewModel
    private lateinit var binding: FragmentSleepTrackerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        sleepTrackerViewModel =
                ViewModelProviders.of(activity!!).get(SleepTrackerViewModel::class.java)

        // To use the View Model with data binding, you have to explicitely
        // give the binding object a reference to it.
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        // Specify the current activity as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.setLifecycleOwner(this)
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            startButton.setOnClickListener { startSleep(it) }
            stopButton.setOnClickListener { stopSleep(it) }
            clearButton.setOnClickListener { clearSleep(it) }
        }
    }

    fun startSleep(view: View) {
        binding.startButton.isEnabled = false
        binding.stopButton.isEnabled = true
        binding.clearButton.isEnabled = false

        sleepTrackerViewModel.onStart()
    }

    fun stopSleep(view: View) {

        binding.startButton.isEnabled = true
        binding.stopButton.isEnabled = false
        binding.clearButton.isEnabled = true

        sleepTrackerViewModel.onStop()

        view.findNavController().navigate(
                SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(
                        sleepTrackerViewModel.tonight.startTimeMilli))
    }

    fun clearSleep (view: View) {
        binding.startButton.isEnabled = true
        binding.stopButton.isEnabled = false
        binding.clearButton.isEnabled = true

        sleepTrackerViewModel.clear()
        binding.textview.text = getString(R.string.cleared_message)
    }
}

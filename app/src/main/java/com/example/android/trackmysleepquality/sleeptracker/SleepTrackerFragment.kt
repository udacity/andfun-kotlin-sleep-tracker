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

import android.annotation.TargetApi
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 * The Clear button will clear all data from the database.
 */
class SleepTrackerFragment : Fragment() {

    // We need references to the ViewModel and the our Binding Object.
    private lateinit var sleepTrackerViewModel: SleepTrackerViewModel
    private lateinit var binding: FragmentSleepTrackerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        // Get a reference to the ViewModel associated with this fragment.
        sleepTrackerViewModel =
                ViewModelProviders.of(this).get(SleepTrackerViewModel::class.java)

        // DO NOT FORGET THIS!!!
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        // Specify the current activity as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.setLifecycleOwner(this)

        // Add an Observer on the Event for showing a Toast when the clear button is pressed.
        // The Event takes care of making sure the toast is only shown once, even if the device
        // has a configuration change.
        sleepTrackerViewModel.showToastEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                // Only proceed if the event has never been handled
                Toast.makeText(
                        context,
                        getString(R.string.cleared_message),
                        Toast.LENGTH_SHORT).show()
            }
        })

        // Add an Observer on the Event for Navigating when the Stop button is pressed.
        // The Event takes care of making sure the navigation is only called once.
        sleepTrackerViewModel.navigateToSleepQualityEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                // Only proceed if the event has never been handled
                binding.stopButton.findNavController().navigate(
                        SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(
                                sleepTrackerViewModel.tonight.startTimeMilli))
            }
        })

        return binding.root
    }
}

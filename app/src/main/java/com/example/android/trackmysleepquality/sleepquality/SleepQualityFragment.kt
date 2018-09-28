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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.databinding.FragmentSleepQualityBinding

/**
 * Fragment that displays a list of clickable icons,
 * each representing a sleep quality rating.
 * Once the user taps an icon, the quality is set in the current sleepNight
 * and the
 * database is updated.
 *
 */
class SleepQualityFragment : Fragment() {

    // Variables to hold references for our ViewModel and Binding Object.
    private lateinit var sleepQualityViewModel: SleepQualityViewModel
    private lateinit var binding: FragmentSleepQualityBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_quality, container, false)

        // Get a reference to the ViewModel associated with this fragment.
        sleepQualityViewModel =
                ViewModelProviders.of(this).get(SleepQualityViewModel::class.java)

        // DO NOT FORGET THIS!!!
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.sleepQualityViewModel = sleepQualityViewModel

        // Add an Observer on the Event for Navigating when a Quality button is pressed.
        // The Event takes care of making sure the navigation is only called once.
        sleepQualityViewModel.navigateToSleepTrackerEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                // Only proceed if the event has never been handled
                this.findNavController().navigate(
                        SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
            }
        })

        // We need to make this value available to the ViewModel.
        // TODO: Is this the correct way of doing this?
        // TODO: Would it be better to create an additional shared ViewModel to hold just this data?
        sleepQualityViewModel.sleepNightKey =
                SleepQualityFragmentArgs.fromBundle(arguments).sleepNightKey

        return binding.root
    }
}

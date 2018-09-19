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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 * The Clear button will clear all data from the database.
 *
 */
class SleepTrackerFragment : Fragment() {

    private lateinit var sleepTrackerViewModel: SleepTrackerViewModel
    private lateinit var allNights: List<SleepNight>
    private lateinit var binding: FragmentSleepTrackerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        sleepTrackerViewModel =
                ViewModelProviders.of(activity!!).get(SleepTrackerViewModel::class.java)

        // TODO: What's missing here to display the data?
        sleepTrackerViewModel.nights.observe(this, Observer<List<SleepNight>> { nights ->
            nights?.apply {
                allNights = nights
                showSleep(binding.textview)

            }
        })
        setClickListeners()

        // Specify the current activity as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.setLifecycleOwner(this)
        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            startButton.setOnClickListener { startSleep(it) }
            stopButton.setOnClickListener { stopSleep(it) }
            stopButton.isEnabled = false
            clearButton.setOnClickListener { clearData(it) }
        }
    }

    // TODO: Sean says to move the click handler work into the view model.
    private fun startSleep(view: View) {
        sleepTrackerViewModel.tonight = SleepNight()
        binding.apply {
            startButton.isEnabled = false
            stopButton.isEnabled = true
            clearButton.isEnabled = true
            clearButton.isEnabled = false
        }
        sleepTrackerViewModel.insert(sleepTrackerViewModel.tonight)
    }

    // TODO: Sean says to move the click handler work into the view model.
    private fun stopSleep(view: View) {
        binding.apply {
            startButton.isEnabled = true
            stopButton.isEnabled = false
            clearButton.isEnabled = true
        }
        sleepTrackerViewModel.tonight.endDateTime = makeDateTimeString()
        sleepTrackerViewModel.tonight.endTimeMilli = System.currentTimeMillis()
        sleepTrackerViewModel.update(sleepTrackerViewModel.tonight)

        view.findNavController().navigate(
                SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(
                                sleepTrackerViewModel.tonight.startTimeMilli))
    }

    // TODO: Is there a way to do this with LiveData + Data Binding?
    // TODO: ... in which case, I think we could use a map?
    private fun showSleep(view: View) {
        val sb = StringBuilder()
        sb.append(getString(R.string.sleep_data_title))

        for (night in allNights) {
            sb.append("Start:\t${night.startDateTime}\n")
            sb.append("End:\t${night.endDateTime}\n")
            sb.append(
                    "Hours / Minutes:\t ${night.endTimeMilli.minus(night.startTimeMilli) / 360000} " +
                            ": ${night.endTimeMilli.minus(night.startTimeMilli) / 60000}\n")
            sb.append("Quality:\t${night.sleepQualty}\n\n")
        }
        binding.textview.text = sb.toString()
    }

    private fun clearData(view: View) {
        sleepTrackerViewModel.clear()
        binding.textview.text = getString(R.string.cleared_message)
    }
}

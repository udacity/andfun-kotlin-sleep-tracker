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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
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

    private lateinit var sleepQualityViewModel: SleepQualityViewModel
    private lateinit var binding: FragmentSleepQualityBinding

    private var sleepNightKey = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // TODO(Dan): Does this go here or Lyla wonders onActivityCreated?
        sleepNightKey = SleepQualityFragmentArgs.fromBundle(arguments).sleepNightKey

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_quality, container, false)

        sleepQualityViewModel =
                ViewModelProviders.of(this).get(SleepQualityViewModel::class.java)

        setClickListeners()
        return binding.root
    }

    /**
     * Attaches listeners to all the views.
     */
    private fun setClickListeners() {
        val clickableViews: List<View> =
                listOf(binding.qualityOneImage, binding.qualityTwoImage, binding.qualityThreeImage,
                        binding.qualityFourImage, binding.qualityFiveImage, binding.qualityZeroImage)

        for (item in clickableViews) {
            item.setOnClickListener { setSleepQuality(it) }
        }
    }

    private fun setSleepQuality(view: View) {
       sleepQualityViewModel.setSleepQuality(sleepNightKey, view.id)

        view.findNavController().navigate(
                SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
    }
}

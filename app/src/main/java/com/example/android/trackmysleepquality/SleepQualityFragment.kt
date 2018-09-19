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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.android.trackmysleepquality.databinding.FragmentQualityChooserBinding

/**
 * Fragment that displays a list of clickable icons, each representing a sleep quality rating.
 * Once the user taps an icon, the quality is added to the current sleepNight and the
 * database is updated.
 *
 */
class SleepQualityFragment : Fragment() {

    private lateinit var sleepQualityViewModel: SleepQualityViewModel
    private lateinit var binding: FragmentQualityChooserBinding

    var sleepNightKey = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // TODO(Dan): Does this go here or Lyla wonders onActivityCreated?
        sleepNightKey = SleepQualityFragmentArgs.fromBundle(arguments).sleepNightKey

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_quality_chooser, container, false)

        // TODO: Add a comment that explains what this does, including activity!!
        sleepQualityViewModel =
                ViewModelProviders.of(activity!!).get(SleepQualityViewModel::class.java)


        setClickListeners()
        return binding.root
    }

    /**
     * Attaches listeners to all the views.
     */
    // TODO: Sean: Move to data binding lambdas
    // TODO: What does that mean?
    private fun setClickListeners() {
        val clickableViews: List<View> =
                listOf(binding.qualityOneImage, binding.qualityTwoImage, binding.qualityThreeImage,
                        binding.qualityFourImage, binding.qualityFiveImage, binding.qualityZeroImage)

        for (item in clickableViews) {
            item.setOnClickListener { setSleepQuality(it) }
        }
    }

    // TODO: So where should this work be done? Also in the ViewModel?
    private fun setSleepQuality(view: View) {
        var quality = 3 // Easy default

        if (-1 != view.id) {
            when (view.id) {
                R.id.quality_zero_image -> quality = 0
                R.id.quality_one_image -> quality = 1
                R.id.quality_two_image -> quality = 2
                R.id.quality_three_image -> quality = 3
                R.id.quality_four_image -> quality = 4
                R.id.quality_five_image -> quality = 5
            }
        }

       sleepQualityViewModel.setSleepQuality(sleepNightKey, quality)

        view.findNavController().navigate(
                SleepQualityFragmentDirections
                        .actionSleepQualityFragmentToSleepTrackerFragment())
    }
}

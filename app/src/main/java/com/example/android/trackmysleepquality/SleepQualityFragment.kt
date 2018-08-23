package com.example.android.trackmysleepquality


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.android.trackmysleepquality.databinding.FragmentQualityChooserBinding

/**
 * A simple [Fragment] subclass.
 *
 */
class SleepQualityFragment : Fragment() {

    private lateinit var sleepQualityViewModel: SleepQualityViewModel
    private lateinit var binding: FragmentQualityChooserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_quality_chooser, container, false)

        sleepQualityViewModel =
                ViewModelProviders.of(activity!!).get(SleepQualityViewModel::class.java)

        setClickListeners()
        return binding.root
    }

    /**
     * Attaches listeners to all the views.
     */
    fun setClickListeners() {
        val clickableViews: List<View> =
                listOf(binding.qualityOneImage, binding.qualityTwoImage, binding.qualityThreeImage,
                        binding.qualityFourImage, binding.qualityFiveImage, binding.qualityZeroImage)

        for (item in clickableViews) {
            item.setOnClickListener { setSleepQuality(it) }
        }
    }

    fun setSleepQuality (view: View){
        var quality = 3 // Easy default.
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

        sleepQualityViewModel.tonightCache.sleepQualty = quality
        sleepQualityViewModel.insert(sleepQualityViewModel.tonightCache)

        view.findNavController().navigate(
                SleepQualityFragmentDirections
                        .actionSleepQualityFragmentToSleepTrackerFragment())
    }
}

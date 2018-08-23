package com.example.android.trackmysleepquality


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import kotlinx.android.synthetic.main.fragment_sleep_tracker.*

/**
 * A simple [Fragment] subclass.
 *
 */
class SleepTrackerFragment : Fragment() {

    private lateinit var sleepQualityViewModel: SleepQualityViewModel
    private lateinit var allNights: List<SleepNight>
    private lateinit var binding: FragmentSleepTrackerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        sleepQualityViewModel =
                ViewModelProviders.of(activity!!).get(SleepQualityViewModel::class.java)

        sleepQualityViewModel.nights.observe(this, Observer<List<SleepNight>> { nights ->
            nights?.apply {
                allNights = nights
                showSleep(binding.textview)
            }
        })
        setClickListeners()
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

    private fun startSleep(view: View) {
        sleepQualityViewModel.tonightCache = SleepNight()
        binding.apply {
            startButton.isEnabled = false
            stopButton.isEnabled = true
            clearButton.isEnabled = true
            clearButton.isEnabled = false
        }
        sleepQualityViewModel.insert(sleepQualityViewModel.tonightCache)
    }

    private fun stopSleep(view: View) {
        binding.apply {
            startButton.isEnabled = true
            stopButton.isEnabled = false
            clearButton.isEnabled = true
        }
        sleepQualityViewModel.tonightCache.endDateTime = makeDateTimeString()
        sleepQualityViewModel.tonightCache.endTimeMilli = System.currentTimeMillis()
        sleepQualityViewModel.insert(sleepQualityViewModel.tonightCache)

        view.findNavController().navigate(
                SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment2())
    }

    private fun showSleep(view: View) {
        binding.apply {
            textview.text = "HERE IS YOUR SLEEP DATA\n\n"
            for (night in allNights) {
                textview.append("Start:\t${night.startDateTime}\n")
                textview.append("End:\t${night.endDateTime}\n")
                textview.append(
                        "Hours / Minutes:\t ${night.endTimeMilli.minus(night.startTimeMilli) / 360000} " +
                                ": ${night.endTimeMilli.minus(night.startTimeMilli) / 60000}\n")
                textview.append("Quality:\t${night.sleepQualty}\n\n")
            }
        }
    }

    private fun clearData(view: View) {
        sleepQualityViewModel.clear()
        binding.textview.text = "All your data is gone forever."
    }
}

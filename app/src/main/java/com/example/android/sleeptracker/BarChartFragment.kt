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

package com.example.android.sleeptracker


import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.android.sleeptracker.database.SleepDatabase
import com.example.android.sleeptracker.database.SleepEntry
import com.example.android.sleeptracker.databinding.FragmentBarchartBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class BarChartFragment : Fragment(), OnChartValueSelectedListener {

    private val xLabel = Array(7) { "" }

    private lateinit var binding: FragmentBarchartBinding
    private lateinit var chart: BarChart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_barchart, container, false)

        chart = binding.chart
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configChart()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.main_menu, menu)
        setChart()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_populate -> {
                populateDatabase()
                setChart()
            }
            R.id.action_clear -> clearDatabase()
        }
        return true
    }

    override fun onNothingSelected() {
        Log.d("TAG", "Nothing selected")
    }

    override fun onValueSelected(entry: Entry?, highlight: Highlight?) {
        Toast.makeText(activity, "clicked ${entry?.x}", Toast.LENGTH_SHORT).show()
        findNavController().navigate(BarChartFragmentDirections.actionBarchartFragmentToDetailFragment())

        Log.d("TAG", entry.toString())
        if (entry == null)
            return

        val bounds = RectF()
        chart.getBarBounds(entry as BarEntry, bounds)
        val position = chart.getPosition(entry, YAxis.AxisDependency.LEFT)

        Log.i("bounds", bounds.toString())
        Log.i("position", position.toString())

        Log.i("x-index",
                "low: " + chart.lowestVisibleX + ", high: "
                        + chart.highestVisibleX)

        MPPointF.recycleInstance(position)
    }

    override fun onResume() {
        super.onResume()
        chart.highlightValues(null)
    }

    private fun setChart() {
        val entries = arrayListOf<BarEntry>()

        val sleepEntries = SleepDatabase.getInstance(requireContext())?.sleepDao()?.getAll()

        if (sleepEntries == null || sleepEntries.isEmpty()) {
            return
        }

        val startColor1 = ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light)
        val startColor2 = ContextCompat.getColor(requireContext(), android.R.color.darker_gray)

        val calendar = Calendar.getInstance()

        val dayOfWeek1 = calendar.get(Calendar.DAY_OF_WEEK)
        val sleepEntriesSubset = sleepEntries.subList(0, dayOfWeek1.minus(1))

        val colors = mutableListOf<Int>()
        for (i in 0..sleepEntriesSubset.size.minus(1)) {
            val value = sleepEntriesSubset[i]
            Log.d("TAG", value.toString())
            val diff = (value.endDate.time - value.startDate.time).div(3600000f)

            calendar.time = value.endDate
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            if (i < sleepEntriesSubset.size.minus(1)) {
                entries.add(BarEntry(dayOfWeek.toFloat(), diff))
                colors.add(startColor1)
            }

            val simpleDateformat = SimpleDateFormat("EEEEE", Locale.getDefault())
            xLabel[dayOfWeek.minus(1)] = simpleDateformat.format(value.endDate)
        }

        val pendingElements = 6 - entries.size

        for (i in 0..pendingElements) {
            calendar.time = Date(Calendar.getInstance().timeInMillis - TimeUnit.DAYS.toMillis(entries.size.plus(1).toLong()))
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            entries.add(BarEntry(dayOfWeek.toFloat(), 0f))
            colors.add(startColor2)

            val simpleDateformat = SimpleDateFormat("EEEEE", Locale.getDefault())
            xLabel[dayOfWeek.minus(1)] = simpleDateformat.format(calendar.time)
        }

        entries.sortBy { it.x }

        val set = BarDataSet(entries, "BarDataSet")
        set.isHighlightEnabled = true
        set.highLightColor = Color.BLACK

        val data = BarData(set)
        set.highLightColor = Color.GREEN

        chart.data = data
        chart.invalidate() // refresh

    }

    private fun configChart() {
        chart.setOnChartValueSelectedListener(this)

        chart.setDrawValueAboveBar(true)

        chart.description.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false)
        chart.isDoubleTapToZoomEnabled = false

        chart.setDrawGridBackground(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // only intervals of 1 day
        xAxis.labelCount = 7
        xAxis.valueFormatter = (IAxisValueFormatter { value, _ -> xLabel[value.toInt().minus(1)] })

        chart.axisRight.isEnabled = false

        val legend = chart.legend
        legend.isEnabled = false
    }


    private fun populateDatabase() {
        val date = Date()
//        val random = Random()
        for (i in 0..10) {
//            val hours = random.nextFloat().times(5).plus(4).toLong()
//            val minutes = random.nextFloat().times(60).toLong()
//            val endDate = Date(date.time - TimeUnit.DAYS.toMillis(i.plus(1).toLong()))
//            val startDate = Date(endDate.time
//                    - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
//            val sleepEntry = SleepEntry(null, startDate, endDate)
//            SleepDatabase.getInstance(this)?.sleepDao()?.insert(sleepEntry)


            val hours = i.plus(1).toLong()
            val minutes = 0.toLong()
            val endDate = Date(date.time - TimeUnit.DAYS.toMillis(i.plus(1).toLong()))
            val startDate = Date(endDate.time
                    - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
            val sleepEntry = SleepEntry(null, startDate, endDate)
            SleepDatabase.getInstance(requireContext())?.sleepDao()?.insert(sleepEntry)
        }
    }

    private fun clearDatabase() {
        SleepDatabase.getInstance(requireContext())?.sleepDao()?.deleteAll()
        chart.clear()
    }
}

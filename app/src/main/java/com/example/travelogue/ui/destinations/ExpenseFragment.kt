package com.example.travelogue.ui.destinations

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChart.pie
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.travelogue.R

//created sample pie chart to test

class ExpenseFragment : Fragment(R.layout.fragment_expense) {

    // Declare PieChart
    private lateinit var pieChart: AnyChartView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = view.findViewById(R.id.pieChart)

        setUpChart()
    }

    private fun setUpChart()
    {
        // Initialize Pie Chart
        val pie = AnyChart.pie()

        // Data entries for Pie Chart
        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("Apples", 6371664))
        data.add(ValueDataEntry("Pears", 789622))
        data.add(ValueDataEntry("Bananas", 7216301))
        data.add(ValueDataEntry("Grapes", 1486621))
        data.add(ValueDataEntry("Oranges", 1200000))

        // Set data to Pie Chart
        pie.data(data)

        // Set the Pie Chart to the AnyChartView
        pieChart.setChart(pie)
    }
}


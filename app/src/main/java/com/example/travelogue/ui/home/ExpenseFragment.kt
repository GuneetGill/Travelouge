package com.example.travelogue.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChart.pie
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.travelogue.R

class ExpenseFragment : Fragment(R.layout.fragment_expense) {

    private lateinit var pieChart: AnyChartView
    private lateinit var floatingButton: Button
    private lateinit var overlay: ConstraintLayout
    private lateinit var overlayButton: Button
    private lateinit var categoryListView: ListView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Pie Chart
        pieChart = view.findViewById(R.id.pieChart)
        setUpChart()

        // Initialize Views
        floatingButton = view.findViewById(R.id.floatingButton)
        overlay = view.findViewById(R.id.overlay)
        overlayButton = view.findViewById(R.id.overlayButton)
        categoryListView = view.findViewById(R.id.categoryListView)

        // Set up floating button click listener
        floatingButton.setOnClickListener {
            // Show the overlay
            overlay.visibility = View.VISIBLE
        }

        // Set up overlay button click listener (optional)
        overlayButton.setOnClickListener {
            // Hide the overlay
            overlay.visibility = View.GONE
        }

        // Set up touch listener to dismiss the overlay when clicked anywhere outside
        view.setOnTouchListener { _, _ ->
            // Hide the overlay when clicked anywhere outside
            if (overlay.visibility == View.VISIBLE) {
                overlay.visibility = View.GONE
            }
            true
        }

        // Set up dummy data for the ListView
        val categories = arrayOf("Food", "Transport", "Entertainment", "Shopping", "Others")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        categoryListView.adapter = adapter

        // Set onItemClickListener to highlight clicked item
        categoryListView.setOnItemClickListener { parent, view, position, id ->
            // Get the clicked item TextView
            val textView = view as TextView

            // Make clicked item bold, and reset the others
            textView.setTypeface(null, Typeface.BOLD)

            // Optionally, reset the others to normal typeface if needed
            for (i in 0 until parent.childCount) {
                val itemView = parent.getChildAt(i) as TextView
                if (i != position) {
                    itemView.setTypeface(null, Typeface.NORMAL)
                }
            }
        }
    }

    private fun setUpChart() {
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

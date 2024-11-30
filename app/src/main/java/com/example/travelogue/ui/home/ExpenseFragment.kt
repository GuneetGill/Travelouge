
package com.example.travelogue.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.LiveData
import com.anychart.core.ui.Legend
import com.example.travelogue.R
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.expense_table.Expense
import com.example.travelogue.expense_table.ExpenseDao
import com.example.travelogue.expense_table.ExpenseRepository
import com.example.travelogue.expense_table.ExpenseViewModel
import com.example.travelogue.expense_table.ExpenseViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import java.util.UUID

class ExpenseFragment : Fragment(R.layout.fragment_expense) {

    private lateinit var pieChart: PieChart
    private lateinit var floatingButton: Button
    private lateinit var overlay: ConstraintLayout
    private lateinit var overlayButton: Button
    private lateinit var categoryListView: ListView
    private lateinit var expenseInputView: TextView

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var factory: ExpenseViewModelFactory
    private lateinit var database: UserDatabase
    private lateinit var databaseDao: ExpenseDao
    private lateinit var repository: ExpenseRepository

    private var selectedCategory: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the journalId from the arguments
        val countryID = arguments?.getLong("countryID") ?: 0L // Default to 0 if null

        // Use the journalId as needed
        Log.d("ExpenseFragment", "Received countryId: $countryID")


        val sessionId = UUID.randomUUID().toString()
        Log.d("ExpenseFragment", "the sessionID is $sessionId")


        // Initialize Pie Chart
        pieChart = view.findViewById(R.id.pieChart)
        setUpChart()

        // Initialize Views
        floatingButton = view.findViewById(R.id.floatingButton)
        overlay = view.findViewById(R.id.overlay)
        overlayButton = view.findViewById(R.id.overlayButton)
        categoryListView = view.findViewById(R.id.categoryListView)
        expenseInputView = view.findViewById(R.id.expenseInput)

        // Initialize ViewModel and Database
        database = UserDatabase.getInstance(requireActivity())
        databaseDao = database.expenseDao
        repository = ExpenseRepository(databaseDao)
        factory = ExpenseViewModelFactory(repository)
        expenseViewModel = ViewModelProvider(requireActivity(), factory).get(ExpenseViewModel::class.java)

        // Set up floating button click listener
        floatingButton.setOnClickListener {
            // Show the overlay
            overlay.visibility = View.VISIBLE

            // Clear selected category and unbold all items
            selectedCategory = ""  // Reset selected category
            resetCategorySelection()  // Unbold all ListView items
            // Clear the input field
            expenseInputView.setText("")  // Clear the expense amount input
        }

        // Set up overlay button click listener
        overlayButton.setOnClickListener {
            // Hide the overlay
            overlay.visibility = View.GONE
        }

        // Set up categories for ListView
        val categories = arrayOf("Food", "Transport", "Entertainment", "Shopping", "Others")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        categoryListView.adapter = adapter

        // Category selection
        categoryListView.setOnItemClickListener { parent, view, position, id ->
            val category = (view as TextView).text.toString()
            selectedCategory = category
            resetCategorySelection()  // Unbold all ListView items
            view.setTypeface(null, Typeface.BOLD) // Bold the selected category
        }

        // Handle expense submission
        val sharedPreferencesCurrency: SharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val savedCurrency = sharedPreferencesCurrency.getString("preferred_currency", "unknown")

        overlayButton.setOnClickListener {
            val amountInput = expenseInputView.text.toString().toFloatOrNull()

            if (amountInput != null && selectedCategory.isNotEmpty()) {
                val newExpense = Expense(
                    journalId = null,
                    sessionId = sessionId,
                    countryId = countryID,
                    category = selectedCategory,
                    amount = amountInput,
                    currency = savedCurrency ?: "unknown"
                )

                // Add the expense to the database
                expenseViewModel.addExpense(newExpense)
                Log.d("ExpenseFragment", "Added expense: $newExpense")

                // Manually fetch updated expenses and update the pie chart
                updateUIWithExpenses(sessionId)

                // Hide overlay after submitting
                overlay.visibility = View.GONE
            } else {
                Toast.makeText(context, "Please enter a valid amount and select a category", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up an outside click listener to close the overlay when clicked outside
        view.setOnTouchListener { _, event ->
            if (overlay.visibility == View.VISIBLE) {
                val rect = Rect()
                overlay.getGlobalVisibleRect(rect)
                if (!rect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    overlay.visibility = View.GONE
                }
            }
            false
        }


        // Inside onViewCreated or wherever you are handling the expenses fetching and UI update
        Log.d("test", "countryID: calling old items $countryID") // Log the countryID

        expenseViewModel.getExpensesByCountryId(countryID)  // Fetch expenses based on the countryID

        // Observe the expenses LiveData for changes
        expenseViewModel.expensesByCountryId.observe(viewLifecycleOwner) { expenses ->
            Log.d("test", "Expenses: being displaued $expenses") // Log the expenses for debugging

            // If no expenses are found for the given countryID, return early
            if (expenses.isEmpty()) {
                Log.d("test", "No expenses found, isEmpty() triggered.") // Log if no expenses are found
                // Optionally, display a message to the user
                //Toast.makeText(context, "No expenses found for this country.", Toast.LENGTH_SHORT).show()
                return@observe  // Skip further code execution
            }

            // Ensure the countryID from expenses matches the current one
            val isMatchingCountryID = expenses.all { it.countryId == countryID }

            if (!isMatchingCountryID) {
                Log.d("test", "Country ID mismatch, skipping UI update.") // Log if country IDs don't match
                return@observe  // Skip the updateUI function if country ID does not match
            }

            // If expenses are found, update the UI with the expenses
            updateUIWithExpensesDB(expenses)
        }


    }


    private fun setUpChart() {
        pieChart.setUsePercentValues(true)
        pieChart.setDrawSliceText(true)
        pieChart.setCenterTextSize(18f)
        pieChart.setHoleRadius(50f)
        pieChart.setTransparentCircleRadius(55f)
        pieChart.setTransparentCircleColor(Color.BLACK)
        pieChart.getDescription().setEnabled(false)
        pieChart.setMaxAngle(360f)

        // Setting up the entry label styling (increased size and black color)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(14f)
        pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD) // Bold for better visibility

    }


    private fun updatePieChart(entries: ArrayList<PieEntry>) {
        val dataSet = PieDataSet(entries, "")
        dataSet.setColors(Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.MAGENTA)
        dataSet.setValueFormatter(PercentFormatter(pieChart))

        val data = PieData(dataSet)
        pieChart.data = data
        data.setValueTextSize(10f)
        pieChart.invalidate() // Refresh the chart
    }

    // Function to unbold all items in the ListView
    private fun resetCategorySelection() {
        for (i in 0 until categoryListView.childCount) {
            val view = categoryListView.getChildAt(i) as TextView
            view.setTypeface(null, Typeface.NORMAL) // Unbold all items
        }
    }

    // Function to update the UI with expenses and update the pie chart
    private fun updateUIWithExpensesDB(expenses: List<Expense>) {
        // Group expenses by category and sum the amounts
        val categoryTotals = mutableMapOf<String, Float>()
        for (expense in expenses) {
            categoryTotals[expense.category] = categoryTotals.getOrDefault(expense.category, 0f) + expense.amount
        }

        // Convert the category totals to PieEntry objects
        val entries = ArrayList<PieEntry>()
        for ((category, total) in categoryTotals) {
            entries.add(PieEntry(total, category))
        }

        // Update the pie chart with the new data
        updatePieChart(entries)

        // Update the ListView with the categories and amounts
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_2, categoryTotals.map { "${it.key}: ${it.value}" })
        categoryListView.adapter = adapter
    }


    // Function to update the UI with expenses and update the pie chart
    private fun updateUIWithExpenses(sessionId: String) {
        // Fetch the expenses by sessionId
        expenseViewModel.getExpenses(null, sessionId)

        // Observe the expenses LiveData for changes
        expenseViewModel.expenses.observe(viewLifecycleOwner, Observer { expenses ->
            // Group expenses by category and sum the amounts
            val categoryTotals = mutableMapOf<String, Float>()
            for (expense in expenses) {
                categoryTotals[expense.category] = categoryTotals.getOrDefault(expense.category, 0f) + expense.amount
            }

            // Convert the category totals to PieEntry objects
            val entries = ArrayList<PieEntry>()
            for ((category, total) in categoryTotals) {
                entries.add(PieEntry(total, category))
            }

            // Update the pie chart with the new data
            updatePieChart(entries)
        })
    }

}




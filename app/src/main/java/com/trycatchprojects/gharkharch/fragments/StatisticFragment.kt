package com.trycatchprojects.gharkharch.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.adapters.StatisticAdapter
import com.trycatchprojects.gharkharch.databinding.FragmentStatisticBinding
import com.trycatchprojects.gharkharch.roomdb.AppDatabase
import com.trycatchprojects.gharkharch.roomdb.entities.ExpenseEntity
import com.trycatchprojects.gharkharch.roomdb.entities.IncomeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class StatisticFragment : Fragment() {
    private lateinit var binding:FragmentStatisticBinding
    private var selectedType: String = "Expense" // Default to Expense
    private var isSortAscending: Boolean = true
    private var startDate: Long = 0L
    private var endDate: Long = 0L
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR) // Default to current year
    private var selectedPeriod: String = "Day" // Default to Day



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticBinding.inflate(layoutInflater)
        setUpExpenseIncomeToggle()
        setUpYearSpinner()
        setupClickListeners()

        binding.imgBack.setOnClickListener {
            findNavController().navigate(R.id.action_statisticFragment_to_homeFragment)
        }

        binding.imgSortAcseDesc.setOnClickListener {
            isSortAscending = !isSortAscending
            // Sort data based on the current date range and type
            fetchData(startDate, endDate)
        }

        // Set default to show today's data
        updateSelection(binding.tvDay)
        fetchDayData() // Fetch initial data for the selected year and day period

        return binding.root
    }
    private fun setupClickListeners() {
        binding.tvDay.setOnClickListener {
            updateSelection(binding.tvDay)
            fetchDayData()
        }

        binding.tvWeek.setOnClickListener {
            updateSelection(binding.tvWeek)
            fetchWeekData()
        }

        binding.tvMonth.setOnClickListener {
            updateSelection(binding.tvMonth)
            fetchMonthData()
        }

        binding.tvYear.setOnClickListener {
            updateSelection(binding.tvYear)
            fetchYearData()
        }
    }

    private fun fetchDayData() {
        selectedPeriod = "Day"
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
        }
        startDate = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        endDate = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        fetchData(startDate, endDate)
    }

    private fun fetchWeekData() {
        selectedPeriod = "Week"
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
        }
        startDate = calendar.apply {
            set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        endDate = calendar.apply {
            set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        fetchData(startDate, endDate)
    }

    private fun fetchMonthData() {
        selectedPeriod = "Month"
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
        }
        startDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        endDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        fetchData(startDate, endDate)
    }

    private fun fetchYearData() {
        selectedPeriod = "Year"
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
        }
        startDate = calendar.apply {
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        endDate = calendar.apply {
            set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        fetchData(startDate, endDate)
    }

    private fun updateSelection(selectedTextView: TextView) {
        resetSelections()
        selectedTextView.setBackgroundResource(R.drawable.spinner_border_bg)
        selectedTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun resetSelections() {
        val textViews = listOf(binding.tvDay, binding.tvWeek, binding.tvMonth, binding.tvYear)
        for (textView in textViews) {
            textView.setBackgroundResource(0) // Remove background
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)) // Set text color to black
        }
    }

    private fun fetchData(startDate: Long, endDate: Long) {
        when (selectedType) {
            "Expense" -> fetchExpenseData(startDate, endDate)
            "Income" -> fetchIncomeData(startDate, endDate)
        }
    }

    private fun fetchExpenseData(startDate: Long, endDate: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val expenseDao = AppDatabase.getDatabase(requireContext()).expenseDao()
            val expenses = expenseDao.getExpensesByDateRange(startDate, endDate)
            val sortedExpenses = if (isSortAscending) {
                expenses.sortedBy { it.amount }
            } else {
                expenses.sortedByDescending { it.amount }
            }
            withContext(Dispatchers.Main) {
                Log.d("StatisticFragment", "Fetched and sorted expense data: $sortedExpenses")
                updateUI(sortedExpenses)
            }
        }
    }

    private fun fetchIncomeData(startDate: Long, endDate: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val incomeDao = AppDatabase.getDatabase(requireContext()).incomeDao()
            val incomes = incomeDao.getIncomesByDateRange(startDate, endDate)
            val sortedIncomes = if (isSortAscending) {
                incomes.sortedBy { it.amount }
            } else {
                incomes.sortedByDescending { it.amount }
            }
            withContext(Dispatchers.Main) {
                Log.d("StatisticFragment", "Fetched and sorted income data: $sortedIncomes")
                updateUI(sortedIncomes)
            }
        }
    }

    private fun updateUI(data: List<Any>) {
        val adapter = StatisticAdapter(data) { item ->
            showDeleteConfirmationDialog(item)
        }
        binding.topSpendingRV.layoutManager = LinearLayoutManager(requireContext())
        binding.topSpendingRV.adapter = adapter
    }

    private fun showDeleteConfirmationDialog(item: Any) {
        val itemName = when (item) {
            is ExpenseEntity -> item.name
            is IncomeEntity -> item.name
            else -> "Item"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete '$itemName'?")
            .setPositiveButton("Yes") { _, _ ->
                deleteItem(item)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteItem(item: Any) {
        CoroutineScope(Dispatchers.IO).launch {
            when (item) {
                is ExpenseEntity -> {
                    AppDatabase.getDatabase(requireContext()).expenseDao().delete(item)
                }
                is IncomeEntity -> {
                    AppDatabase.getDatabase(requireContext()).incomeDao().delete(item)
                }
            }
            fetchData(startDate, endDate) // Refresh data after deletion
        }
    }
    private fun setUpExpenseIncomeToggle() {
        val list = listOf("Expense", "Income")
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list
        )
        arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.ExpenseIncomeSpinner.adapter = arrayAdapter

        binding.ExpenseIncomeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selectedType = if (position == 0) "Expense" else "Income"
                fetchData(startDate, endDate)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setUpYearSpinner() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (2024..2054).toList()
        val yearAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, years
        )
        yearAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.spinnerYear.adapter = yearAdapter
        binding.spinnerYear.setSelection(years.indexOf(currentYear))

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selectedYear = years[position]
                // Update the data shown according to the selected year and current period
                when (selectedPeriod) {
                    "Day" -> fetchDayData()
                    "Week" -> fetchWeekData()
                    "Month" -> fetchMonthData()
                    "Year" -> fetchYearData()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}
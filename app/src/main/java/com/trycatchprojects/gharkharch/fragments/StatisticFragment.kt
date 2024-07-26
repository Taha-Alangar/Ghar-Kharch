package com.trycatchprojects.gharkharch.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.adapters.StatisticAdapter
import com.trycatchprojects.gharkharch.databinding.FragmentStatisticBinding
import com.trycatchprojects.gharkharch.roomdb.AppDatabase
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticBinding.inflate(layoutInflater)
        setUpExpenseIncomeToggle()
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
        val calendar = Calendar.getInstance()
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


        return binding.root
    }

    private fun setupClickListeners() {
        binding.tvDay.setOnClickListener {
            val calendar = Calendar.getInstance()
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

        binding.tvWeek.setOnClickListener {
            val calendar = Calendar.getInstance()
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

        binding.tvMonth.setOnClickListener {
            val calendar = Calendar.getInstance()
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

        binding.tvYear.setOnClickListener {
            val calendar = Calendar.getInstance()
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
        val adapter = StatisticAdapter(data)
        binding.topSpendingRV.layoutManager = LinearLayoutManager(requireContext())
        binding.topSpendingRV.adapter = adapter
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
}
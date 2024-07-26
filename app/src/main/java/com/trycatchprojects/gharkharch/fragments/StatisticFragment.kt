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
        binding.apply {
            lineChart.gradientFillColors= intArrayOf(
                Color.parseColor("#63B5AF"),
                Color.TRANSPARENT,
            )
            lineChart.animation.duration= animationDuration
            lineChart.animate(lineSet)

            lineChart.onDataPointTouchListener={index,_,_ ->
                tvChartData.text= lineSet.toList()[index].second.toString()
            }
        }


        return binding.root
    }

    private fun setupClickListeners() {
        binding.tvDay.setOnClickListener {
            val calendar = Calendar.getInstance()
            val startOfDay = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val endOfDay = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
            fetchData(startOfDay, endOfDay)
        }

        binding.tvWeek.setOnClickListener {
            val calendar = Calendar.getInstance()
            val startOfWeek = calendar.apply {
                set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val endOfWeek = calendar.apply {
                set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
            fetchData(startOfWeek, endOfWeek)
        }

        binding.tvMonth.setOnClickListener {
            val calendar = Calendar.getInstance()
            val startOfMonth = calendar.apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val endOfMonth = calendar.apply {
                set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
            fetchData(startOfMonth, endOfMonth)
        }

        binding.tvYear.setOnClickListener {
            val calendar = Calendar.getInstance()
            val startOfYear = calendar.apply {
                set(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val endOfYear = calendar.apply {
                set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
            fetchData(startOfYear, endOfYear)
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
            withContext(Dispatchers.Main) {
                Log.d("StatisticFragment", "Fetched expense data: $expenses")
                updateUI(expenses)
            }
        }
    }

    private fun fetchIncomeData(startDate: Long, endDate: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val incomeDao = AppDatabase.getDatabase(requireContext()).incomeDao()
            val incomes = incomeDao.getIncomesByDateRange(startDate, endDate)
            withContext(Dispatchers.Main) {
                Log.d("StatisticFragment", "Fetched income data: $incomes")
                updateUI(incomes)
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
                val calendar = Calendar.getInstance()
                val startDate = calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val endDate = calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis
                fetchData(startDate, endDate)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
    companion object{
        private val lineSet= listOf(
            "label1" to 4.5f,
            "label2" to 6f,
            "label3" to 10f,
            "label4" to 3f,
            "label5" to 2.5f,
            "label6" to 1.5f,
            "label6" to 2.5f,
            "label6" to 0.5f,
            "label6" to 4.5f,
            "label6" to 6.5f,
            "label6" to 20.5f,
            "label6" to 9.5f,
            "label6" to 10.5f,

            )
        private const val animationDuration=1000L
    }

}
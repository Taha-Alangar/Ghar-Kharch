package com.trycatchprojects.gharkharch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.adapters.C_E_Adapter
import com.trycatchprojects.gharkharch.databinding.FragmentCategoryExpenditureBinding
import com.trycatchprojects.gharkharch.roomdb.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CategoryExpenditureFragment : Fragment() {
    private lateinit var binding:FragmentCategoryExpenditureBinding
    private val expenseDao by lazy { AppDatabase.getDatabase(requireContext()).expenseDao() }
    private val categoryDao by lazy { AppDatabase.getDatabase(requireContext()).categoryDao() }
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR) // Default to current year


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentCategoryExpenditureBinding.inflate(layoutInflater)


        setUpCategorySpinner()
        setUpYearSpinner()
        setUpCERecyclerView()

        binding.CEImgBack.setOnClickListener {
            findNavController().navigate(R.id.action_categoryExpenditureFragment_to_homeFragment)
        }

        return binding.root
    }

    private fun setUpCERecyclerView() {
        binding.CERv.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpCategorySpinner() {
        CoroutineScope(Dispatchers.IO).launch {
            val categories = categoryDao.getAllCategories()
            val categoryNames = categories.map { it.name }

            withContext(Dispatchers.Main) {
                val arrayAdapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoryNames)
                arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
                binding.CESpinner.adapter = arrayAdapter

                binding.CESpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedCategory = categoryNames.getOrNull(position) ?: return
                        fetchExpenseForCategory(selectedCategory, selectedYear)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }

    private fun setUpYearSpinner() {
        // Define the year range from 2024 to 2054
        val startYear = 2024
        val endYear = 2054
        val years = (startYear..endYear).toList()

        // Create an ArrayAdapter with the specified year range
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, years
        )
        arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.yearSpinner.adapter = arrayAdapter

        // Set the default selected year to the current year if it falls within the range
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val defaultYear = if (currentYear in startYear..endYear) currentYear else startYear
        val defaultYearPosition = years.indexOf(defaultYear)
        binding.yearSpinner.setSelection(defaultYearPosition)

        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedYear = years.getOrNull(position) ?: return
                val selectedCategory = binding.CESpinner.selectedItem?.toString() ?: return
                fetchExpenseForCategory(selectedCategory, selectedYear)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }



    private fun fetchExpenseForCategory(category: String, year: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val categoryId = categoryDao.getCategoryIdByName(category)
            if (categoryId != null) {
                val startDate = getStartOfYear(year)
                val endDate = getEndOfYear(year)
                val expenses = expenseDao.getExpensesByCategoryIdAndDateRange(categoryId, startDate, endDate)

                withContext(Dispatchers.Main) {
                    binding.CERv.adapter = C_E_Adapter(expenses)
                    binding.CETotalExpenditure.text = "₹ ${expenses.sumOf { it.amount }}"
                }
            }
        }
    }

    private fun getStartOfYear(year: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getEndOfYear(year: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_YEAR, getActualMaximum(Calendar.DAY_OF_YEAR))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }
}
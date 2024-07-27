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

class CategoryExpenditureFragment : Fragment() {
    private lateinit var binding:FragmentCategoryExpenditureBinding
    private val expenseDao by lazy { AppDatabase.getDatabase(requireContext()).expenseDao() }
    private val categoryDao by lazy { AppDatabase.getDatabase(requireContext()).categoryDao() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentCategoryExpenditureBinding.inflate(layoutInflater)

        setUpCategorySpinner()

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

                // Set up listener to handle selection
                binding.CESpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedCategory = categoryNames[position]
                        fetchExpenseForCategory(selectedCategory)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }

    private fun fetchExpenseForCategory(category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val categoryId = categoryDao.getCategoryIdByName(category)
            if (categoryId != null) {
                val expenses = expenseDao.getExpensesByCategoryId(categoryId)

                withContext(Dispatchers.Main) {
                    // Update the UI with the expenses and total expense
                    binding.CERv.adapter = C_E_Adapter(expenses)
                    binding.CETotalExpenditure.text = "₹ ${expenses.sumOf { it.amount }}"

                }

            }


        }

    }
}
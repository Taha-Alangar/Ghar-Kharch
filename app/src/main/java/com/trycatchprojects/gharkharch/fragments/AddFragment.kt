package com.trycatchprojects.gharkharch.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.databinding.FragmentAddBinding
import com.trycatchprojects.gharkharch.roomdb.AppDatabase
import com.trycatchprojects.gharkharch.roomdb.entities.CategoryEntity
import com.trycatchprojects.gharkharch.roomdb.entities.ExpenseEntity
import com.trycatchprojects.gharkharch.roomdb.entities.IncomeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddBinding.inflate(layoutInflater)


        // Set default date
        setDefaultDate()

        // Set up date picker dialog
        setUpDatePicker()

        // Category Spinner
        setUpCategorySpinner()

        // Setup Expense/Income/Category Toggle Spinner
        setUpEICSpinner()

        // Set up add button listener
        setAddButtonListener()

        binding.imageView3.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }

        return binding.root
    }
    private fun setUpDatePicker() {
        binding.edtDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val minYear = 2024 // Minimum year allowed

            // Set initial year, month, and day
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(selectedYear, selectedMonth, selectedDay)
                    }

                    // Ensure that selected year is 2024 or later
                    if (selectedYear >= minYear) {
                        val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                        binding.edtDate.setText(dateFormat.format(selectedDate.time))
                    } else {
                        Toast.makeText(requireContext(), "Please select a date from 2024 or later", Toast.LENGTH_SHORT).show()
                        // Optionally, reset to default date or current date
                        setDefaultDate()
                    }
                },
                year,
                month,
                day
            )

            // Customize the DatePickerDialog to restrict year range
            datePickerDialog.datePicker.minDate = Calendar.getInstance().apply {
                set(minYear, Calendar.JANUARY, 1)
            }.timeInMillis
            datePickerDialog.datePicker.maxDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2100) // You can set this to a sensible maximum year
            }.timeInMillis

            datePickerDialog.show()
        }
    }

    private fun setDefaultDate() {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        binding.edtDate.setText(dateFormat.format(currentDate))
    }

    private fun setAddButtonListener() {
        binding.btnAddEI.setOnClickListener {
            val selectedOption = binding.ExpenseIncomeToggle.selectedItemPosition
            when (selectedOption) {
                0 -> addExpense()
                1 -> addIncome()
                2 -> addCategory()
            }
        }
    }

    private fun addCategory() {
        val categoryName = binding.edtIncomeName.text.toString().trim()
        if (categoryName.isNotEmpty()) {
            val category = CategoryEntity(name = categoryName)
            CoroutineScope(Dispatchers.IO).launch {
                val categoryDao = AppDatabase.getDatabase(requireContext()).categoryDao()
                categoryDao.insertCategory(category)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Category added successfully", Toast.LENGTH_SHORT).show()
                    binding.edtIncomeName.text!!.clear()
                    setUpCategorySpinner() // Refresh the spinner after adding a new category
                }
            }
        } else {
            binding.edtIncomeName.error = "Category name cannot be empty"
        }
    }
    private fun addIncome() {
        val incomeName = binding.edtIncomeName.text.toString().trim()
        val incomeAmount = binding.edtAmount.text.toString().toDoubleOrNull()
        val incomeDateStr = binding.edtDate.text.toString().trim()

        if (incomeName.isNotEmpty() && incomeAmount != null && incomeDateStr.isNotEmpty()) {
            val incomeDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).parse(incomeDateStr)?.time ?: System.currentTimeMillis()
            val income = IncomeEntity(name = incomeName, amount = incomeAmount, date = incomeDate)

            CoroutineScope(Dispatchers.IO).launch {
                val incomeDao = AppDatabase.getDatabase(requireContext()).incomeDao()
                incomeDao.insertIncome(income)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Income added successfully", Toast.LENGTH_SHORT).show()
                    binding.edtIncomeName.text!!.clear()
                    binding.edtAmount.text!!.clear()
                    binding.edtDate.text!!.clear()
                    setDefaultDate()
                }
            }
        } else {
            if (incomeName.isEmpty()) binding.edtIncomeName.error = "Income name cannot be empty"
            if (incomeAmount == null) binding.edtAmount.error = "Amount must be a number"
            if (incomeDateStr.isEmpty()) binding.edtDate.error = "Date cannot be empty"
        }
    }

    private fun addExpense() {
        val spinnerPosition = binding.spinnerName.selectedItemPosition
        val expenseAmount = binding.edtAmount.text.toString().toDoubleOrNull()
        val expenseDateStr = binding.edtDate.text.toString().trim()

        if (expenseAmount != null && expenseDateStr.isNotEmpty()&& spinnerPosition != AdapterView.INVALID_POSITION ) {
            val expenseDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).parse(expenseDateStr)?.time ?: System.currentTimeMillis()

            CoroutineScope(Dispatchers.IO).launch {
                val categoryDao = AppDatabase.getDatabase(requireContext()).categoryDao()
                val selectedCategory = categoryDao.getAllCategories()[spinnerPosition]

                val expense = ExpenseEntity(categoryId = selectedCategory.id, amount = expenseAmount, name = selectedCategory.name, date = expenseDate)
                val expenseDao = AppDatabase.getDatabase(requireContext()).expenseDao()
                expenseDao.insertExpense(expense)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Expense added successfully", Toast.LENGTH_SHORT).show()
                    binding.edtAmount.text!!.clear()
                    binding.edtDate.text!!.clear()
                    setDefaultDate()
                }
            }
        } else {
            if (expenseAmount == null) binding.edtAmount.error = "Amount must be a number"
            if (expenseDateStr.isEmpty()) binding.edtDate.error = "Date cannot be empty"
            if (spinnerPosition == AdapterView.INVALID_POSITION){
                Toast.makeText(requireContext(), "Add Category First", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setUpEICSpinner() {
        val list2 = listOf("Expense", "Income", "Category")
        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.spinner_item, list2)
        arrayAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.ExpenseIncomeToggle.adapter = arrayAdapter2

        binding.ExpenseIncomeToggle.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    when (position) {
                        0 -> {
                            binding.textView5.text="Name"
                            binding.tvAddEI.text = "Add Expense"
                            binding.tvEIDescription.text = "You can add your daily expenditure here"
                            binding.textView6.visibility = View.VISIBLE
                            binding.textView6.text="Amount"
                            binding.textView61.text="Date"
                            binding.textInputLayout21.visibility = View.GONE
                            binding.textInputLayout2.visibility = View.VISIBLE
                            binding.textView61.visibility=View.VISIBLE
                            binding.textInputLayout3.visibility = View.VISIBLE
                            binding.btnAddEI.text = "Add Expense"
                            binding.spinnerName.visibility = View.VISIBLE
                        }
                        1 -> {
                            binding.textView5.text="Name"
                            binding.tvAddEI.text = "Add Income"
                            binding.spinnerName.visibility=View.GONE
                            binding.textInputLayout21.visibility=View.VISIBLE
                            binding.edtIncomeName.visibility=View.VISIBLE
                            binding.textView6.visibility=View.VISIBLE
                            binding.textView6.text="Amount"
                            binding.textInputLayout21.hint="Income Type"
                            binding.tvEIDescription.text = "You can add your Income here"
                            binding.textInputLayout2.visibility=View.VISIBLE
                            binding.edtAmount.visibility=View.VISIBLE
                            binding.textView61.visibility=View.VISIBLE
                            binding.textView61.text="Date"
                            binding.textInputLayout3.visibility=View.VISIBLE
                            binding.edtDate.visibility=View.VISIBLE
                            binding.btnAddEI.text = "Add Income"
                        }
                        2 -> {
                            binding.textView5.text="Name"
                            binding.tvAddEI.text = "Add Category"
                            binding.textInputLayout21.hint="Category Name"
                            binding.edtIncomeName.visibility=View.VISIBLE
                            binding.textView6.text="Here you can add Categories for \nyour expenses"
                            binding.textView61.text="Example : Groceries , Water Bills,Electricity Bills ,\n Water Bills ,Others...."
                            binding.textInputLayout2.visibility=View.GONE
                            binding.textInputLayout3.visibility=View.GONE
                            binding.spinnerName.visibility=View.GONE
                            binding.textInputLayout21.visibility=View.VISIBLE
                            binding.tvEIDescription.text = "You can create a category here"
                            binding.btnAddEI.text = "Add Category"
                        }
                    }
                    binding.btnAddEI.visibility = View.VISIBLE
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // Do nothing
                }
            }
    }


    private fun setUpCategorySpinner() {
        val categoryDao = AppDatabase.getDatabase(requireContext()).categoryDao()
        CoroutineScope(Dispatchers.IO).launch {
            val categories = categoryDao.getAllCategories()
            val categoryNames = categories.map { it.name }

            withContext(Dispatchers.Main) {
                val arrayAdapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoryNames)
                arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
                binding.spinnerName.adapter = arrayAdapter

                binding.spinnerName.setOnLongClickListener {
                    val selectedPosition = binding.spinnerName.selectedItemPosition
                    if (selectedPosition != AdapterView.INVALID_POSITION) {
                        val selectedCategoryName = categoryNames[selectedPosition]
                        showDeleteCategoryDialog(selectedCategoryName)
                    }
                    true
                }
            }
        }

        binding.spinnerName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle item selection
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun showDeleteCategoryDialog(categoryName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete the category '$categoryName'?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteCategory(categoryName)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun deleteCategory(categoryName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val categoryDao = AppDatabase.getDatabase(requireContext()).categoryDao()
            val category = categoryDao.getAllCategories().find { it.name == categoryName }
            if (category != null) {
                categoryDao.deleteCategory(category)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Category '$categoryName' deleted successfully", Toast.LENGTH_SHORT).show()
                    setUpCategorySpinner() // Refresh the spinner after deletion
                }
            }
        }
    }


}
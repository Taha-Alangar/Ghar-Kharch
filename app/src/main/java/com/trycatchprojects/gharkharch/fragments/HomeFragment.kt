package com.trycatchprojects.gharkharch.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.adapters.TransactionAdapter
import com.trycatchprojects.gharkharch.databinding.FragmentHomeBinding
import com.trycatchprojects.gharkharch.roomdb.AppDatabase
import com.trycatchprojects.gharkharch.roomdb.dao.ExpenseDao
import com.trycatchprojects.gharkharch.roomdb.dao.IncomeDao
import com.trycatchprojects.gharkharch.utils.Transaction
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("myName", "")
        binding.myName.text = name

        setUpTransactionRV()
        getTransactionsRecord()
        binding.textView14.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_statisticFragment)
        }

        return binding.root
    }
    private fun getTransactionsRecord() {
        // Assuming you have access to your DAO objects here
        val expenseDao= AppDatabase.getDatabase(requireContext()).expenseDao()
        val incomeDao = AppDatabase.getDatabase(requireContext()).incomeDao()

            lifecycleScope.launch {
                val expenses = expenseDao.getAllExpenses()
                val incomes = incomeDao.getAllIncomes()

                // Calculate totals
                val totalExpense = expenses.sumOf { it.amount }
                val totalIncome = incomes.sumOf { it.amount }
                val totalBalance = totalIncome - totalExpense

                // Update UI with totals
                binding.tvTotalBalance.text = "$${totalBalance}"
                binding.tvIncome.text = "$${totalIncome}"
                binding.tvExpense.text = "$${totalExpense}"

                val transactions = mutableListOf<Transaction>()
                transactions.addAll(expenses.map {
                    Transaction.Expense(it.id, it.categoryId, it.name, it.amount,
                        it.date
                    )
                })
                transactions.addAll(incomes.map {
                    Transaction.Income(it.id, it.name, it.amount, it.date)
                })

//                // Sort transactions as needed, for example by date
                transactions.sortBy {transactions->
                    when(transactions){
                        is Transaction.Expense -> transactions.date
                        is Transaction.Income -> transactions.date
                    }
                }

                transactionAdapter = TransactionAdapter(transactions)
                binding.transactionRV.adapter = transactionAdapter
            }
    }


    private fun setUpTransactionRV() {
        binding.transactionRV.layoutManager = LinearLayoutManager(requireContext())
    }

}
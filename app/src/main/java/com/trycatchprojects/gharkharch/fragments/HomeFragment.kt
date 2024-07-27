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
    private val expenseDao by lazy { AppDatabase.getDatabase(requireContext()).expenseDao() }
    private val incomeDao by lazy { AppDatabase.getDatabase(requireContext()).incomeDao() }


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
        lifecycleScope.launch {
            val expenses = expenseDao.getAllExpenses()
            val incomes = incomeDao.getAllIncomes()

            // Calculate totals
            val totalExpense = expenses.sumOf { it.amount }
            val totalIncome = incomes.sumOf { it.amount }
            val totalBalance = totalIncome - totalExpense

            // Update UI with totals
            binding.tvTotalBalance.text = "₹ $totalBalance"
            binding.tvIncome.text = "₹ $totalIncome"
            binding.tvExpense.text = "₹ $totalExpense"

            val allTransactions = mutableListOf<Transaction>()
            allTransactions.addAll(expenses.map {
                Transaction.Expense(
                    it.id, it.categoryId, it.name, it.amount, it.date
                )
            })
            allTransactions.addAll(incomes.map {
                Transaction.Income(
                    it.id, it.name, it.amount, it.date
                )
            })

            // Sort transactions by date and take the top 20 most recent
            val topTransactions = allTransactions.sortedByDescending { transaction ->
                when (transaction) {
                    is Transaction.Expense -> transaction.date
                    is Transaction.Income -> transaction.date
                }
            }.take(20)

            // Set up adapter with the top 20 transactions
            transactionAdapter = TransactionAdapter(topTransactions)
            binding.transactionRV.adapter = transactionAdapter
        }
    }


    private fun setUpTransactionRV() {
        binding.transactionRV.layoutManager = LinearLayoutManager(requireContext())
    }

}
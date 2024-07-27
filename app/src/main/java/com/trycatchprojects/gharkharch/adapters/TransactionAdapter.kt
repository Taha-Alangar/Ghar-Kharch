package com.trycatchprojects.gharkharch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.utils.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private val transactions: List<Transaction>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_INCOME = 0
        private const val TYPE_EXPENSE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (transactions[position]) {
            is Transaction.Income -> TYPE_INCOME
            is Transaction.Expense -> TYPE_EXPENSE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_INCOME -> IncomeViewHolder(layoutInflater.inflate(R.layout.item_view_expense_income, parent, false))
            TYPE_EXPENSE -> ExpenseViewHolder(layoutInflater.inflate(R.layout.item_view_expense_income, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val transaction = transactions[position]) {
            is Transaction.Income -> (holder as IncomeViewHolder).bind(transaction)
            is Transaction.Expense -> (holder as ExpenseViewHolder).bind(transaction)
        }
    }

    override fun getItemCount(): Int = transactions.size

    // ViewHolder classes for Income and Expense
    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)


        fun bind(income: Transaction.Income) {
            tvName.text = income.name
            tvDate.text = formatDate(income.date)
            tvAmount.text = "₹ ${income.amount}"
            tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.income_color))

        }
    }


    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

        fun bind(expense: Transaction.Expense) {
            tvName.text = expense.name // You might want to customize this or include the category name
            tvDate.text = formatDate(expense.date)
            tvAmount.text = "₹ ${expense.amount}"
            tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.expense_color))
        }
    }
}
// Utility function to format the date
private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(date)
}
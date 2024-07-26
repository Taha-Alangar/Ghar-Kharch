package com.trycatchprojects.gharkharch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trycatchprojects.gharkharch.databinding.ItemViewExpenseIncomeBinding
import com.trycatchprojects.gharkharch.roomdb.entities.ExpenseEntity
import java.text.SimpleDateFormat
import java.util.Locale

class C_E_Adapter(private val expenses: List<ExpenseEntity>) : RecyclerView.Adapter<C_E_Adapter.ViewHolder>(){
    class ViewHolder(val binding: ItemViewExpenseIncomeBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the binding layout for each item
        val binding = ItemViewExpenseIncomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenses[position]
        holder.binding.tvName.text = expense.name
        holder.binding.tvDate.text = formatDate(expense.date)
        holder.binding.tvAmount.text = expense.amount.toString()
    }
    // Utility function to format the date
    private fun formatDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}
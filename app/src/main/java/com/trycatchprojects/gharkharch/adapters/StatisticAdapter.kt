// StatisticAdapter.kt
package com.trycatchprojects.gharkharch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trycatchprojects.gharkharch.databinding.ItemViewExpenseIncomeBinding
import com.trycatchprojects.gharkharch.roomdb.entities.ExpenseEntity
import com.trycatchprojects.gharkharch.roomdb.entities.IncomeEntity
import java.text.SimpleDateFormat
import java.util.Locale

class StatisticAdapter(private val items: List<Any>) :
    RecyclerView.Adapter<StatisticAdapter.StatisticViewHolder>() {

    inner class StatisticViewHolder(private val binding: ItemViewExpenseIncomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any) {
            if (item is ExpenseEntity) {
                binding.tvName.text = item.name
                binding.tvAmount.text = item.amount.toString()
                binding.tvDate.text = formatDate(item.date)
            } else if (item is IncomeEntity) {
                binding.tvName.text = item.name
                binding.tvAmount.text = item.amount.toString()
                binding.tvDate.text = formatDate(item.date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        val binding = ItemViewExpenseIncomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatisticViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    // Utility function to format the date
    private fun formatDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}

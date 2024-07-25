package com.trycatchprojects.gharkharch.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.databinding.FragmentStatisticBinding

class StatisticFragment : Fragment() {
    private lateinit var binding:FragmentStatisticBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticBinding.inflate(layoutInflater)

        val list= listOf("Expense","Income")
        val arrayAdapter= ArrayAdapter(requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,list)
        arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.ExpenseIncomeSpinner.adapter=arrayAdapter

        binding.ExpenseIncomeSpinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val selectedItem=parent?.getItemAtPosition(position).toString()
//                binding.textView5.text=selectedItem
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
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
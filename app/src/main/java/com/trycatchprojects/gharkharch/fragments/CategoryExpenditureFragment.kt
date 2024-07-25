package com.trycatchprojects.gharkharch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.databinding.FragmentCategoryExpenditureBinding

class CategoryExpenditureFragment : Fragment() {
    private lateinit var binding:FragmentCategoryExpenditureBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentCategoryExpenditureBinding.inflate(layoutInflater)

        val list= listOf("Youtube Premium","Groceries","Movies","Water Bills","Rent","Food")
        val arrayAdapter= ArrayAdapter(requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,list)
        arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.CESpinner.adapter=arrayAdapter
        binding.CESpinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val selectedItem=parent?.getItemAtPosition(position).toString()
//                binding.textView5.text=selectedItem
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        return binding.root
    }
}
package com.trycatchprojects.gharkharch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.databinding.FragmentCategoryExpenditureBinding

class CategoryExpenditureFragment : Fragment() {
    private lateinit var binding:FragmentCategoryExpenditureBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCategoryExpenditureBinding.inflate(layoutInflater)
        return binding.root
    }
}
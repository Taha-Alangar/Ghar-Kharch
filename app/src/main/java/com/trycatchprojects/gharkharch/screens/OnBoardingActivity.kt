package com.trycatchprojects.gharkharch.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.trycatchprojects.gharkharch.MainActivity
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        binding.btnGetStarted.setOnClickListener {
            val myName = binding.edtMyName.text.toString()

            if (myName.isNotEmpty() && myName.length <=12) {
                sharedPreferences.edit().putString("myName", myName).apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Show an error message or handle the case where the input is invalid
                binding.edtMyName.error = "Name must be exactly 12 characters"
            }
        }
    }
}
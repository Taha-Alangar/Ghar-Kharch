package com.trycatchprojects.gharkharch.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.trycatchprojects.gharkharch.MainActivity
import com.trycatchprojects.gharkharch.R
import com.trycatchprojects.gharkharch.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        Handler(Looper.getMainLooper()).postDelayed({
            val myName = sharedPreferences.getString("myName", null)
            val nextActivity = if (myName.isNullOrEmpty()) {
                OnBoardingActivity::class.java
            } else {
                MainActivity::class.java
            }
            startActivity(Intent(this, nextActivity))
            finish()
        }, 1000)
    }
}
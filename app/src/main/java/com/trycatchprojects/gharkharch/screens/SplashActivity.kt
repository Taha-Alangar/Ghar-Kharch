package com.trycatchprojects.gharkharch.screens

import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
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
        }, 1300)
        val textView: TextView = findViewById(R.id.textView)
        val text = "Ghar Kharch"
        animateTextView(textView, text)
    }
    private fun animateTextView(textView: TextView, text: String) {
        val handler = Handler(Looper.getMainLooper())
        val delay = 100L // Delay between each letter appearance

        ValueAnimator.ofInt(0, text.length).apply {
            duration = (delay * text.length).toLong()
            addUpdateListener { animation ->
                val index = animation.animatedValue as Int
                textView.text = text.substring(0, index)
            }
            start()
        }
    }
}
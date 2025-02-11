package com.example.foodify.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodify.R
import com.example.foodify.authentication.ui.AuthActivity
import com.example.foodify.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupSplashScreen()

    }

    private fun setupSplashScreen() {
        val lottieAnimationView = binding.lottieAnimationView
        lottieAnimationView.playAnimation()

        // Wait for Lottie animation to complete
        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(animation: Animator) {
                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                finish()
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }
}
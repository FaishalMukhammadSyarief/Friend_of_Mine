package com.zhalz.friendofmine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.zhalz.friendofmine.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        binding.access = this

    }
    fun toMain() {
        val toMain = Intent(this, MainActivity::class.java)
        startActivity(toMain)
    }
}
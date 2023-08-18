package com.zhalz.friendofmine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.zhalz.friendofmine.databinding.ActivityLetsBeBinding

class LetsBeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLetsBeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lets_be)

        binding.access = this

    }
    fun toMain() {
        val toMain = Intent(this, HomeActivity::class.java)
        startActivity(toMain)
        finish()
    }
}
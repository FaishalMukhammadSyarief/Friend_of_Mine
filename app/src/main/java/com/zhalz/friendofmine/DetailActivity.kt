package com.zhalz.friendofmine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.zhalz.friendofmine.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private lateinit var getName: String
    private lateinit var getSchool: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        val getName = intent.getStringExtra("name")
        val getSchool = intent.getStringExtra("school")

        binding.name = getName
        binding.school = getSchool
    }
}
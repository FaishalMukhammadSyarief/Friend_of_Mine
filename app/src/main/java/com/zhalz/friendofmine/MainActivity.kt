package com.zhalz.friendofmine

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.zhalz.friendofmine.adapter.RvAdapter
import com.zhalz.friendofmine.database.FriendEntity
import com.zhalz.friendofmine.database.MyDatabase
import com.zhalz.friendofmine.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myDatabase: MyDatabase

    private var adapter: RvAdapter? = null

    private val listFriend = ArrayList<FriendEntity>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.activity = this

        myDatabase = MyDatabase.getDatabase(this)

        lifecycleScope.launch {
            myDatabase.friendDao().getAll().collect {
                listFriend.clear()
                adapter?.notifyDataSetChanged()
                listFriend.addAll(it)
                adapter?.notifyItemInserted(0)
            }
        }

        adapter = RvAdapter(listFriend) {data ->
            val toDetail = Intent(this, DetailActivity::class.java).apply {
                putExtra("name", data.name)
                putExtra("school", data.school)
                putExtra("id", data.id)
            }
            startActivity(toDetail)
        }

        binding.setAdapter = adapter

    }

    fun toAdd() {
        val toAdd = Intent(this, AddActivity::class.java)
        startActivity(toAdd)
    }
}
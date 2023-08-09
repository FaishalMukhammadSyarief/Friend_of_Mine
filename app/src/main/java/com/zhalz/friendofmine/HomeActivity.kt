package com.zhalz.friendofmine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.zhalz.friendofmine.adapter.RvAdapter
import com.zhalz.friendofmine.database.FriendEntity
import com.zhalz.friendofmine.database.MyDatabase
import com.zhalz.friendofmine.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var myDatabase: MyDatabase

    private val listFriend = ArrayList<FriendEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        binding.access = this

        myDatabase = MyDatabase.getDatabase(this)

        lifecycleScope.launch {
            myDatabase.friendDao().getAll().collect {
                listFriend.clear()
                binding.recycler.adapter?.notifyDataSetChanged()
                listFriend.addAll(it)
                binding.recycler.adapter?.notifyItemInserted(0)
            }
        }
        binding.setAdapter = RvAdapter(listFriend) { data ->
            val toDetail = Intent(this, DetailActivity::class.java).apply {
                putExtra("photo", data.photo)
                putExtra("name", data.name)
                putExtra("school", data.school)
                putExtra("bio", data.bio)
                putExtra("id", data.id)
            }
            startActivity(toDetail)
        }

    }

    fun toAdd() {
        val toAdd = Intent(this, AddActivity::class.java)
        startActivity(toAdd)
    }
}
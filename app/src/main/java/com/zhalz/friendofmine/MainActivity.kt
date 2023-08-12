package com.zhalz.friendofmine

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.zhalz.friendofmine.adapter.RvAdapter
import com.zhalz.friendofmine.database.FriendEntity
import com.zhalz.friendofmine.database.MyDatabase
import com.zhalz.friendofmine.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myDatabase: MyDatabase

    private val listFriend = ArrayList<FriendEntity>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

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
                putExtra("name", data.name)
                putExtra("school", data.school)
                putExtra("id", data.id)
            }
            startActivity(toDetail)
        }

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<FriendEntity>()
            for (i in listFriend) {
                if (i.name.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Data not Found", Toast.LENGTH_SHORT).show()
            }

            else {
                binding.setAdapter?.items = filteredList
                binding.setAdapter?.notifyDataSetChanged()
            }
        }
    }

    fun toAdd() {
        val toAdd = Intent(this, AddActivity::class.java)
        startActivity(toAdd)
    }

}
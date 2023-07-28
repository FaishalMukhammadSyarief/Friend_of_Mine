package com.zhalz.friendofmine

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
    private lateinit var roomDatabase: MyDatabase

    private var adapter: RvAdapter? = null

    private val listFriend = ArrayList<FriendEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.activity = this

        /*val friendsList = arrayListOf(
            FriendEntity("Faishal Mukhammad", "SMKN 1 Purwokerto"),
            FriendEntity("Fabe Bustanil", "SMKN 1 Purwokerto"),
            FriendEntity("Alfred Aisytiens", "SMKN 1 Purwokerto")
        )*/


        roomDatabase = MyDatabase.getDatabase(this)

        lifecycleScope.launch {
            roomDatabase.friendDao().getAll().collect {
                listFriend.clear()
                adapter?.notifyDataSetChanged()
                listFriend.addAll(it)
                adapter?.notifyItemInserted(0)
            }
        }

        binding.setAdapter = RvAdapter(listFriend)

    }

    fun toAdd() {
        val toDetail = Intent(this, AddActivity::class.java)
        startActivity(toDetail)
    }
}
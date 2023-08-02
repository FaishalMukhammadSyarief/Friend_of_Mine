package com.zhalz.friendofmine

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.zhalz.friendofmine.database.FriendEntity
import com.zhalz.friendofmine.database.MyDatabase
import com.zhalz.friendofmine.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private lateinit var myDatabase: MyDatabase

    private var dataFriend: FriendEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        binding.access = this

        myDatabase = MyDatabase.getDatabase(this)

        val name = intent.getStringExtra("name") ?: ""
        val school = intent.getStringExtra("school") ?: ""
        val friendId = intent.getIntExtra("id", 0)

        binding.name = name
        binding.school = school

        dataFriend = FriendEntity(name, school).apply {
            id = friendId
        }

    }

    fun onDeleteClick() {
        val adBuilder = AlertDialog.Builder(this)

        adBuilder.setTitle("Remove Friend")
        adBuilder.setMessage("Are you sure to remove this friend?")

        adBuilder.setPositiveButton("Remove") { dialog: DialogInterface, _: Int ->
            dataFriend?.let {
                lifecycleScope.launch {
                    myDatabase.friendDao().delete(it)
                    dialog.dismiss()
                    Toast.makeText(this@DetailActivity, "Removed", Toast.LENGTH_SHORT).show()
                    finish()
//                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        adBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        adBuilder.create().show()
    }
}
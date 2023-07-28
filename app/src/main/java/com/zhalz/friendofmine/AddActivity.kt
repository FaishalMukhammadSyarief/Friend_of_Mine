package com.zhalz.friendofmine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.zhalz.friendofmine.database.FriendEntity
import com.zhalz.friendofmine.database.MyDatabase
import com.zhalz.friendofmine.databinding.ActivityAddBinding
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding

    private lateinit var myDatabase: MyDatabase

    var name = ""
    var school = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add)

        binding.activity = this

        myDatabase = MyDatabase.getDatabase(this)
    }

    fun saveNewFriend() {

        if (name.isEmpty() || school.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show()
        }
        else {
            val newFriend = FriendEntity(name, school)

            lifecycleScope.launch {
                myDatabase.friendDao().insert(newFriend)
                Toast.makeText(this@AddActivity, "Succeed", Toast . LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
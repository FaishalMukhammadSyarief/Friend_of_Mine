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
import com.zhalz.friendofmine.databinding.ActivityEditBinding
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var myDatabase: MyDatabase
    private var dataFriend : FriendEntity? = null

    var name = ""
    var school = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit)

        binding.access = this

        myDatabase = MyDatabase.getDatabase(this)

        name = intent.getStringExtra("name2") ?: ""
        school = intent.getStringExtra("school2") ?: ""

    }

    fun saveUpdateFriend() {
        val adBuilder = AlertDialog.Builder(this)

        adBuilder.setTitle("Update Friend")
        adBuilder.setMessage("Are you sure to update this friend?")

        adBuilder.setPositiveButton("Update") { dialog: DialogInterface, _: Int ->
            dataFriend?.let {
                lifecycleScope.launch {
                    myDatabase.friendDao().update(it)
                    dialog.dismiss()
                    Toast.makeText(this@EditActivity, "Updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        adBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        adBuilder.create().show()

    }
}
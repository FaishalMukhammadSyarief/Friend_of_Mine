package com.zhalz.friendofmine

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
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

    var photo = ""
    var name = ""
    var school = ""
    var bio = ""
    var idFriend = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        binding.access = this

        myDatabase = MyDatabase.getDatabase(this)

        //get data
        photo = intent.getStringExtra("photo") ?: ""
        name = intent.getStringExtra("name") ?: ""
        school = intent.getStringExtra("school") ?: ""
        bio = intent.getStringExtra("bio") ?: ""
        idFriend = intent.getIntExtra("id", 0)

        dataFriend = FriendEntity(photo, name, school, bio).apply {
            id = idFriend
        }
    }

    //delete
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
                }
            }
        }
        adBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        adBuilder.create().show()
    }

    //edit
    fun saveUpdateFriend() {
        val updatedFriend = FriendEntity(photo, name, school, bio).apply {
            id = idFriend
        }

        lifecycleScope.launch {
            myDatabase.friendDao().update(updatedFriend)
            Toast.makeText(this@DetailActivity, "Updated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    //edit
    fun onEditClick() {
        binding.ivPhotoDetail.isEnabled = true
        binding.etName.isEnabled = true
        binding.etSchool.isEnabled = true
        binding.etBio.isEnabled = true

        binding.btnEdit.isVisible = false
        binding.btnEdit.isEnabled = false

        binding.btnSave.isVisible = true

    }
}
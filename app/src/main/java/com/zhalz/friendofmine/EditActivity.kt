package com.zhalz.friendofmine

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.room.Database
import com.zhalz.friendofmine.bitmap.BitmapHelper
import com.zhalz.friendofmine.database.FriendEntity
import com.zhalz.friendofmine.database.MyDatabase
import com.zhalz.friendofmine.databinding.ActivityEditBinding
import kotlinx.coroutines.launch
import java.io.File

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var myDatabase: MyDatabase
    private var dataFriend : FriendEntity? = null

    private lateinit var photoFile: File
    private val photoName = "photo.jpg"
    private val tag = "friendData"

    var photo = ""
    var name = ""
    var school = ""
    var bio = ""
    var idFriend = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit)

        binding.access = this

        binding.ivPhotoEdit.background = null

        myDatabase = MyDatabase.getDatabase(this)

        name = intent.getStringExtra("name") ?: ""
        school = intent.getStringExtra("school") ?: ""
        bio = intent.getStringExtra("bio") ?: ""
        photo = intent.getStringExtra("photo") ?: ""
        idFriend = intent.getIntExtra("id", 0)

        photoFile = getPhotoFileUri(photoName)

        val bitmap = BitmapHelper().stringToBitmap(this, photo)
        Log.d("TestBitmap", "DATA_BITMAP : $bitmap")
        binding.ivPhotoEdit.setImageBitmap(bitmap)

        binding.ivPhotoEdit.setOnClickListener {
            selectImage()
        }
    }

    private fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), tag)
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdir()) {
            Log.d(tag, "Failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    private fun checkPermissionCamera(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionCamera() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
    }

    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            110
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openCamera()
            } else {
                Toast.makeText(this, "User tidak memberikan izin Camera", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == 110) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openGallery()
            } else {
                Toast.makeText(this, "User tidak memberikan izin Gallery", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private var activityLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            binding.ivPhotoEdit.setImageBitmap(takenImage)
        }

    private var activityLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                try {
                    val bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                    binding.ivPhotoEdit.setImageBitmap(bitmap)
                    photo = BitmapHelper().bitmapToString(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun selectImage() {
        binding.ivPhotoEdit.setImageResource(0)
        val items = arrayOf<CharSequence>(
            "Take Photo with Camera", "Choose from Gallery",
            "Cancel"
        )
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@EditActivity)
        builder.setTitle("Add Photo Profile !")
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Take Photo with Camera") {
                if (checkPermissionCamera()) {
                    openCamera()
                } else {
                    requestPermissionCamera()
                }
            } else if (items[item] == "Choose from Gallery") {
                if (checkPermissionGallery()) {
                    openGallery()
                } else {
                    requestPermissionGallery()
                }
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val fileProvider =
            FileProvider.getUriForFile(this, "com.zhalz.friendofmine.fileProvider", photoFile)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        }
        activityLauncherCamera.launch(takePictureIntent)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
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
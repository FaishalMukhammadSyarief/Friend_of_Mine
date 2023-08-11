package com.zhalz.friendofmine

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
import com.zhalz.friendofmine.bitmap.BitmapHelper
import com.zhalz.friendofmine.database.FriendEntity
import com.zhalz.friendofmine.database.MyDatabase
import com.zhalz.friendofmine.databinding.ActivityAddBinding
import kotlinx.coroutines.launch
import java.io.File

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private lateinit var myDatabase: MyDatabase
    private lateinit var photoFile: File

    private val photoName = "photo.jpg"
    private val tag = "bioFriend"

    var photo = ""
    var name = ""
    var school = ""
    var bio = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add)

        binding.access = this

        binding.ivPhotoAdd.background = null

        photoFile = getPhotoFileUri(photoName)

        myDatabase = MyDatabase.getDatabase(this)

        binding.ivPhotoAdd.setOnClickListener {
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
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionCamera() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
    }

    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 110)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
                Toast.makeText(this, "User tidak memberikan izin Gallery", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var activityLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
        binding.ivPhotoAdd.setImageBitmap(takenImage)
    }


    private var activityLauncherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                binding.ivPhotoAdd.setImageBitmap(bitmap)
                photo = BitmapHelper().bitmapToString(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun selectImage() {
        binding.ivPhotoAdd.setImageResource(0)
        val items = arrayOf<CharSequence>("Take Photo with Camera", "Choose from Gallery",
            "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@AddActivity)
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
        val fileProvider = FileProvider.getUriForFile(this, "com.zhalz.friendofmine.fileProvider", photoFile)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        }
        activityLauncherCamera.launch(takePictureIntent)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
    }


    fun saveNewFriend() {
        val name = binding.etName.text.trim().toString()
        val school = binding.etSchool.text.trim().toString()
        val bio = binding.etBio.text.trim().toString()

        if (name.isEmpty() || school.isEmpty() || bio.isEmpty() || photo.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show()

            Log.d("CekForm", "PHOTO:$photo")
            Log.d("CekForm", "NAME:$name")
            Log.d("CekForm", "SCHOOL:$school")
            Log.d("CekForm", "BIO:$bio")
        }
            val newFriend = FriendEntity(name, school, bio, photo)

            lifecycleScope.launch {
                myDatabase.friendDao().insert(newFriend)
                Toast.makeText(this@AddActivity, "Succeed", Toast.LENGTH_SHORT).show()
                finish()
        }
    }

}
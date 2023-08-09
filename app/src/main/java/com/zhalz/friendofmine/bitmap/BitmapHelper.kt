package com.zhalz.friendofmine.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.zhalz.friendofmine.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Random

class BitmapHelper {
    companion object{
        @JvmStatic
        @BindingAdapter("imageSrc")
        fun ImageView.setImageSrc(imagePhoto: String?){
            imagePhoto.let {
                val bitmapPhoto = BitmapHelper().stringToBitmap(this.context, imagePhoto)
                this.setImageBitmap(bitmapPhoto)
            }
        }
    }
    fun bitmapToString(image: Bitmap): String {
        var baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var b = baos.toByteArray()
        var temp: String? = null
        try {
            System.gc()
            temp = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            b = baos.toByteArray()
            temp = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
        }
        return temp ?: ""
    }

    fun stringToBitmap(context: Context, input: String?): Bitmap {
        val decodedByte = android.util.Base64.decode(input, 0)

        val sdCardDirectory = File(context.cacheDir, "")

        val rand = Random()

        val randomNum = rand.nextInt((1000 - 0) + 1) + 0

        val nw = "IMG_$randomNum.txt"
        val image = File(sdCardDirectory, nw)

        var outStream: FileOutputStream? = null
        try {
            outStream = FileOutputStream(image)
            outStream.write(input!!.toByteArray())
            outStream.flush()
            outStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return try {
            BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            val iss = context.resources.openRawResource(R.raw.error)
            BitmapFactory.decodeStream(iss)
        } catch (e: Exception) {
            e.printStackTrace()
            val iss = context.resources.openRawResource(R.raw.error)
            BitmapFactory.decodeStream(iss)
        }
    }
}
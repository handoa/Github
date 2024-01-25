package com.example.github

import com.bumptech.glide.Glide
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_PERMISSION_CODE = 123
        private const val PICK_IMAGE_REQUEST_CODE = 456
    }

    private lateinit var storageRef: StorageReference
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun getOneYearAgoDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private val lastYearDate = getOneYearAgoDate()
    inner class FirebaseStorageHelper {
        private val storage = FirebaseStorage.getInstance()
        private val storageRef = storage.reference

        fun uploadImageToFirebase(selectedImageUri: Uri): Uri? {
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

            return try {
                val uploadTask = imageRef.putFile(selectedImageUri)
                Tasks.await(uploadTask)
                imageRef.downloadUrl.result
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        suspend fun getImageUrlForDate(date: String): Uri? {
            return try {
                val imageRef = storageRef.child("images/image_$date.jpg")
                val url = Tasks.await(imageRef.downloadUrl)
                url
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private val firebaseStorageHelper = FirebaseStorageHelper()

    private fun loadImageForLastYear() {
        GlobalScope.launch(Dispatchers.IO) {
            val imageUrl = firebaseStorageHelper.getImageUrlForDate(lastYearDate)
            withContext(Dispatchers.Main) {
                imageUrl?.let { url ->
                    Glide.with(this@MainActivity)
                        .load(url)
                        .into(imageView)
                }
            }
        }
    }


    }


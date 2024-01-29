package com.example.github
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
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
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.github.network.DisplayImageActivity
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
import kotlin.random.Random

class activity_myCloset : AppCompatActivity() {
    private lateinit var iv: ImageView
    private lateinit var imageView: ImageView
    lateinit var toolbar: Toolbar
    lateinit var actionBar: ActionBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_closet)


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기


        checkSelfPermission()

        iv = findViewById(R.id.btnChooseImage)
        iv.setOnClickListener {
            val intent = Intent().apply {
                type = MediaStore.Images.Media.CONTENT_TYPE
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(intent, 101)
        }

        imageView = findViewById(R.id.imageView)
        if (checkSelfPermission()) {
            setRandomImageFromLastYear()
        }



    }



    private fun setRandomImageFromLastYear() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        val endDate = calendar.timeInMillis
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.timeInMillis

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ? AND ${MediaStore.Images.Media.DATE_TAKEN} <= ?"
        val selectionArgs = arrayOf(startDate.toString(), endDate.toString())

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        val imageIds = mutableListOf<Long>()
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                imageIds.add(it.getLong(idColumn))
            }
        }

        if (imageIds.isNotEmpty()) {
            val randomId = imageIds[Random.nextInt(imageIds.size)]
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                .appendPath(randomId.toString()).build()
            imageView.setImageURI(uri)
        }
    }

    private fun checkSelfPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setRandomImageFromLastYear()
        }
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                //뒤로가기 눌렀을 때
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



}





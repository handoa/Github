package com.example.github
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import java.util.Calendar
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.github.adapter.ImageAdapter
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

    // private val permissionsRequestCode = 100
   // private val pickImageRequestCode = 101

    companion object {
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 102 // 값 변경
    }

    private lateinit var imageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    lateinit var toolbar: Toolbar
    lateinit var actionBar: ActionBar

    // ResultLauncher for picking image
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    imageView.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(this, "이미지를 선택하지 않았습니다", Toast.LENGTH_SHORT).show()
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_closet)


        imageView = findViewById(R.id.imageView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        checkSelfPermission()
        imageView.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                }
            pickImage.launch(intent)
        }






        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기


        /* 저장소 읽기 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 이미 부여된 경우
            loadImagesFromLastYear()
        } else {
            // 권한이 부여되지 않은 경우 권한 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
        }

        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
                // 권한 요청 결과 확인
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 부여된 경우
                    loadImagesFromLastYear()
                } else {
                    // 권한이 거부된 경우
                    Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun loadImagesFromLastYear() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val images = mutableListOf<Uri>()

            // 현재 날짜에서 1년 전으로 설정하고, 한 달의 기간을 설정
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -1) // 현재로부터 1년 전
            val startDate = calendar.timeInMillis
            calendar.add(Calendar.MONTH, 1) // 1년 전 날짜로부터 한 달 후
            val endDate = calendar.timeInMillis

            val projection = arrayOf(MediaStore.Images.Media._ID)
            val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ? AND ${MediaStore.Images.Media.DATE_TAKEN} <= ?"
            val selectionArgs = arrayOf(startDate.toString(), endDate.toString())

            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    images.add(uri)
                }
            }

            imageAdapter = ImageAdapter(images)
            recyclerView.adapter = imageAdapter
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
        }
    }





    companion object {
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 101
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadImagesFromLastYear()
        }
    }*/








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

    // 권한 확인 및 요청 함수
    private fun checkSelfPermission() {
        Log.d("Permissions", "Checking READ_EXTERNAL_STORAGE permission")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("Permissions", "READ_EXTERNAL_STORAGE permission not granted, requesting")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            Log.d("Permissions", "READ_EXTERNAL_STORAGE permission already granted")
            loadImagesFromLastYear()
        }
    }

    // 권한 요청 결과 처리 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "READ_EXTERNAL_STORAGE permission granted")
                loadImagesFromLastYear()
            } else {
                Log.d("Permissions", "READ_EXTERNAL_STORAGE permission denied")
                Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


        private fun loadImagesFromLastYear() {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val images = mutableListOf<Uri>()

                // 현재 날짜에서 1년 전으로 설정하고, 한 달의 기간을 설정
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.YEAR, -1) // 현재로부터 1년 전
                val startDate = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1) // 1년 전 날짜로부터 한 달 후
                val endDate = calendar.timeInMillis

                val projection = arrayOf(MediaStore.Images.Media._ID)
                val selection =
                    "${MediaStore.Images.Media.DATE_TAKEN} >= ? AND ${MediaStore.Images.Media.DATE_TAKEN} <= ?"
                val selectionArgs = arrayOf(startDate.toString(), endDate.toString())

                val cursor = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    "${MediaStore.Images.Media.DATE_TAKEN} DESC"
                )

                cursor?.use {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    while (it.moveToNext()) {
                        val id = it.getLong(idColumn)
                        val uri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                        images.add(uri)
                    }
                }

                imageAdapter = ImageAdapter(images)
                recyclerView.adapter = imageAdapter
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
                )
            }
        }





}






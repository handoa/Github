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
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
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
    private var mCameraPhotoPath: Uri? = null
    private lateinit var imageView: ImageView  // 이미지를 표시할 ImageView 추가
    lateinit var weatherView: WebView
    //lateinit var realtimeTalk : RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var myCloset: ImageView
    lateinit var ootd: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        storageRef = FirebaseStorage.getInstance().reference.child("images")
        imageView = findViewById(R.id.imageView)

        fun checkGalleryPermission(): Boolean {
            return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        // 권한을 요청하는 함수
        fun requestGalleryPermission() {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        }

        // 권한 확인 및 갤러리 열기
        if (checkPermissions()) {
            findViewById<Button>(R.id.btnChooseImage).setOnClickListener {
                checkGalleryPermission()
                openGallery()
            }
        } else {
            requestGalleryPermission()
        }

        // Load image for last year
        loadImageForLastYear()
    }

    //메뉴 리소스 XML의 내용을 앱바(App Bar)에 반영
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        //return super.onCreateOptionsMenu(menu)
        return true
    }

    //앱바(App Bar)에 표시된 액션 또는 오버플로우 메뉴가 선택되면
    //액티비티의 onOptionsItemSelected() 메소드가 호출
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.toolbar_myPage -> {
                //마이페이지 아이콘 눌렀을 때
                Toast.makeText(applicationContext, "마이페이지 이동", Toast.LENGTH_LONG).show()
                //var intentToMyPage = Intent(this, activity_myPage::class.java)
                //startActivity(intentToMyPage)
                return super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

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


    }


    private val firebaseStorageHelper = FirebaseStorageHelper()

    // 이미지 선택 후 업로드를 수행할 부분
    private fun uploadSelectedImageToFirebase(selectedImageUri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            firebaseStorageHelper.uploadImageToFirebase(selectedImageUri)
        }
    }


    private val lastYearDate = getOneYearAgoDate()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            GlobalScope.launch(Dispatchers.IO) {
                selectedImageUri?.let {
                    uploadSelectedImageToFirebase(it)
                }
            }
        }
    }
}









package com.example.github

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

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_PERMISSION_CODE = 123
        private const val PICK_IMAGE_REQUEST_CODE = 456
        private const val IMG_MIME_TYPE = "image/*"
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PERMISSION = 2
        private const val TEMP_IMG_REMOVE_INTERVAL = 24 * 60 * 60 * 1000L // 24 hours
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
        imageView = findViewById(R.id.imageView)  // ImageView 초기화
        //val btnChooseImage: Button = findViewById(R.id.btnChooseImage)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정

        //날씨 정보 클릭 시 날씨화면으로 이동
        weatherView.setOnClickListener {
            var intentToWeatherPage = Intent(this, activity_weatherPage::class.java)
            startActivity(intentToWeatherPage)
        }

        //실시간 날씨 토크 클릭 시 실시간 날씨 정보 공유 화면으로 이동
        /*realtimeTalk.setOnClickListener {
            var intentToRealtimeTalk = Intent(this, activity_realtimeTalk::class.java)
            startActivity(intentToRealtimeTalk)
        }*/

        //작년 입은 옷 사진 클릭 시 이동
        myCloset.setOnClickListener {
            var intentToMyCloset = Intent(this, activity_myCloset::class.java)
            startActivity(intentToMyCloset)
        }

        //ootd 사진 클릭 시 이동
        ootd.setOnClickListener {
            var intentToOotd = Intent(this, activity_ootd::class.java)
            startActivity(intentToOotd)
        }


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
            // 권한이 없는 경우 권한 요청
            requestGalleryPermission()
            /*ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )*/
        }
    }

    //메뉴 리소시 XML의 내용을 앱바(App Bar)에 반영
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

        suspend fun uploadImageToFirebase(selectedImageUri: Uri): Uri? {
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

            //val uploadTask = imageRef.putFile(selectedImageUri)

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
            // Firebase에 이미지 업로드를 수행
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            GlobalScope.launch(Dispatchers.IO) {
                selectedImageUri?.let {

                    /*val imageUrl = firebaseStorageHelper.uploadImageToFirebase(it)
                    withContext(Dispatchers.Main) {
                        imageUrl?.let { url ->
                            Glide.with(this@MainActivity)
                                .load(url)
                                .into(imageView)
                        }
                    }*/
                }
            }
        }



    }
}









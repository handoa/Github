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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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

class MainActivity : AppCompatActivity() {
    private lateinit var iv: ImageView
    private lateinit var weatherView: Button
    lateinit var realtimeTalk : LinearLayout
    lateinit var toolbar: Toolbar
    lateinit var myCloset: ImageView
    lateinit var ootd: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.include)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        supportActionBar?.setDisplayShowTitleEnabled(false)

        weatherView = findViewById(R.id.weatherView)
        realtimeTalk = findViewById(R.id.realtimeTalk)
        myCloset = findViewById(R.id.imageView_myCloset)
        myCloset.clipToOutline = true
        ootd = findViewById(R.id.imageView_ootd)

        //날씨 정보 클릭 시 날씨화면으로 이동
        weatherView.setOnClickListener {
            var intentToWeatherPage = Intent(this, activity_weatherPage::class.java)
            startActivity(intentToWeatherPage)
        }

        //실시간 날씨 토크 클릭 시 실시간 날씨 정보 공유 화면으로 이동
        realtimeTalk.setOnClickListener {
            var intentToRealtimeTalk = Intent(this, activity_realtimeTalk::class.java)
            startActivity(intentToRealtimeTalk)
        }

        //작년 입은 옷 사진 클릭 시 이동
        /*myCloset.setOnClickListener {
            var intentToMyCloset = Intent(this, activity_myCloset::class.java)
            startActivity(intentToMyCloset)
        }*/

        //ootd 사진 클릭 시 이동
        ootd.setOnClickListener {
            var intentToOotd = Intent(this, activity_ootd::class.java)
            startActivity(intentToOotd)
        }

        checkSelfPermission()

        iv = findViewById(R.id.imageView_myCloset)
        iv.setOnClickListener {
            val intent = Intent().apply {
                type = MediaStore.Images.Media.CONTENT_TYPE
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(intent, 101)
        }


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
                var intentToMyPage = Intent(this, activity_myPage::class.java)
                startActivity(intentToMyPage)
                return super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            grantResults.indices.forEach { i ->
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "권한 허용: ${permissions[i]}")
                }
            }
        }
    }

    private fun checkSelfPermission() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 1)
        } else {
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == RESULT_OK) {
            try {
                val inputStream = data?.data?.let { contentResolver.openInputStream(it) }
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                iv.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == 101 && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show()
        }
    }

    }


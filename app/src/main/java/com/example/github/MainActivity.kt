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
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
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

    private lateinit var weatherlayout: ConstraintLayout
    lateinit var realtimeTalk: LinearLayout
    lateinit var toolbar: Toolbar
    lateinit var myCloset: ImageView
    lateinit var ootd: ImageView
    private var doubleBackToExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.include)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        supportActionBar?.setDisplayShowTitleEnabled(false)

        weatherlayout = findViewById(R.id.weatherlayout)
        realtimeTalk = findViewById(R.id.realtimeTalk)
        myCloset = findViewById(R.id.imageView_myCloset)
        myCloset.clipToOutline = true
        ootd = findViewById(R.id.imageView_ootd)

        //날씨 정보 클릭 시 날씨화면으로 이동
        weatherlayout.setOnClickListener {
            var intentToWeatherPage = Intent(this, MainActivity2::class.java)
            startActivity(intentToWeatherPage)
        }

        //실시간 날씨 토크 클릭 시 실시간 날씨 정보 공유 화면으로 이동
        realtimeTalk.setOnClickListener {
            var intentToRealtimeTalk = Intent(this, activity_realtimeTalk::class.java)
            startActivity(intentToRealtimeTalk)
        }

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


    }

    //메뉴 리소스 XML의 내용을 앱바(App Bar)에 반영
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        // return super.onCreateOptionsMenu(menu)
        return true
    }


    //앱바(App Bar)에 표시된 액션 또는 오버플로우 메뉴가 선택되면
    //액티비티의 onOptionsItemSelected() 메소드가 호출
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.toolbar_myPage -> {
                //마이페이지 아이콘 눌렀을 때
                var intentToMyPage = Intent(this, activity_myPage::class.java)
                startActivity(intentToMyPage)
                return super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() { //뒤로가기 두 번 눌러야 앱 종료 가능
        if (doubleBackToExit) {
            finishAffinity()
        } else {
            Toast.makeText(this, "종료하시려면 뒤로가기를 한번 더 눌러주세요", Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed(1500L) { //1.5초 이내에 한 번 더 탭해야 앱 종료 가능
                doubleBackToExit = false
            }
        }
    }

    fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }
}


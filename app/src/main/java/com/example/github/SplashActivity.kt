package com.example.github

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //일정 시간 지연 이후 실행
        Handler(Looper.getMainLooper()).postDelayed({

            //일정 시간 지나면 로그인 액티비티로 이동
            val intent = Intent(this, activity_login::class.java)
            startActivity(intent)

            finish()
        }, 2000) //2초 딜레이
    }
}
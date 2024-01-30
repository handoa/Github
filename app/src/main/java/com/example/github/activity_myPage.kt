package com.example.github

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class activity_myPage : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var actionBar: ActionBar
    lateinit var btnLogout: Button

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기

        auth = Firebase.auth
        btnLogout = findViewById(R.id.btnLogout)

        //로그아웃 버튼 클릭 시 로그아웃 후 로그인 페이지로 이동
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            var intentToLogin = Intent(this, activity_login::class.java)
            intentToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentToLogin)
            Toast.makeText(this, "로그아웃 완료", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            android.R.id.home -> {
                //뒤로가기 눌렀을 때
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

   /* fun getUid(): String { //firebase에서 uid를 가져옴
        return auth.currentUser?.uid.toString()
    }*/
}
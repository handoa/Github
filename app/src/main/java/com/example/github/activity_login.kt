package com.example.github

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class activity_login : AppCompatActivity() {
    lateinit var loginButton: Button
    lateinit var joinTextView: TextView
    lateinit var idEditText: EditText
    lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)
        joinTextView = findViewById(R.id.joinTextView)
        idEditText = findViewById(R.id.idEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        //로그인 버튼 클릭 시 메인화면으로 이동
        loginButton.setOnClickListener {
            var intentToMain = Intent(this, MainActivity::class.java)
            startActivity(intentToMain)
        }

        //회원가입 버튼 클릭 시 회원가입 페이지로 이동
        joinTextView.setOnClickListener {
            var intentToSignup = Intent(this, SignupActivity::class.java)
            startActivity(intentToSignup)
        }
    }
}

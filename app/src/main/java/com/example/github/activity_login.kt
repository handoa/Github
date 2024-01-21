package com.example.github

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    lateinit var loginButton: Button
    lateinit var joinButton: Button
    lateinit var idEditText: EditText
    lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)
        joinButton = findViewById(R.id.joinButton)
        idEditText = findViewById(R.id.idEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        joinButton.setOnClickListener {
            // 회원가입 창으로 이동
            val intent = Intent(this@MainActivity, SignupActivity::class.java)
            startActivity(intent)



        }
    }
}
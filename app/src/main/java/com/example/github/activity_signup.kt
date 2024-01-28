package com.example.github

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        val newId: EditText = findViewById(R.id.new_id)
        val newPw: EditText = findViewById(R.id.new_pw)
        val newName: EditText = findViewById(R.id.new_name)
        val newTel: EditText = findViewById(R.id.new_tel)
        val btnSignup: Button = findViewById(R.id.btnSignup)

        btnSignup.setOnClickListener {
            val id = newId.text.toString()
            val password = newPw.text.toString()
            val name = newName.text.toString()
            val contact = newTel.text.toString()

            var intent = Intent(this, activity_login::class.java)
            startActivity(intent)

            val message = "ID: $id\nPassword: $password\nName: $name\nContact: $contact"
            Toast.makeText(this@SignupActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}


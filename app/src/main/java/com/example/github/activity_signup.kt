package com.example.github

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        val editTextID: EditText = findViewById(R.id.editTextID)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextContact: EditText = findViewById(R.id.editTextContact)
        val buttonSignUp: Button = findViewById(R.id.buttonSignUp)

        buttonSignUp.setOnClickListener {
            val id = editTextID.text.toString()
            val password = editTextPassword.text.toString()
            val name = editTextName.text.toString()
            val contact = editTextContact.text.toString()





            val message = "ID: $id\nPassword: $password\nName: $name\nContact: $contact"
            Toast.makeText(this@SignupActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}


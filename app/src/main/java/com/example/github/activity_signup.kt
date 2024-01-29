package com.example.github

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val newId: EditText = findViewById(R.id.new_id)
        val newPw: EditText = findViewById(R.id.new_pw)
        val newName: EditText = findViewById(R.id.new_name)
        val newTel: EditText = findViewById(R.id.new_tel)
        val btnSignup: Button = findViewById(R.id.btnSignup)

        auth = Firebase.auth

        btnSignup.setOnClickListener {
            createAccount(newId.text.toString(), newPw.text.toString())

            val id = newId.text.toString()
            val password = newPw.text.toString()
            val name = newName.text.toString()
            val contact = newTel.text.toString()

            var intent = Intent(this, activity_login::class.java)
            startActivity(intent)

            /*val message = "ID: $id\nPassword: $password\nName: $name\nContact: $contact"
            Toast.makeText(this@SignupActivity, message, Toast.LENGTH_SHORT).show()*/
        }
    }

    //회원가입 함수
    private fun createAccount(id: String, password: String) {

        if (id.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(id, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this, "계정 생성 완료.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish() // 가입창 종료
                    } else {
                        Toast.makeText(
                            this, "계정 생성 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    fun signUp(id: String, password: String) {
        auth.createUserWithEmailAndPassword(id, password)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    //Firebase DB에 저장되어 있는 계정 아닐 경우 새로 등록
                    goToLoginActivity(task.result?.user)
                } else if (task.exception?.message.isNullOrEmpty()) {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    //(id, password)
                }
            }
    }

    //회원가입 성공 시 로그인 화면으로 이동
    fun goToLoginActivity(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, activity_login::class.java))
        }
    }

}


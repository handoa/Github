package com.example.github

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.github.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var newId: EditText
    lateinit var newPw: EditText
    lateinit var newName: EditText
    lateinit var newTel: EditText
    lateinit var btnSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth
        newId = findViewById(R.id.new_id)
        newPw = findViewById(R.id.new_pw)
        newName = findViewById(R.id.new_name)
        newTel = findViewById(R.id.new_tel)
        btnSignup = findViewById(R.id.btnSignup)

        initializeView()
        initializeListener()

    }

    //뷰 초기화
    fun initializeView(){
        auth = FirebaseAuth.getInstance()
    }

    //버튼 클릭 시 리스너 초기화
    fun initializeListener(){
        btnSignup.setOnClickListener() {
            createAccount(newId.text.toString(), newPw.text.toString())
        }
    }

    //회원가입 함수
    fun createAccount(id: String, password: String) {

        val userId = newId.text.toString()
        val userName = newName.text.toString()
        val userTel = newTel.text.toString()

        if (id.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(id, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        FirebaseDatabase.getInstance().getReference("User").child("users").child(id).setValue((User(userId, userName, userTel)))
                        Toast.makeText(
                            this, "계정 생성 완료.\n로그인해주세요.", Toast.LENGTH_SHORT).show()
                        finish() // 가입창 종료
                    } else {
                        Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_SHORT).show()
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


package com.example.github

import android.content.Intent
import android.content.LocusId
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.github.data.User
import com.example.github.data.userId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var newId: EditText
    lateinit var newEmail: EditText
    lateinit var newPw: EditText
    lateinit var newName: EditText
    lateinit var newTel: EditText
    lateinit var btnSignup: Button
    private var uid : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        database = Firebase.database.reference
        auth = Firebase.auth
        newId = findViewById(R.id.new_id)
        newEmail = findViewById(R.id.new_email)
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
            createAccount(newEmail.text.toString(), newPw.text.toString())
        }
    }

    //회원가입 함수
    fun createAccount(email: String, password: String) {

        val userId = newId.text.toString()
        val userEmail = newEmail.text.toString()
        val userName = newName.text.toString()
        val userTel = newTel.text.toString()
        //uid = auth.uid.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입이 완료되었어요.\n환영합니다!", Toast.LENGTH_SHORT).show()
                        uid = task.getResult().getUser()!!.getUid()
                        writeNewUser(userId, userEmail, userName, userTel)
                        //startActivity(Intent(this, activity_login::class.java))
                        finish() // 가입창 종료
                    } else {
                        Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun writeNewUser(userId: String, name:String, email: String, tel: String) {
        val user = User(userId, name, email, tel)
        database.child("users").child(uid).setValue(user)
        com.example.github.data.userId.userid = userId
    }

}


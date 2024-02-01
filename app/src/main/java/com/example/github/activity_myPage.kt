package com.example.github

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.example.github.data.User
import com.google.firebase.appcheck.internal.util.Logger.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class activity_myPage : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var actionBar: ActionBar
    lateinit var btnLogout: Button
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var info_userEmail: TextView
    lateinit var info_userId: TextView
    lateinit var info_userName: TextView
    lateinit var info_userTel: TextView
    private var uid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기
        info_userEmail = findViewById(R.id.info_userEmail)
        info_userId = findViewById(R.id.info_userId)
        info_userName = findViewById(R.id.info_userName)
        info_userTel = findViewById(R.id.info_userTel)

        auth = Firebase.auth
        //var user = FirebaseAuth.getInstance().currentUser?.uid
        //uid = auth.currentUser.toString()
        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        btnLogout = findViewById(R.id.btnLogout)

        database = Firebase.database.reference
        database.child("users").child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<User>()
                info_userEmail.setText(value!!.userEmail)
                info_userName.setText(value!!.userName)
                info_userId.setText(value!!.userId)
                info_userTel.setText(value!!.userTel)
                Log.d(TAG, "Value is: $value")
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        }) /*{
            //info_userEmail = it.value as TextView
            //Log.i("firevase", "Got value ${it.value}")

        }*/
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

    // database 읽기
    /*myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            val value = dataSnapshot.getValue<String>()
            Log.d(TAG, "Value is: $value")
        }
        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    })*/
}
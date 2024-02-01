package com.example.github

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.example.github.data.User
import com.google.firebase.storage.FirebaseStorage

//날씨 클릭 시 이동하게 되는 화면
class activity_weatherPage : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var actionBar: ActionBar
    lateinit var edtNickname: EditText
    lateinit var btnEnter: Button

    lateinit var firebaseStorage: FirebaseStorage
    private var isFirst :Boolean = true

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_page)

        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기

        edtNickname = findViewById(R.id.edtNickname)
        btnEnter = findViewById(R.id.btnEnter)
        firebaseStorage = FirebaseStorage.getInstance()

        //폰에 저장되어 있는 프로필 읽어오기
        loadData()
        if(User.nickName != null) {
            edtNickname.setText(User.nickName)
            isFirst = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun clickBtn(view: View) {
        if(!isFirst) {
            val intent = Intent(this, activity_realtimeTalk::class.java)
            startActivity(intent)
            finish()
        } else {
            saveData()
        }
    }

    private fun saveData() {
        User.nickName = edtNickname.text.toString()

        val firebaseStorage: Firebase.
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
    }*/
}
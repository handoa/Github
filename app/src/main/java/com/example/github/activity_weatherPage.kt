package com.example.github

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar

//날씨 클릭 시 이동하게 되는 화면
class activity_weatherPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_closet)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        val actionbar: ActionBar = supportActionBar!!
        actionbar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            androidx.appcompat.R.id.home -> {
                //뒤로가기 눌렀을 때
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
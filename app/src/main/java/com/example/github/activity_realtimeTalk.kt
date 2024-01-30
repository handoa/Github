package com.example.github

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.firebase.appcheck.internal.util.Logger.TAG
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class activity_realtimeTalk : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var actionBar: ActionBar

    private lateinit var recyclerView: RecyclerView


    lateinit var btnSend: Button
    lateinit var edt: EditText
    lateinit var stEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime_talk)

        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기

        //Firebase DB
        // database 쓰기
        val database = Firebase.database
        val myRef = database.getReference("message")

        myRef.setValue("Hello, Wolrd!")

        // database 읽기
        myRef.addValueEventListener(object : ValueEventListener {
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
        })
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
}
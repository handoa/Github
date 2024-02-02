package com.example.github

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class activity_ootd : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var actionBar: ActionBar
    private lateinit var gridLayout: GridLayout
    private lateinit var storageReference: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ootd)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기

        gridLayout = findViewById(R.id.gridLayout)
        storageReference = FirebaseStorage.getInstance().reference
        //storageReference = FirebaseStorage.getInstance().getReference()

        // Firebase Storage 이미지 경로
        val imagePaths = listOf(
            "Rectangle 27.png",
            "Rectangle 27 (1).png",
            "Rectangle 27 (2).png",
            "Rectangle 27 (3).png",
            "Rectangle 27 (4).png",
            "Rectangle 27 (5).png"
        )

        for (i in 0 until gridLayout.rowCount) {
            for (j in 0 until gridLayout.columnCount) {
                val index = i * gridLayout.columnCount + j
                if (index < imagePaths.size) {
                    loadImage(imagePaths[index], i, j)

                }
            }
        }
    }

    private fun loadImage(imagePath: String, row: Int, column: Int) {
        val imageView = ImageView(this)
        val layoutParams = GridLayout.LayoutParams()
        layoutParams.rowSpec = GridLayout.spec(row)
        layoutParams.columnSpec = GridLayout.spec(column)
        imageView.layoutParams = layoutParams
        gridLayout.addView(imageView)

        // Firebase Storage에서 이미지를 다운로드 후  ImageView로 설정
        storageReference.child(imagePath).downloadUrl.addOnSuccessListener { uri ->

            Glide.with(this)
                .load(uri)
                .into(imageView)
        }.addOnFailureListener {
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
}
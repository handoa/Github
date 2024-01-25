package com.example.github.network
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.github.R

class DisplayImageActivity: AppCompatActivity() {
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        imageView = findViewById(R.id.displayImageView)

        // 전달받은 이미지 URL을 가져옴
        val imageUrl = intent.getStringExtra("image_url")

        // Glide 등을 사용하여 이미지 로딩
        imageUrl?.let {
            Glide.with(this)
                .load(it)
                .into(imageView)
        }
    }
}
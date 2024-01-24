package com.example.github

import com.bumptech.glide.Glide
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_PERMISSION_CODE = 123
        private const val PICK_IMAGE_REQUEST_CODE = 456
        private const val IMG_MIME_TYPE = "image/*"
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PERMISSION = 2
        private const val TEMP_IMG_REMOVE_INTERVAL = 24 * 60 * 60 * 1000L // 24 hours
    }

    private lateinit var storageRef: StorageReference
    private var mCameraPhotoPath: Uri? = null
    private lateinit var imageView: ImageView  // 이미지를 표시할 ImageView 추가


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        storageRef = FirebaseStorage.getInstance().reference.child("images")
        imageView = findViewById(R.id.imageView)  // ImageView 초기화

        //val btnChooseImage: Button = findViewById(R.id.btnChooseImage)

        fun checkGalleryPermission(): Boolean {
            return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        // 권한을 요청하는 함수
        fun requestGalleryPermission() {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        }

        // 권한 확인 및 갤러리 열기
        if (checkPermissions()) {
            findViewById<Button>(R.id.btnChooseImage).setOnClickListener {
                checkGalleryPermission()
                openGallery()
            }
        } else {
            // 권한이 없는 경우 권한 요청
            requestGalleryPermission()
            /*ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )*/
        }
    }


    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }


    inner class FirebaseStorageHelper {
        private val storage = FirebaseStorage.getInstance()
        private val storageRef = storage.reference

        fun uploadImageToFirebase(selectedImageUri: Uri): Uri? {
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

            //val uploadTask = imageRef.putFile(selectedImageUri)

            return try {
                val uploadTask = imageRef.putFile(selectedImageUri)
                Tasks.await(uploadTask)
                imageRef.downloadUrl.result
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


    }


    private val firebaseStorageHelper = FirebaseStorageHelper()

    // 이미지 선택 후 업로드를 수행할 부분

    private fun uploadSelectedImageToFirebase(selectedImageUri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Firebase에 이미지 업로드를 수행하고 업로드된 이미지의 URL을 받아옴
                val imageUrl = firebaseStorageHelper.uploadImageToFirebase(selectedImageUri)

                // 업로드된 이미지의 URL을 메인 스레드로 전달하여 UI 업데이트
                withContext(Dispatchers.Main) {
                    imageUrl?.let { url ->
                        // Glide를 사용하여 업로드된 이미지의 URL을 이미지뷰에 표시
                        Glide.with(this@MainActivity)
                            .load(url)
                            .into(imageView)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 예외 처리: 업로드 실패 등의 상황에 대한 처리를 여기에 추가가능
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                // 이미지를 업로드하는 함수 호출
                uploadSelectedImageToFirebase(it)
            }
        }
    }
}









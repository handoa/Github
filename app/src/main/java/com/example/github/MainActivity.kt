package com.example.github

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

class MainActivity : AppCompatActivity() {

    private lateinit var storageRef: StorageReference
    private var mCameraPhotoPath: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        storageRef = FirebaseStorage.getInstance().reference.child("images")

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
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
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

        suspend fun uploadImageToFirebase(selectedImageUri: Uri): Uri? {
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

            val uploadTask = imageRef.putFile(selectedImageUri)

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
            // Firebase에 이미지 업로드를 수행
            firebaseStorageHelper.uploadImageToFirebase(selectedImageUri)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            GlobalScope.launch(Dispatchers.IO) {
                selectedImageUri?.let {

                    firebaseStorageHelper.uploadImageToFirebase(it)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 123
        private const val PICK_IMAGE_REQUEST_CODE = 456
        private const val IMG_MIME_TYPE = "image/*"
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PERMISSION = 2
        private const val TEMP_IMG_REMOVE_INTERVAL = 24 * 60 * 60 * 1000L // 24 hours
        }

    /* private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    }*/


    }










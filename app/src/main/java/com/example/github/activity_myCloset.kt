package com.example.github
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
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

class activity_myCloset : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_closet)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        val actionbar: ActionBar = supportActionBar!!
        actionbar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기
        storageRef = FirebaseStorage.getInstance().reference.child("images")
        imageView = findViewById(R.id.imageView)
        fun checkGalleryPermission(): Boolean {
            return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestGalleryPermission() {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        }

        if (checkPermissions()) {
            findViewById<Button>(R.id.btnChooseImage).setOnClickListener {
                checkGalleryPermission()
                openGallery()
            }
        } else {
            requestGalleryPermission()
        }



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

    companion object {
        private const val REQUEST_PERMISSION_CODE = 123
        private const val PICK_IMAGE_REQUEST_CODE = 456
    }

    private lateinit var storageRef: StorageReference
    private lateinit var imageView: ImageView


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

            return try {
                val uploadTask = imageRef.putFile(selectedImageUri)
                Tasks.await(uploadTask)
                imageRef.downloadUrl.result
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        suspend fun getImageUrlForDate(date: String): Uri? {
            return try {
                val imageRef = storageRef.child("images/image_$date.jpg")
                val url = Tasks.await(imageRef.downloadUrl)
                url
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private val firebaseStorageHelper = FirebaseStorageHelper()

    private fun uploadSelectedImageToFirebase(selectedImageUri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            firebaseStorageHelper.uploadImageToFirebase(selectedImageUri)
        }
    }

    private fun getOneYearAgoDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private val lastYearDate = getOneYearAgoDate()


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            GlobalScope.launch(Dispatchers.IO) {
                selectedImageUri?.let {
                    uploadSelectedImageToFirebase(it)
                }
            }
        }
    }
}










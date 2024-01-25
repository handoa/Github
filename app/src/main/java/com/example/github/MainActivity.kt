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
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.github.network.DisplayImageActivity
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
    }

    private lateinit var storageRef: StorageReference
    private lateinit var imageView: ImageView
    lateinit var weatherView: WebView
    //lateinit var realtimeTalk : RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var myCloset: ImageView
    lateinit var ootd: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //메뉴 리소스 XML의 내용을 앱바(App Bar)에 반영
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        //return super.onCreateOptionsMenu(menu)
        return true
    }

    //앱바(App Bar)에 표시된 액션 또는 오버플로우 메뉴가 선택되면
    //액티비티의 onOptionsItemSelected() 메소드가 호출



    private fun uploadSelectedImageToFirebase(selectedImageUri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            val imageUrl = firebaseStorageHelper.uploadImageToFirebase(selectedImageUri)
            withContext(Dispatchers.Main) {

                imageUrl?.let { url ->
                    // Glide를 사용하여 이미지를 로딩하고 imageView에 표시
                    Glide.with(this@MainActivity)
                        .load(url)
                        .into(imageView)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.toolbar_myPage -> {
                //마이페이지 아이콘 눌렀을 때
                Toast.makeText(applicationContext, "마이페이지 이동", Toast.LENGTH_LONG).show()
                //var intentToMyPage = Intent(this, activity_myPage::class.java)
                //startActivity(intentToMyPage)
                return super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getOneYearAgoDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private val lastYearDate = getOneYearAgoDate()
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

    private fun loadImageForLastYear() {
        GlobalScope.launch(Dispatchers.IO) {
            val imageUrl = firebaseStorageHelper.getImageUrlForDate(lastYearDate)
            withContext(Dispatchers.Main) {
                imageUrl?.let { url ->
                    Glide.with(this@MainActivity)
                        .load(url)
                        .into(imageView)
                }
            }
        }
    }




    }


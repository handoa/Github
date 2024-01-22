package com.example.github

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.util.UUID
class FirebaseStorageHelper {
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
package com.example.visit_jeju_app.community.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.visit_jeju_app.MyApplication.Companion.db
import com.example.visit_jeju_app.MyApplication.Companion.storage
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.community.dateToString
import com.example.visit_jeju_app.databinding.ActivityCommWriteBinding
import java.io.File
import java.util.Date

class CommWriteActivity : AppCompatActivity() {
    lateinit var binding : ActivityCommWriteBinding
    lateinit var filePath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postbtn.setOnClickListener {
            saveStore()
            val intent = intent //인텐트
            startActivity(intent) //액티비티 열기
            overridePendingTransition(0, 0) //인텐트 효과 없애기
            finish()
        }

        binding.upload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)

        }

    }

    private val requestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode === android.app.Activity.RESULT_OK) {
            Glide
                .with(applicationContext)
                .load(it.data?.data)
                .apply(RequestOptions().override(250, 200))
                .centerCrop()
                .into(binding.imageView)
            val cursor = contentResolver.query(
                it.data?.data as Uri,
                arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null
            )
            cursor?.moveToFirst().let {
                filePath = cursor?.getString(0) as String
            }
        }
    }

    private fun saveStore() {
        val data = mapOf(
            "title" to binding.title.text.toString(),
            "content" to binding.addEditView.text.toString(),
            "date" to dateToString(Date())
        )
        db.collection("Communities")
            .add(data)
            .addOnSuccessListener {
                uploadImage(it.id)
            }
            .addOnFailureListener {
                Toast.makeText(this, "오류가 발생했습니다!!", Toast.LENGTH_SHORT).show()
            }
        finish()
    }

    private fun uploadImage(docId: String) {
        val storage = storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("images/${docId}.jpg")

        val file = Uri.fromFile(File(filePath))
        imgRef.putFile(file)
            .addOnSuccessListener {
                Toast.makeText(this, "업로드 성공했습니다", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "업로드 실패했습니다", Toast.LENGTH_SHORT).show()

            }
    }

}
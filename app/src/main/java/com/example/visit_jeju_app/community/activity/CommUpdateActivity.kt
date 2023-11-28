package com.example.visit_jeju_app.community.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.community.dateToString
import com.example.visit_jeju_app.databinding.ActivityCommUpdateBinding
import java.util.Date
import java.util.UUID

class CommUpdateActivity : AppCompatActivity() {

    lateinit var binding : ActivityCommUpdateBinding

    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val docId = intent.getStringExtra("DocId")
        val title = intent.getStringExtra("CommunityTitle")
        val content = intent.getStringExtra("CommunityContent")
        val date = intent.getStringExtra("CommunityDate")

        // 수정 뷰에서 사진 변경할 시, 파이어베이스에 기존 사진에서 변경 반영하는 관련 코드
        val imageUrl = intent.getStringExtra("CommunityImageUrl") // 추가된 부분

        binding.CommunityDate.text = date
        binding.regTitle.setText(title)
        binding.regContent.setText(content)


        // "사진 변경" 버튼 클릭 시 갤러리에서 사진을 선택할 수 있도록 하는 코드

        binding.regImageUpdateBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)

        }


        // 내용 수정한 부분을 파이어베이스에 반영하는 코드
        // 수정
        binding.CommunityModify.setOnClickListener {
            val updatedTitle = binding.regTitle.text.toString()
            val updatedContent = binding.regContent.text.toString()


            // 제목 또는 내용이 수정되었는지 확인
            if (updatedTitle != title || updatedContent != content) {
                val data = mapOf(
                    "title" to updatedTitle,
                    "content" to updatedContent,
                    "date" to dateToString(Date())
                )
                if (docId != null) {
                    MyApplication.db.collection("Communities").document(docId).update(data)
                }
            }

            finish()
        }

        // 수정 취소
        binding.CommunityCancel.setOnClickListener {
            finish()
        }


        // 파이어베이스에 등록된 디테일 뷰와 동일한 사진 가져오는 관련 코드
        // 등록한 이미지 가져 오기
        val imgRef = MyApplication.storage.reference.child("images/${docId}.jpg")
        imgRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(binding.regImageUpdateView)
            }
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
                .into(binding.regImageUpdateView)
            val cursor = contentResolver.query(
                it.data?.data as Uri,
                arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null
            )
            cursor?.moveToFirst().let {
                filePath = cursor?.getString(0) as String
            }
        }
    }



    }


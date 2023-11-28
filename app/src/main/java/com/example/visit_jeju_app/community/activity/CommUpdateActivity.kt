package com.example.visit_jeju_app.community.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.community.dateToString
import com.example.visit_jeju_app.databinding.ActivityCommUpdateBinding
import java.util.Date
import java.util.UUID

class CommUpdateActivity : AppCompatActivity() {

    lateinit var binding : ActivityCommUpdateBinding

    // 수정 뷰에서 사진 변경할 시, 파이어베이스에 기존 사진에서 변경 반영하는 관련 코드
    private var selectedImageUri: Uri? = null
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

        // 수정 뷰에서 사진 변경할 시, 파이어베이스에 기존 사진에서 변경 반영하는 관련 코드
        // 이미지 로딩 코드 (Glide 라이브러리 사용)
        imageUrl?.let {
            Glide.with(this).load(it).into(binding.regImageUpdateView)
        }

        // "사진 변경" 버튼 클릭 시 갤러리에서 사진을 선택할 수 있도록 하는 코드
        binding.regImageUpdateBtn.setOnClickListener {
            openGalleryForImage()
        }

        // 내용 수정한 부분을 파이어베이스에 반영하는 코드
        // 수정
        binding.CommunityModify.setOnClickListener {
            val updatedTitle = binding.regTitle.text.toString()
            val updatedContent = binding.regContent.text.toString()


            // "사진 변경" 버튼 클릭하여 사진 변경 후 "수정" 버튼 클릭하면 파이어베이스에 변경된 사진으로 반영하는 코드
            // 이미지가 선택되었는지 확인
            if (selectedImageUri != null) {
                // 이미지 업로드를 위한 고유한 파일 이름 생성
                val fileName = UUID.randomUUID().toString()
                val imageRef = MyApplication.storage.reference.child("images/$fileName.jpg")

                // 선택한 이미지를 Firebase Storage에 업로드
                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        // 이미지 업로드 성공 시 해당 URL을 데이터에 저장
                        val imageUrl = it.metadata?.reference?.downloadUrl.toString()

                        // 데이터 업데이트
                        updateData(docId, updatedTitle, updatedContent, imageUrl)

                        // 액티비티 종료
                        finish()
                    }
                    .addOnFailureListener {
                        // 이미지 업로드 실패
                        // 실패 처리 로직 추가
                    }
                // "사진 변경" 버튼 클릭하여 사진 변경 후 "수정" 버튼 클릭하면 파이어베이스에 변경된 사진으로 반영하는 코드
            } else {
                // 이미지가 선택되지 않은 경우에는 데이터 업데이트만 진행
                updateData(docId, updatedTitle, updatedContent, imageUrl)

                // 사진 변경한 내용을 디테일 뷰와 수정 뷰 둘다 바로 반영하는 코드
                setResultAndFinish(updatedTitle, updatedContent, imageUrl)
            }
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

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = result.data?.data
                Glide.with(this)
                    .load(selectedImageUri)
                    .into(binding.regImageUpdateView)
                this.selectedImageUri = selectedImageUri
            }
        }

    private fun updateData(docId: String?, updatedTitle: String, updatedContent: String, imageUrl: String?) {
        val data = mapOf(
            "title" to updatedTitle,
            "content" to updatedContent,
            "date" to dateToString(Date()),
            "imageUrl" to imageUrl
        )

        if (docId != null) {
            MyApplication.db.collection("Communities").document(docId).update(data)
        }
    }


    // 사진 변경한 내용을 디테일 뷰와 수정 뷰 둘다 바로 반영하는 코드
    private fun setResultAndFinish(updatedTitle: String, updatedContent: String, updatedImageUrl: String?) {
        val resultIntent = Intent()
        resultIntent.putExtra("UpdatedTitle", updatedTitle)
        resultIntent.putExtra("UpdatedContent", updatedContent)
        resultIntent.putExtra("UpdatedImageUrl", updatedImageUrl)

        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}
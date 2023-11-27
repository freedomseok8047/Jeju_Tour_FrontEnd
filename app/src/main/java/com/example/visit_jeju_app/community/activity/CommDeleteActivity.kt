package com.example.visit_jeju_app.community.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.databinding.ActivityCommDeleteBinding

class CommDeleteActivity : AppCompatActivity() {

    lateinit var binding : ActivityCommDeleteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val DocId = intent.getStringExtra("DocId")

        if (DocId != null) {
            deleteDatabase(DocId)


            Toast.makeText(this, "게시글 삭제 완료", Toast.LENGTH_SHORT).show()

        } else {
            Log.e("CommDeleteActivity", "Error")
            Toast.makeText(this, "게시글 삭제 실패", Toast.LENGTH_SHORT).show()
        }
    }

}
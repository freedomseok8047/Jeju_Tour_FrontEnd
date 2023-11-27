package com.example.visit_jeju_app.community

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*


fun myCheckPermission(activity: AppCompatActivity) {

    val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(activity, "권한 승인", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "권한 거부", Toast.LENGTH_SHORT).show()
        }
    }

    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_MEDIA_IMAGES
        ) !== PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
    }
}

fun dateToString(date: Date): String {
    val format = SimpleDateFormat("yyyy-MM-dd")
    return format.format(date)
}
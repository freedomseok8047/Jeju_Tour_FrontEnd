package com.example.visit_jeju_app

import android.annotation.SuppressLint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationServices

class LocationTestActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_test)

        handler = Handler(Looper.getMainLooper())

        getLocation()
        val pref = getSharedPreferences("LatLnt", MODE_PRIVATE)
        val gettedLat = pref.getString("lat", "default lat")?.toDouble()
        val gettedLnt = pref.getString("lnt", "default lnt")?.toDouble()
        Log.d("lsy", "pref 현재 위치 조회 : lat : ${gettedLat}," +
                " lnt : ${pref.getString("lnt", "${gettedLnt}")}")

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    Log.d("lsy", "get Location 현재 위치 조회 : lat : ${location.latitude}, lnt : ${location.longitude}")

                    val lat = location.latitude.toString()
                    val lnt = location.longitude.toString()

                    val pref = getSharedPreferences("LatLnt", MODE_PRIVATE)
                    val editor = pref.edit()
                    editor.putString("lat", lat)
                    editor.putString("lnt", lnt)
                    editor.commit()


                    val gettedLat = pref.getString("lat", "default lat")?.toDouble()
                    val gettedLnt = pref.getString("lnt", "default lnt")?.toDouble()

                    Log.d("lsy", "pref 현재 위치 조회 : lat : ${gettedLat}," +
                            " lnt : ${pref.getString("lnt", "${gettedLnt}")}")
                }
            }
            .addOnFailureListener { fail ->
                Log.d("lsy", "현재 위치 조회 실패")
            }
    }
}
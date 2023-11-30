package com.example.visit_jeju_app.festival

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.databinding.ActivityFesRegionNmBinding
import com.example.visit_jeju_app.festival.adapter.RegionNmAdapter
import com.example.visit_jeju_app.festival.model.FesList
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import retrofit2.Response

class FesRegionNmActivity : AppCompatActivity() {
    lateinit var binding: ActivityFesRegionNmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFesRegionNmBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.setOnClickListener {
        }
        // 슬라이딩 패널 ==============================================================================
        val slidePanel = binding.mainFrame                      // SlidingUpPanel

        // 패널 열고 닫기
        binding.btnToggle.setOnClickListener {
            val state = slidePanel.panelState
            // 닫힌 상태일 경우 열기
            if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
            }
            // 열린 상태일 경우 닫기
            else if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }

        // 지역 코드로 관광지 불러오기 ==================================================================

        // 디폴트 : 제주시내
        binding.regionNm1.setOnClickListener {
            getFesNmList(11)
            binding.whereBtn.text = "제주시내"
        }

        binding.regionNm2.setOnClickListener {
            getFesNmList(21)
            binding.whereBtn.text = "서귀포시내"
        }
        binding.regionNm3.setOnClickListener {
            getFesNmList(12)
            binding.whereBtn.text = "애월"
        }
        binding.regionNm4.setOnClickListener {
            getFesNmList(17)
            binding.whereBtn.text = "성산"
        }
        binding.regionNm5.setOnClickListener {
            getFesNmList(31)
            binding.whereBtn.text = "우도"
        }
        binding.regionNm6.setOnClickListener {
            getFesNmList(24)
            binding.whereBtn.text = "중문"
        }
        binding.regionNm7.setOnClickListener {
            getFesNmList(25)
            binding.whereBtn.text = "남원"
        }
        binding.regionNm8.setOnClickListener {
            getFesNmList(13)
            binding.whereBtn.text = "한림"
        }
        binding.regionNm9.setOnClickListener {
            getFesNmList(14)
            binding.whereBtn.text = "한경"
        }
        binding.regionNm10.setOnClickListener {
            getFesNmList(15)
            binding.whereBtn.text = "조천"
        }
        binding.regionNm11.setOnClickListener {
            getFesNmList(16)
            binding.whereBtn.text = "구좌"
        }
        binding.regionNm12.setOnClickListener {
            getFesNmList(22)
            binding.whereBtn.text = "대정"
        }

        binding.pageChange.setOnClickListener {
            val intent = Intent(this@FesRegionNmActivity,FesActivity::class.java)
            startActivity(intent)
        }

        // ==========================================================================================
    }//onCreate 문 닫음

    private fun getFesNmList(itemsRegion2CdValue: Int) {

        var jejuRegionCode: Int = itemsRegion2CdValue
        val networkService = (applicationContext as MyApplication).networkService
        val userListCall =
            networkService.getFesList(jejuRegionCode)

        userListCall.enqueue(object : retrofit2.Callback<List<FesList>> {
            override fun onResponse(
                call: retrofit2.Call<List<FesList>>,
                response: Response<List<FesList>>
            ) {
                val FesList = response.body()

                binding.recyclerView.adapter =
                    RegionNmAdapter(this@FesRegionNmActivity, FesList)

                binding.recyclerView.addItemDecoration(
                    DividerItemDecoration(this@FesRegionNmActivity, LinearLayoutManager.VERTICAL)
                )

            }

            override fun onFailure(call: retrofit2.Call<List<FesList>>, t: Throwable) {
                call.cancel()
            }
        })
    }
}
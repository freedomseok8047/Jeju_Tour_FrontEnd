package com.example.visit_jeju_app.restaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.databinding.ActivityResRegionNmBinding
import com.example.visit_jeju_app.restaurant.adapter.RegionNmAdapter
import com.example.visit_jeju_app.restaurant.model.ResList
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import retrofit2.Response

class ResRegionNmActivity : AppCompatActivity() {
    lateinit var binding: ActivityResRegionNmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResRegionNmBinding.inflate(layoutInflater)
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
            getResNmList(11)
            binding.whereBtn.text = "제주시내"
        }

        binding.regionNm2.setOnClickListener {
            getResNmList(21)
            binding.whereBtn.text = "서귀포시내"
        }
        binding.regionNm3.setOnClickListener {
            getResNmList(12)
            binding.whereBtn.text = "애월"
        }
        binding.regionNm4.setOnClickListener {
            getResNmList(17)
            binding.whereBtn.text = "성산"
        }
        binding.regionNm5.setOnClickListener {
            getResNmList(31)
            binding.whereBtn.text = "우도"
        }
        binding.regionNm6.setOnClickListener {
            getResNmList(24)
            binding.whereBtn.text = "중문"
        }
        binding.regionNm7.setOnClickListener {
            getResNmList(25)
            binding.whereBtn.text = "남원"
        }
        binding.regionNm8.setOnClickListener {
            getResNmList(13)
            binding.whereBtn.text = "한림"
        }
        binding.regionNm9.setOnClickListener {
            getResNmList(14)
            binding.whereBtn.text = "한경"
        }
        binding.regionNm10.setOnClickListener {
            getResNmList(15)
            binding.whereBtn.text = "조천"
        }
        binding.regionNm11.setOnClickListener {
            getResNmList(16)
            binding.whereBtn.text = "구좌"
        }
        binding.regionNm12.setOnClickListener {
            getResNmList(22)
            binding.whereBtn.text = "대정"
        }

        binding.pageChange.setOnClickListener {
            val intent = Intent(this@ResRegionNmActivity,ResActivity::class.java)
            startActivity(intent)
        }

        // ==========================================================================================
    }//onCreate 문 닫음

    private fun getResNmList(itemsRegion2CdValue: Int) {

        var jejuRegionCode: Int = itemsRegion2CdValue
        val networkService = (applicationContext as MyApplication).networkService
        val userListCall =
            networkService.getResList(jejuRegionCode)

        userListCall.enqueue(object : retrofit2.Callback<List<ResList>> {
            override fun onResponse(
                call: retrofit2.Call<List<ResList>>,
                response: Response<List<ResList>>
            ) {
                val ResList = response.body()

                binding.recyclerView.adapter =
                    RegionNmAdapter(this@ResRegionNmActivity, ResList)

                binding.recyclerView.addItemDecoration(
                    DividerItemDecoration(this@ResRegionNmActivity, LinearLayoutManager.VERTICAL)
                )

            }

            override fun onFailure(call: retrofit2.Call<List<ResList>>, t: Throwable) {
                call.cancel()
            }
        })
    }
}
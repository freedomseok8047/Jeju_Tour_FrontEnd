package com.example.visit_jeju_app.accommodation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.accommodation.adapter.RegionNmAdapter
import com.example.visit_jeju_app.accommodation.model.AccomList
import com.example.visit_jeju_app.databinding.ActivityAccomRegionNmBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import retrofit2.Response

class AccomRegionNmActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccomRegionNmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAccomRegionNmBinding.inflate(layoutInflater)
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
            getAccomNmList(11)
            binding.whereBtn.text = "제주시내"
        }

        binding.regionNm2.setOnClickListener {
            getAccomNmList(21)
            binding.whereBtn.text = "서귀포시내"
        }
        binding.regionNm3.setOnClickListener {
            getAccomNmList(12)
            binding.whereBtn.text = "애월"
        }
        binding.regionNm4.setOnClickListener {
            getAccomNmList(17)
            binding.whereBtn.text = "성산"
        }
        binding.regionNm5.setOnClickListener {
            getAccomNmList(31)
            binding.whereBtn.text = "우도"
        }
        binding.regionNm6.setOnClickListener {
            getAccomNmList(24)
            binding.whereBtn.text = "중문"
        }
        binding.regionNm7.setOnClickListener {
            getAccomNmList(25)
            binding.whereBtn.text = "남원"
        }
        binding.regionNm8.setOnClickListener {
            getAccomNmList(13)
            binding.whereBtn.text = "한림"
        }
        binding.regionNm9.setOnClickListener {
            getAccomNmList(14)
            binding.whereBtn.text = "한경"
        }
        binding.regionNm10.setOnClickListener {
            getAccomNmList(15)
            binding.whereBtn.text = "조천"
        }
        binding.regionNm11.setOnClickListener {
            getAccomNmList(16)
            binding.whereBtn.text = "구좌"
        }
        binding.regionNm12.setOnClickListener {
            getAccomNmList(22)
            binding.whereBtn.text = "대정"
        }

        binding.pageChange.setOnClickListener {
            val intent = Intent(this@AccomRegionNmActivity,AccomActivity::class.java)
            startActivity(intent)
        }

        // ==========================================================================================
    }//onCreate 문 닫음

    private fun getAccomNmList(itemsRegion2CdValue: Int) {

        var jejuRegionCode: Int = itemsRegion2CdValue
        val networkService = (applicationContext as MyApplication).networkService
        val userListCall =
            networkService.getAccomList(jejuRegionCode)

        userListCall.enqueue(object : retrofit2.Callback<List<AccomList>> {
            override fun onResponse(
                call: retrofit2.Call<List<AccomList>>,
                accomponse: Response<List<AccomList>>
            ) {
                val AccomList = accomponse.body()

                binding.recyclerView.adapter =
                    RegionNmAdapter(this@AccomRegionNmActivity, AccomList)

                binding.recyclerView.addItemDecoration(
                    DividerItemDecoration(this@AccomRegionNmActivity, LinearLayoutManager.VERTICAL)
                )

            }

            override fun onFailure(call: retrofit2.Call<List<AccomList>>, t: Throwable) {
                call.cancel()
            }
        })
    }
}
package com.example.visit_jeju_app.shopping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.databinding.ActivityShopRegionNmBinding
import com.example.visit_jeju_app.shopping.adapter.RegionNmAdapter
import com.example.visit_jeju_app.shopping.model.ShopList
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import retrofit2.Response

class ShopRegionNmActivity : AppCompatActivity() {
    lateinit var binding: ActivityShopRegionNmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityShopRegionNmBinding.inflate(layoutInflater)
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
            getShopNmList(11)
            binding.whereBtn.text = "제주시내"
        }

        binding.regionNm2.setOnClickListener {
            getShopNmList(21)
            binding.whereBtn.text = "서귀포시내"
        }
        binding.regionNm3.setOnClickListener {
            getShopNmList(12)
            binding.whereBtn.text = "애월"
        }
        binding.regionNm4.setOnClickListener {
            getShopNmList(17)
            binding.whereBtn.text = "성산"
        }
        binding.regionNm5.setOnClickListener {
            getShopNmList(31)
            binding.whereBtn.text = "우도"
        }
        binding.regionNm6.setOnClickListener {
            getShopNmList(24)
            binding.whereBtn.text = "중문"
        }
        binding.regionNm7.setOnClickListener {
            getShopNmList(25)
            binding.whereBtn.text = "남원"
        }
        binding.regionNm8.setOnClickListener {
            getShopNmList(13)
            binding.whereBtn.text = "한림"
        }
        binding.regionNm9.setOnClickListener {
            getShopNmList(14)
            binding.whereBtn.text = "한경"
        }
        binding.regionNm10.setOnClickListener {
            getShopNmList(15)
            binding.whereBtn.text = "조천"
        }
        binding.regionNm11.setOnClickListener {
            getShopNmList(16)
            binding.whereBtn.text = "구좌"
        }
        binding.regionNm12.setOnClickListener {
            getShopNmList(22)
            binding.whereBtn.text = "대정"
        }

        binding.pageChange.setOnClickListener {
            val intent = Intent(this@ShopRegionNmActivity,ShopActivity::class.java)
            startActivity(intent)
        }

        // ==========================================================================================
    }//onCreate 문 닫음

    private fun getShopNmList(itemsRegion2CdValue: Int) {

        var jejuRegionCode: Int = itemsRegion2CdValue
        val networkService = (applicationContext as MyApplication).networkService
        val userListCall =
            networkService.getShopList(jejuRegionCode)

        userListCall.enqueue(object : retrofit2.Callback<List<ShopList>> {
            override fun onResponse(
                call: retrofit2.Call<List<ShopList>>,
                response: Response<List<ShopList>>
            ) {
                val ShopList = response.body()

                binding.recyclerView.adapter =
                    RegionNmAdapter(this@ShopRegionNmActivity, ShopList)

                binding.recyclerView.addItemDecoration(
                    DividerItemDecoration(this@ShopRegionNmActivity, LinearLayoutManager.VERTICAL)
                )

            }

            override fun onFailure(call: retrofit2.Call<List<ShopList>>, t: Throwable) {
                call.cancel()
            }
        })
    }
}
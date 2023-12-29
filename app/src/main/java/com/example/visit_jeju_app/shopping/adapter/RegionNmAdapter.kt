package com.example.visit_jeju_app.shopping.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.visit_jeju_app.databinding.ItemRegionnmBinding
import com.example.visit_jeju_app.shopping.ShopRegionNmActivity
import com.example.visit_jeju_app.shopping.model.ShopList
import com.example.visit_jeju_app.shopping.regionNmDetailShopActivity


class RegionNmViewHolder(val binding: ItemRegionnmBinding) : RecyclerView.ViewHolder(binding.root)
class RegionNmAdapter(val context: ShopRegionNmActivity, val datas: List<ShopList>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        RegionNmViewHolder(ItemRegionnmBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    // 리사이클러뷰
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as RegionNmViewHolder).binding

        //add......................................
        val model = datas!![position]
        binding.facltNm.text = model.itemsTitle
        val urlImg = model.itemsRepPhotoPhotoidImgPath
        Glide.with(context)
            .asBitmap()
            .load(urlImg)
            .into(object : CustomTarget<Bitmap>(200, 200) {

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    binding.avatarView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        // 클릭시 관광지 상세 정보 페이지
        holder.binding.root.setOnClickListener {
            val intent = Intent(holder.binding.root?.context, regionNmDetailShopActivity::class.java)
            intent.putExtra("shopId", model?.shopId)
            intent.putExtra("itemsTitle", model.itemsTitle)
            intent.putExtra("itemsContentsCdLabel", model.itemsContentsCdLabel)
            intent.putExtra("itemsRepPhotoPhotoidImgPath", model.itemsRepPhotoPhotoidImgPath)
            intent.putExtra("itemsPhoneNo", model.itemsPhoneNo)
            intent.putExtra("itemsRegion2CdLabel", model.itemsRegion2CdLabel)
            intent.putExtra("itemsRegion2CdValue", model.itemsRegion2CdValue)
            intent.putExtra("itemsAddress", model.itemsAddress)
            intent.putExtra("itemsRoadAddress", model.itemsRoadAddress)
            intent.putExtra("itemsLatitude", model.itemsLatitude)
            intent.putExtra("itemsLongitude", model.itemsLongitude)
            intent.putExtra("itemsAllTag", model.itemsAllTag)
            intent.putExtra("itemsIntroduction", model.itemsIntroduction)
            intent.putExtra("itemsContentsCdLabel", model.itemsContentsCdLabel)

            ContextCompat.startActivity(holder.binding.root.context, intent, null)
        }

    }
}
















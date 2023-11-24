package com.example.visit_jeju_app.community.recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.visit_jeju_app.community.activity.CommDetailActivity
import com.example.visit_jeju_app.community.model.CommunityData
import com.example.visit_jeju_app.databinding.CommunityItemBinding


class CommunityViewHolder(val binding: CommunityItemBinding) : RecyclerView.ViewHolder(binding.root) {

}

class CommunityAdapter(val context: Context, val itemList: MutableList<CommunityData>): RecyclerView.Adapter<CommunityViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommunityViewHolder(CommunityItemBinding.inflate(layoutInflater))

    }
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        val data = itemList.get(position)

        holder.binding.run {
            itemTitleView.text=data.title
            itemContentView.text=data.content
            itemDateView.text=data.date
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CommDetailActivity::class.java)
            intent.putExtra("DocId", data.docId)
            intent.putExtra("CommunityTitle", data.title)
            intent.putExtra("CommunityContent", data.content)
            intent.putExtra("CommunityDate", data.date)
            intent.putExtra("Comment", data.comment)
            context.startActivity(intent)
        }
    }
}
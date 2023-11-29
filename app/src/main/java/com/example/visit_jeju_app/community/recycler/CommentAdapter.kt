package com.example.visit_jeju_app.community.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.visit_jeju_app.community.activity.CommDetailActivity
import com.example.visit_jeju_app.databinding.ItemCommentBinding

class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {

}

class CommentAdapter(val context: Context, val itemList: MutableList<CommDetailActivity.comment>): RecyclerView.Adapter<CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommentViewHolder(ItemCommentBinding.inflate(layoutInflater))

    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val binding = (holder as CommentViewHolder).binding
        binding.CommentDate.text = itemList[position].time
        binding.Comment.text = itemList[position].comment

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
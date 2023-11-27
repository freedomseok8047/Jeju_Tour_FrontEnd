package com.example.visit_jeju_app.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.visit_jeju_app.R

class RecyclerViewAdapter(private val dataList: List<String>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    // ViewHolder 클래스: 각 아이템 뷰의 구성 요소를 보유합니다.
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTextView: TextView = itemView.findViewById(R.id.testText)
    }

    // onCreateViewHolder: 뷰홀더를 생성하고 뷰를 연결합니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder: 뷰홀더에 데이터를 바인딩합니다.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTextView.text = dataList[position]
    }

    // getItemCount: RecyclerView에 표시할 아이템 개수를 반환합니다.
    override fun getItemCount(): Int {
        return dataList.size
    }
}
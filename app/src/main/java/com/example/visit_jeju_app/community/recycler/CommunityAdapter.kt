package com.example.visit_jeju_app.community.recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.visit_jeju_app.community.activity.CommDetailActivity
import com.example.visit_jeju_app.community.model.CommunityData
import com.example.visit_jeju_app.databinding.CommunityItemBinding
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat


// RecyclerView를 위한 ViewHolder 클래스를 정의하는 코드
class CommunityViewHolder(val binding: CommunityItemBinding) : RecyclerView.ViewHolder(binding.root) {

}
// crud된 파이어베이스 데이터가 activiy_comm_read.xml 뷰에 자동반영되도록 하는 코드
// RecyclerView의 Adapter 클래스로 데이터를 처리하고 뷰를 생성하는 코드
class CommunityAdapter(val context: Context, private var itemList: MutableList<CommunityData>) :
    RecyclerView.Adapter<CommunityViewHolder>() {

    // 필요할 때 ViewHolder 인스턴스를 생성하는 코드 (즉, 초기화 작업)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        // 각 아이템 뷰를 위한 레이아웃을 인플레이트하는 코드
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommunityViewHolder(CommunityItemBinding.inflate(layoutInflater))

    }
    // 데이터 세트의 아이템 수를 반환하는 코드
    override fun getItemCount(): Int {
        return itemList.size
    }

    // RecyclerView의 각 아이템에 데이터를 바인딩하는 코드
    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        // crud된 파이어베이스 데이터가 activiy_comm_read.xml 뷰에 자동반영되도록 하는 코드
        // 현재 위치의 데이터를 가져오도록 하는 코드
        val data = itemList[position]

        // 데이터 바인딩을 사용하여 뷰에 액세스하는 코드
        holder.binding.run {
            // 아이템에 제목 설정 코드
            itemTitleView.text=data.title
            // 아이템에 내용 설정 코드
            itemContentView.text=data.content

            // 파이어베이스에 저장된 timestamp형의 데이터를 불러와서
            // activity_comm_read.xml에 최신순으로 나타나도록하는 관련코드
            // Timestamp를 문자열로 변환하여 표시
            // timestamp형이 아닌 string이면서 "yyyy-MM-dd HH:mm"포맷으로 파이어베이스 저장 및 조회 관련 코드
            // 지정된 형식으로 날짜를 표시 코드
            itemDateView.text = data.date

            // 카테고리를 파이어베이스에 저장하는 코드
            // 카테고리를 표시하는 코드
            itemCategoryView.text = "${data.category}"

        }

        // RecyclerView의 각 아이템에 대한 클릭 리스너를 설정하는 코드
        holder.itemView.setOnClickListener {
            // 디테일 액티비티를 시작하기 위한 인텐트를 생성하는 코드
            val intent = Intent(context, CommDetailActivity::class.java)
            // 인텐트 엑스트라를 사용하여 디테일 액티비티로 데이터를 전달
            intent.putExtra("DocId", data.docId)
            intent.putExtra("CommunityTitle", data.title)
            intent.putExtra("CommunityContent", data.content)
            intent.putExtra("CommunityDate", data.date)
            intent.putExtra("Comment", data.comment)

            // 카테고리를 파이어베이스에 저장하는 코드
            intent.putExtra("Category", data.category)
            // 디테일 액티비티를 시작하는 코드
            context.startActivity(intent)
        }
    }

    // crud된 파이어베이스 데이터가 activiy_comm_read.xml 뷰에 자동반영되도록 하는 코드
    fun updateData(newItemList: MutableList<CommunityData>) {
        itemList = newItemList
        notifyDataSetChanged()
    }
}
package com.example.visit_jeju_app.community.model

import com.google.firebase.Timestamp

class CommunityData {
    var docId: String? = null
    var title: String? = null
    var content: String? = null
    // 파이어베이스에 저장된 timestamp형의 데이터를 불러와서
    // activity_comm_read.xml에 최신순으로 나타나도록하는 관련코드
    var date: Timestamp? = null
    var comment: String? = null
}

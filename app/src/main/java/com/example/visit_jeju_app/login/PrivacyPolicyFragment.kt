package com.example.visit_jeju_app.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.visit_jeju_app.R

/*class PrivacyPolicyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트에서 보여줄 레이아웃을 연결합니다. 여기에 자세한 내용을 보여주는 레이아웃을 넣으세요.
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false)
    }
}*/

class PrivacyPolicyFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false)
    }
}
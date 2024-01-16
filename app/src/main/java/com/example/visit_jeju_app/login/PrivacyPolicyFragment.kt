package com.example.visit_jeju_app.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.example.visit_jeju_app.R

class PrivacyPolicyFragment : DialogFragment() {
    private var privacyChangeListener: PrivacyPolicyListener? = null

    fun setPrivacyPolicyListener(listener: PrivacyPolicyListener) {
        privacyChangeListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_privacy_policy, container, false)

        val checkBox = view.findViewById<CheckBox>(R.id.checkboxUserAgreement3)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            // CheckBox 상태가 변경될 때 SignUpActivity에 변경된 상태를 알림
            privacyChangeListener?.onPrivacyPolicyChanged(isChecked)
            // 체크박스가 체크되면 프래그먼트를 닫음
            if (isChecked) {
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            }
        }

        // closeBtn 클릭 이벤트 처리
        val closeButton = view.findViewById<ImageButton>(R.id.closeBtn)
        closeButton.setOnClickListener {
            // 현재 프래그먼트를 닫음
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        return view
    }

    interface PrivacyPolicyListener {
        fun onPrivacyPolicyChanged(checked: Boolean)
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}
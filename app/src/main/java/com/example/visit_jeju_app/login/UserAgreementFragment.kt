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



class UserAgreementFragment : DialogFragment() {
    private var useragreementChangeListener: UserAgreementChangeListener? = null

    fun setUserAgreementChangeListener(listener: UserAgreementChangeListener) {
        useragreementChangeListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_agreement, container, false)

        val checkBox = view.findViewById<CheckBox>(R.id.checkboxUserAgreement2)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            // CheckBox 상태가 변경될 때 SignUpActivity에 변경된 상태를 알림
            useragreementChangeListener?.onAgreementChanged(isChecked)
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

    interface UserAgreementChangeListener {
        fun onAgreementChanged(checked: Boolean)
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}

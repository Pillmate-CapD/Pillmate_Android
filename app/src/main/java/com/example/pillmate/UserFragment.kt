package com.example.pillmate

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class UserFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그아웃 버튼 처리
        val logoutTextView: TextView = view.findViewById(R.id.tv_logout)
        logoutTextView.setOnClickListener {
            // 로그아웃 다이얼로그 표시
            showLogoutDialog()
        }
        // 비밀번호 변경 버튼 처리
        val changePwButton: View = view.findViewById(R.id.btn_ch_pw)
        changePwButton.setOnClickListener {
            // 비밀번호 변경 화면으로 이동
            val intent = Intent(activity, PwChangeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLogoutDialog() {
        // LogoutDialogFragment를 사용하여 로그아웃 다이얼로그 표시
        val logoutDialog = LogoutDialogFragment()
        logoutDialog.show(parentFragmentManager, "logoutDialog")
    }

}
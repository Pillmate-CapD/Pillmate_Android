package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LogoutDialogFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.dialog_logout, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 로그아웃 버튼 처리
        val logoutButton: Button = view.findViewById(R.id.btn_logout)
        logoutButton.setOnClickListener {
            logout()
        }

        // 취소 버튼 처리
        val cancelButton: Button = view.findViewById(R.id.btn_cancel)
        cancelButton.setOnClickListener {
            dismiss()  // 다이얼로그 닫기
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 배경을 투명하게 설정
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    private fun logout() {
        // 로그아웃 처리 및 로그인 화면으로 이동
        val auto = requireActivity().getSharedPreferences("autoLogin", AppCompatActivity.MODE_PRIVATE)
        val autoLoginEdit = auto.edit()

        autoLoginEdit.putBoolean("autoLoginUse", false)
        autoLoginEdit.putString("Id", null)
        autoLoginEdit.putString("Pw", null)
        autoLoginEdit.commit()

        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
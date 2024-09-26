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
            // 로그아웃 확인 다이얼로그 표시
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
        // 로그아웃 확인을 위한 AlertDialog 생성
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setMessage("필메이트에서 로그아웃하시겠어요?")
            .setPositiveButton("로그아웃") { dialog, id ->
                // 로그아웃 클릭 시 로그아웃 처리
                logout()
            }
            .setNegativeButton("취소") { dialog, id ->
                // 취소 클릭 시 다이얼로그 닫기
                dialog.dismiss()
            }

        // 다이얼로그 표시
        val dialog = builder.create()
        dialog.show()

        // 버튼 색상 커스터마이징 (선택 사항)
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(android.R.color.holo_red_light))
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(android.R.color.black))
    }

    private fun logout() {
        // Perform the logout logic and redirect to the LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
package com.example.pillmate

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그아웃 버튼 처리
        val logoutTextView: TextView = view.findViewById(R.id.tv_logout)
        logoutTextView.setOnClickListener {
            showLogoutDialog()
        }

        // 비밀번호 변경 버튼 처리
        val changePwButton: View = view.findViewById(R.id.btn_ch_pw)
        changePwButton.setOnClickListener {
            val intent = Intent(activity, PwChangeActivity::class.java)
            startActivity(intent)
        }
    }

    // 로그아웃 다이얼로그 표시
    private fun showLogoutDialog() {
        val logoutDialogView = layoutInflater.inflate(R.layout.dialog_logout, null)

        val logoutDialog = AlertDialog.Builder(requireContext())
            .setView(logoutDialogView)
            .create()

        // 로그아웃 버튼 클릭 시
        logoutDialogView.findViewById<View>(R.id.btn_logout).setOnClickListener {
            logoutDialog.dismiss()
            Log.d("UserFragment", "Logout button clicked")
            //logoutUser()

            val auto = requireActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
            val autoLoginEdit = auto.edit()

            autoLoginEdit.putBoolean("autoLoginUse", false)
            autoLoginEdit.putString("Id", null)
            autoLoginEdit.putString("Pw", null)
            autoLoginEdit.commit()

            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // 취소 버튼 클릭 시
        logoutDialogView.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            logoutDialog.dismiss()  // 다이얼로그 닫기
            Log.d("UserFragment", "Cancel button clicked")
        }

        logoutDialog.show()
    }

    // 로그아웃 API 요청
    private fun logoutUser() {
        lifecycleScope.launch {
            try {
                Log.d("UserFragment", "Sending logout request...")
                val response = RetrofitApi.getRetrofitService.logout()
                Log.d("UserFragment", "Logout response: memberId = ${response.memberId}")

                if (response.memberId > 0) {
                    Log.d("UserFragment", "Logout successful, redirecting to LoginActivity")
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Log.d("UserFragment", "Logout failed, memberId is not valid")
                }
            } catch (e: Exception) {
                Log.e("UserFragment", "Logout request failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

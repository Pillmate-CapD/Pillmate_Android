package com.example.pillmate

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pillmate.databinding.FragmentUserBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding
    private lateinit var retrofitService: RetrofitService
    // 사용자 이름 변수 추가
    private var userName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.fragment_user, container, false)
        binding = FragmentUserBinding.inflate(inflater, container, false)

        // Retrofit 서비스 초기화 (여기 수정함)
        retrofitService = RetrofitApi.getRetrofitService

//        // ProgressBar 값 테스트용으로 설정
//        binding.homePillProgressBar.setProgress(75) // ProgressBar를 75%로 설정
//        binding.homePercent.text = "75점" // 점수 텍스트도 동일하게 설정

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 현재 월 가져오기 및 설정
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // 0부터 시작하므로 +1 필요
        binding.tvOctoberStatus.text = "${currentMonth}월 복용 현황"

        // SharedPreferences에서 사용자 이름 가져오기
        val sharedPreferences = requireActivity().getSharedPreferences("userName", Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("userName", "이름을 불러오는데 실패했습니다")
        binding.tvUser.text = userName  // 사용자 이름을 TextView에 설정

        // 복약 순응도, 복용 현황, 점수 데이터 불러오기
        lifecycleScope.launch {
            try {
                val userStatus = retrofitService.getUserStatus()
                binding.homePillLevel.text = userStatus.grade       // 복약 순응도
                // 복용 일수
                val formattedDay = if (userStatus.takenDay in 0..9) "  ${userStatus.takenDay}일" else "${userStatus.takenDay}일"
                binding.homePillUser.text = formattedDay
                //binding.homePillUser.text = "${userStatus.takenDay}일"  // 복용 일수
                binding.homePillDay.text = "/${userStatus.month}일"      // 총 일수
                binding.homePercent.text = "${userStatus.rate}점"        // 점수

                // ProgressBar의 점수를 직접 설정
                binding.homePillProgressBar.setProgress(userStatus.rate)

            } catch (e: Exception) {
                Log.e("UserFragment", "Failed to fetch user status: ${e.message}")
            }
        }
        //도움말
        val help: View = view.findViewById(R.id.btn_help)
        help.setOnClickListener {
            val intent = Intent(activity, Help1Activity::class.java)
            startActivity(intent)
        }

        // 로그아웃 버튼 처리
        val logoutTextView: TextView = view.findViewById(R.id.tv_logout)
        logoutTextView.setOnClickListener {
            showLogoutDialog()
        }

        // 비밀번호 변경 버튼 처리
        val changePwButton: View = view.findViewById(R.id.btn_ch_pw)
        changePwButton.setOnClickListener {
            val intent = Intent(activity, PwChange1Activity::class.java)
            startActivity(intent)
        }
        // 비밀번호 변경 버튼 처리
        val qna: View = view.findViewById(R.id.btn_question)
        qna.setOnClickListener {
            val intent = Intent(activity, QnaActivity::class.java)
            startActivity(intent)
        }

        val myhealth: View = view.findViewById(R.id.btn_my_health)
        myhealth.setOnClickListener {
            val intent = Intent(activity, MyHealthInfoActivity::class.java)
            startActivity(intent)
        }


        val heathLayout: ConstraintLayout = view.findViewById(R.id.my_health_layout)
        heathLayout.setOnClickListener {
            val intent = Intent(activity, MyHealthInfoActivity::class.java)
            startActivity(intent)
        }

        val questionLayout: ConstraintLayout = view.findViewById(R.id.question_layout)
        questionLayout.setOnClickListener {
            val intent = Intent(activity, QnaActivity::class.java)
            startActivity(intent)
        }

        val helpLayout: ConstraintLayout = view.findViewById(R.id.help_layout)
        helpLayout.setOnClickListener {

        }

        val changeLayout: ConstraintLayout = view.findViewById(R.id.change_layout)
        changeLayout.setOnClickListener {
            val intent = Intent(activity, PwChange1Activity::class.java)
            startActivity(intent)
        }
    }

    // 로그아웃 다이얼로그 표시
    private fun showLogoutDialog() {
        val logoutDialogView = layoutInflater.inflate(R.layout.dialog_logout, null)

        val logoutDialog = AlertDialog.Builder(requireContext())
            .setView(logoutDialogView)
            .create()

        logoutDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

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
        val window = logoutDialog.window
        window?.setLayout((resources.displayMetrics.widthPixels * 0.80).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
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

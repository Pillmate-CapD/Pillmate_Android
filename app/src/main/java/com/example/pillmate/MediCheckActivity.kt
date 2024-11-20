package com.example.pillmate

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pillmate.databinding.ActivityAddMediFinBinding
import com.example.pillmate.databinding.ActivityMediCheckBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediCheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediCheckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로 전달된 데이터 수신
        val mediName = intent.getStringExtra("mediName")
        val disease = intent.getStringExtra("disease")
        val oneEat = intent.getIntExtra("oneEat", 0)
        val oneDay = intent.getIntExtra("oneDay", 0)
        val allDay = intent.getIntExtra("allDay", 0)

        val timeSlotListJson = intent.getStringExtra("timeSlotList")
        val timeSlotListType = object : TypeToken<List<TimeSlotRequest>>() {}.type
        val timeSlotList: List<TimeSlotRequest> =
            Gson().fromJson(timeSlotListJson, timeSlotListType)

        binding.vpMediName.text = mediName
        binding.tvCategory.text = disease
        binding.vpMediAmount.text = "${oneEat}회"
        binding.vpMediPerday.text = "${oneDay}정"
        binding.vpMediDay.text = "${allDay}일"
//        binding.vpMediSlot.text =
//            timeSlotList.joinToString(separator = "\n") { "${it.spinnerTime} | ${it.pickerTime}" }
        binding.vpMediSlot.text =
            timeSlotList.joinToString(separator = "\n") { formatPickerTime(it.spinnerTime, it.pickerTime) }


        binding.btnPreAddMedi.setOnClickListener {
            // MediAddRequest 객체 생성
            val mediAddRequest = MediAddRequest(
                medicineName = mediName ?: "",
                disease = disease ?: "",
                amount = oneEat,
                timesPerDay = oneDay,
                day = allDay,
                timeSlotList = timeSlotList
            )
            sendMediAdd(mediAddRequest)
        }

        binding.tvCheckDayNum.text =
            SimpleDateFormat("yy.MM.dd", Locale.getDefault()).format(Date())

        binding.tvCancel.setOnClickListener {
            finish()
        }

        // 카테고리에 따라 텍스트와 배경색 설정
        val background = binding.tvCategory.background as GradientDrawable
        when (disease) {
            "심혈관질환" -> {
                background.setColor(Color.parseColor("#40FFCEDF"))
                binding.tvCategory.setTextColor(Color.parseColor("#FD5592"))
            }
            "고혈압" -> {
                background.setColor(Color.parseColor("#E6EBFA"))
                binding.tvCategory.setTextColor(Color.parseColor("#1E54DF"))
            }
            "당뇨" -> {
                background.setColor(Color.parseColor("#D6F0EF"))
                binding.tvCategory.setTextColor(Color.parseColor("#0CBBB2"))
            }
            "고지혈증" -> {
                background.setColor(Color.parseColor("#B0FFBCB8"))
                binding.tvCategory.setTextColor(Color.parseColor("#FF453A"))
            }
            "호흡기질환" -> {
                background.setColor(Color.parseColor("#5CEDF2A3"))
                binding.tvCategory.setTextColor(Color.parseColor("#E2B100"))
            }
            "기타" -> {
                background.setColor(Color.parseColor("#F0DDF7"))
                binding.tvCategory.setTextColor(Color.parseColor("#951FC0"))
            }
            else -> {
                background.setColor(Color.parseColor("#F0DDF7"))
                binding.tvCategory.setTextColor(Color.parseColor("#951FC0"))
            }
        }
    }

    private fun sendMediAdd(mediAddRequest: MediAddRequest) {
        val service = RetrofitApi.getRetrofitService // Retrofit 인스턴스 가져오기
        val call = service.addMedi(mediAddRequest)   // MediAddRequest 전체를 한 번에 전송

        val gson = Gson()
        val requestJson = gson.toJson(mediAddRequest)
        Log.d("WriteMediActivity", "보낸 요청: $requestJson")

        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    val message = response.body()
                    // 성공 시 처리할 로직 추가
                    message?.let {
                        Log.d("WriteMediActivity", "약 추가 성공: $it")
                        //showCustomToast(it) // 서버에서 보낸 메시지를 토스트로 표시

                        val intent = Intent(this@MediCheckActivity, AddMediFinActivity::class.java)
                        triggerPillAlarmWorker(this@MediCheckActivity)
                        Log.d("triggerAlarm", "Trigger Alarm: 알람을 재구성 호출")
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.d("WriteMediActivity", "약 추가 실패: ${response.code()}, ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("WriteMediActivity", "API 호출 실패", t)
            }
        })
    }

    // PillAlarmWorker를 트리거하는 함수
    private fun triggerPillAlarmWorker(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<PillAlarmWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "PillAlarmWorker",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun formatPickerTime(spinnerTime: String, pickerTime: String): String {
        // pickerTime 포맷 변환 (예: "18:00:00" -> "오후 06:00")
        val formattedPickerTime = try {
            val hour = pickerTime.split(":")[0].toInt()
            val minute = pickerTime.split(":")[1]

            val amPm = if (hour < 12) "오전" else "오후"
            val hour12 = if (hour % 12 == 0) 12 else hour % 12

            "$amPm ${String.format("%02d:%02d", hour12, minute.toInt())}"
        } catch (e: Exception) {
            // 포맷 변환 실패 시 원본 반환
            pickerTime
        }

        return "$spinnerTime | $formattedPickerTime"
    }
}
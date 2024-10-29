package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        binding.vpMediSlot.text =
            timeSlotList.joinToString(separator = "\n") { "${it.spinnerTime} | ${it.pickerTime}" }


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
}
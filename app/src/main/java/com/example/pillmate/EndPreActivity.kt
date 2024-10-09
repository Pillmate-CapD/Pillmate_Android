package com.example.pillmate

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pillmate.databinding.ActivityEditMediBinding
import com.example.pillmate.databinding.ActivityEndPreBinding
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EndPreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEndPreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding을 사용하여 레이아웃 설정
        binding = ActivityEndPreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 리스트 데이터를 받음 (Serializable로 전달된 데이터)
        val medicineList = intent.getSerializableExtra("medicineList") as? ArrayList<MedicineInfo>

        // 받은 데이터를 처리 및 ViewPager2에 연결
        medicineList?.let { list ->
            val adapter = ViewPagerAdapter(list)
            binding.mediViewPager.adapter = adapter

            // 페이지 인디케이터와 ViewPager2 연결
            binding.pageIndicator.attachToPager(binding.mediViewPager)

            // UI 업데이트 (총 약 개수 등)
            binding.tvAllMedi.text = "${list.size}개"

            // 약 리스트를 MediAddRequest로 변환하여 서버로 전송
            binding.btnPreAddMedi.setOnClickListener {
                list.forEach { medicine ->
                    val mediAddRequest = createMediAddRequest(medicine)
                    sendMediAdd(mediAddRequest)
                }
            }
        }
    }

    // MediAddRequest 객체를 생성하는 함수
    private fun createMediAddRequest(medicine: MedicineInfo): MediAddRequest {
        return MediAddRequest(
            medicineName = medicine.mediName,
            disease = medicine.category,
            amount = medicine.oneEat,
            timesPerDay = medicine.oneDay,
            day = medicine.allDay,
            timeSlotList = medicine.timeSlotList.map { timeSlot ->
                TimeSlotRequest(timeSlot.spinnerTime, timeSlot.pickerTime)
            }
        )
    }


    // MediAddRequest를 서버로 전송하는 함수
    private fun sendMediAdd(mediAddRequest: MediAddRequest) {
        val service = RetrofitApi.getRetrofitService // Retrofit 인스턴스 가져오기
        val call = service.addMedi(mediAddRequest)   // MediAddRequest 전체를 한 번에 전송

        val gson = Gson()
        val requestJson = gson.toJson(mediAddRequest)
        Log.d("EndPreActivity", "보낸 요청: $requestJson")

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val message = response.body()
                    message?.let {
                        Log.d("EndPreActivity", "약 추가 성공: $it")
                        showCustomToast(it) // 서버에서 보낸 메시지를 토스트로 표시
                        //this@EndPreActivity.finish()
                        Log.d("EndPreActivity", "약 추가 성공: $it")
                        showCustomToast(it) // 서버에서 보낸 메시지를 토스트로 표시

// Fragment 전환을 위한 FragmentTransaction
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        val listFragment = ListFragment() // 이동할 ListFragment 인스턴스 생성

// fragment_container는 ListFragment를 담을 수 있는 레이아웃 ID
                        fragmentTransaction.replace(R.id.nav_host_fragment, listFragment)
                        fragmentTransaction.addToBackStack(null)  // 뒤로 가기 스택에 추가 (선택 사항)
                        fragmentTransaction.commit()  // 트랜잭션 실행

// 액티비티 종료 (선택 사항)
                        finish()
                    }
                } else {
                    Log.d("EndPreActivity", "약 추가 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    showCustomToast("약 추가에 실패했습니다: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("EndPreActivity", "API 호출 실패", t)
                showCustomToast("API 호출 실패: ${t.message}")
            }
        })
    }

    // 커스텀 토스트 메시지를 띄우는 함수
    private fun showCustomToast(message: String) {
        // 커스텀 토스트 레이아웃을 인플레이트
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        // 메시지 설정
        val textView = layout.findViewById<TextView>(R.id.tv_toast)
        textView.text = message

        // 커스텀 토스트 생성 및 설정
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM, 0, 80) // 화면 하단에 표시
        toast.show()
    }
}
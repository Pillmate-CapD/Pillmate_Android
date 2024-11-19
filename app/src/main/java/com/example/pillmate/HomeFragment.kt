package com.example.pillmate

import android.animation.ValueAnimator
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillmate.databinding.FragmentHomeBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var pillListAdapter: PillListAdapter
    private lateinit var remainMediAdapter: RemainMediAdapter
    private lateinit var alarmDatabase: AlarmDatabase
    private val sharedPrefs by lazy { requireContext().getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE) }

    private val alarmLogViewModel: AlarmLogViewModel by viewModels()

    private var userName: String? = null

    // 이전 로그 개수를 저장하는 변수
    private var previousLogCount: Int = 0

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(requireContext())
    }

    private val pillViewModel: PillViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // userId 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("userId", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)

        // userId가 유효한 경우에만 로그 관찰
        if (userId != -1) {
            alarmLogViewModel.getLogsForUser(userId).observe(viewLifecycleOwner) { logs ->
                // SharedPreferences에 저장된 마지막 확인한 타임스탬프 가져오기
                val lastCheckedTimestamp = sharedPrefs.getLong("lastCheckedTimestamp", 0L)

                // 새로운 로그가 있는지 확인
                val hasNewLogs = logs.any { it.timestamp.time > lastCheckedTimestamp }

                // 새로운 로그가 있을 때만 view_exist_alarm을 보이게 설정
                binding.viewExistAlarm.visibility = if (hasNewLogs) View.VISIBLE else View.GONE
            }
        }

        // 알림 아이콘을 클릭하면 마지막 확인한 타임스탬프를 업데이트하고 뷰를 숨김
        binding.alertImg.setOnClickListener {
            // view_exist_alarm 비활성화
            binding.viewExistAlarm.visibility = View.INVISIBLE
            // 마지막 확인한 타임스탬프를 현재 시간으로 업데이트
            if (userId != -1) {
                val latestTimestamp = System.currentTimeMillis()
                sharedPrefs.edit().putLong("lastCheckedTimestamp", latestTimestamp).apply()
            }
            // AlarmListActivity로 이동
            val intent = Intent(context, AlarmListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("userName", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "손해인")
        if (userName != null) {

            val sharedPreferences = requireContext().getSharedPreferences("userId", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("userId", -1)

            // 약 리스트 RecyclerView 설정
            pillListAdapter = PillListAdapter(arrayListOf(), this, preferencesHelper,userId)
            binding.pillList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.pillList.adapter = pillListAdapter

            // RemainMediAdapter 초기화 및 설정
            remainMediAdapter = RemainMediAdapter(listOf()) // 빈 리스트로 초기화
            binding.remainMediRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.remainMediRecycler.adapter = remainMediAdapter

            // ViewModel을 통해 약 리스트 업데이트
            pillViewModel.pillItems.observe(viewLifecycleOwner) { pillItems ->
                pillListAdapter.updateItems(pillItems) // 필터링 없이 전체 약 리스트 업데이트
            }

//            binding.homePillProgressBar.setMaxProgress(100)
//            binding.homePillProgressBar.setProgress(100)

            binding.alertImg.setOnClickListener {
                //binding.viewExistAlarm.visibility = View.INVISIBLE
                val intent = Intent(context, AlarmListActivity::class.java)
                startActivity(intent)
            }

            binding.btnNextMedi.isEnabled = false
            binding.btnNextMedi.isClickable = false


            // 현재 월 업데이트
            // 현재 월과 일 업데이트
            val calendar = Calendar.getInstance()
            val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            val day = calendar.get(Calendar.DAY_OF_MONTH).toString()

            binding.calMonth.text = month
            binding.calDay.text = day

            //binding.homeMonth.text = month

            binding.btnPillList.setOnClickListener {
                findNavController().navigate(R.id.listFragment)
            }

//            binding.nonBtnAddMedi.setOnClickListener {
//                findNavController().navigate(R.id.listFragment)
//            }

            binding.btnNextMedi.setOnClickListener {
                // 목표 위치 계산
                val targetY = binding.middleLayout.top

                // ValueAnimator로 스크롤 애니메이션 설정
                val animator = ValueAnimator.ofInt(binding.scrollView.scrollY, targetY)
                animator.duration = 500 // 애니메이션 지속 시간 (1000ms = 1초)
                animator.interpolator = DecelerateInterpolator() // 점점 느려지는 효과

                animator.addUpdateListener { valueAnimator ->
                    val animatedValue = valueAnimator.animatedValue as Int
                    binding.scrollView.scrollTo(0, animatedValue)
                }
                animator.start()
            }

            binding.btnNotEatenMedi.setOnClickListener {
                // 목표 위치 계산
                val targetY = binding.middleLayout.top

                // ValueAnimator로 스크롤 애니메이션 설정
                val animator = ValueAnimator.ofInt(binding.scrollView.scrollY, targetY)
                animator.duration = 500 // 애니메이션 지속 시간 (1000ms = 1초)
                animator.interpolator = DecelerateInterpolator() // 점점 느려지는 효과

                animator.addUpdateListener { valueAnimator ->
                    val animatedValue = valueAnimator.animatedValue as Int
                    binding.scrollView.scrollTo(0, animatedValue)
                }
                animator.start()
            }

        }
        return binding.root
    }

    private fun fetchMain() {
        val service = RetrofitApi.getRetrofitService
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        Log.d("fetchMain", "Current Time: $currentTime")

        // API 호출 준비 단계
        val call = service.getMain(currentTime)
        Log.d("fetchMain", "API Call Initiated with time parameter: $currentTime")

        call.enqueue(object : Callback<MainPageResponse> {
            override fun onResponse(
                call: Call<MainPageResponse>,
                response: Response<MainPageResponse>
            ) {
                Log.d("fetchMain", "API Response Received")
                Log.d("fetchMain", "Response Code: ${response.code()}")
                Log.d("fetchMain", "Response Message: ${response.message()}")

                if (response.isSuccessful) {
                    val mainResponse = response.body()
                    Log.d("fetchMain", "Response Body: $mainResponse")

                    mainResponse?.let { response ->
                        Log.d("fetchMain", "Parsed Response Data: $response")

                        pillViewModel.loadPillItems(
                            response.medicineAlarmRecords.map {
                                val formattedTime = convertTimeTo12HourFormat(it.time)
                                PillListItem(
                                    time = formattedTime,
                                    name = it.name,
                                    isEaten = it.isEaten,
                                    medicineId = it.medicineId,
                                    alarmId = it.alarmId
                                )
                            }
                        )

                        // medicineAlarmRecords 개수 로그 출력
                        val medicineRecordCount = response.medicineAlarmRecords.size
                        binding.mediNum.text = "총 ${medicineRecordCount}정"

                        if (medicineRecordCount > 0) {
                            binding.tvNextMedi.text = "가장 먼저 먹어야 할 약이에요!"
                            binding.btnNextMedi.text = "다음 약 먹으러 가기"
                            binding.btnNextMedi.isEnabled = true
                            binding.btnNextMedi.isClickable = true
                            binding.btnNextMedi.backgroundTintList = null
                            binding.btnNextMedi.alpha = 1f
                            binding.btnNextMedi.setTextColor(Color.parseColor("#1E54DF"))

                        }

                        // upcomingAlarm의 medicineName과 time을 변환하여 UI에 표시
                        val upcomingAlarm = response.upcomingAlarm
                        val formattedTime = convertTimeTo12HourFormat(upcomingAlarm.time)
                        binding.tvNext.text = "$formattedTime ${upcomingAlarm.medicineName}"

                        // isEaten이 false인 약 리스트 필터링
                        val remainingMedicine = response.medicineAlarmRecords.filter { !it.isEaten }
                        val remainingAlarmCount = remainingMedicine.size

                        // 복용 안한 약 개수 텍스트 설정
                        binding.tvLastMediGuide.text = "복용 안한 약 일정이 ${remainingAlarmCount}개 있어요!"

                        // 복용 안한 약 이름 리스트 생성
                        val remainingMedicineNames = if (remainingAlarmCount > 1) {
                            "${remainingMedicine.first().name} 외 ${remainingAlarmCount - 1}정"
                        } else {
                            remainingMedicine.firstOrNull()?.name ?: "없음"
                        }
                        binding.tvLastMedi.text = remainingMedicineNames


                        val remainingMedicineItems = response.remainingMedicine.map {
                            RemainMediItem(
                                it.name,
                                it.category,
                                it.day
                            )
                        }
                        remainMediAdapter.updateItems(remainingMedicineItems)

                        // 리스트가 비어있는지 확인하여 tvNoEaten의 visibility를 변경
                        binding.tvNoEaten.visibility =
                            if (remainingMedicineItems.isEmpty()) View.VISIBLE else View.GONE


                        // 화면 업데이트
                        if (response.medicineAlarmRecords.isNullOrEmpty()) {
                            Log.d(
                                "fetchMain",
                                "No medicine records found - updating UI to show no data layout"
                            )
                            //binding.pillList.visibility = View.VISIBLE
                            binding.btnAllPill.visibility = View.VISIBLE
                            binding.homeNonDataLayout.visibility = View.VISIBLE
                        } else {
                            Log.d("fetchMain", "Medicine records found - updating UI to show data")
                            binding.pillList.visibility = View.VISIBLE
                            binding.txtGo.text = "약 알람 전체 보기"
                            binding.btnAllPill.visibility = View.VISIBLE
                            binding.homeNonDataLayout.visibility = View.GONE
                        }

                        binding.homeGoodPillName.text = response.bestRecord.name
                        binding.homePillGoodUser.text = "${response.bestRecord.taken}정 "
                        binding.homePillGoodNum.text = "/ ${response.bestRecord.scheduled}정 "

                        // bestRecord의 taken과 scheduled 값을 기반으로 퍼센트 계산 (정수형)
                        val bestTaken = response.bestRecord.taken
                        val bestScheduled = response.bestRecord.scheduled
                        val bestPercentage =
                            if (bestScheduled != 0) (bestTaken * 100) / bestScheduled else 0

// worstRecord의 taken과 scheduled 값을 기반으로 퍼센트 계산 (정수형)
                        val worstTaken = response.worstRecord.taken
                        val worstScheduled = response.worstRecord.scheduled
                        val worstPercentage =
                            if (worstScheduled != 0) (worstTaken * 100) / worstScheduled else 0


                        binding.goodProgressBar.setProgress(bestPercentage)

                        binding.homeBadPillName.text = response.worstRecord.name
                        binding.homePillBadUser.text = "${response.worstRecord.taken}정 "
                        binding.homePillBadNum.text = "/ ${response.worstRecord.scheduled}정 "


                        binding.tvInfo.text = "${response.bestRecord.taken}정으로 제일 잘 챙겨 먹었어요!"
                        binding.tvBadInfo.text = "${response.worstRecord.taken}정으로 조금 더 열심히 복용해보세요!"

                        binding.badProgressBar.setProgress(worstPercentage)

                        // remainingMedicineItems의 개수를 구합니다
                        val itemCount = remainingMedicineItems.size
                        binding.tvWorst.text = "복용률 ${itemCount}위"

                    }
                } else {
                    // 응답이 실패했을 때의 처리
                    response.errorBody()?.string()?.let { errorBody ->
                        try {
                            val errorJson = JSONObject(errorBody)
                            val code = errorJson.optString("code")

                            if (code == "NOT_FOUND_ALARM") {
                                Log.d("fetchMain", "No active alarm found - showing no data layout")
                                binding.homeNonDataLayout.visibility = View.VISIBLE
                                binding.pillList.visibility = View.GONE
                                binding.btnAllPill.visibility = View.VISIBLE

                                binding.txt4.visibility = View.VISIBLE
                                binding.layoutGood.visibility = View.INVISIBLE
                                binding.layoutBad.visibility = View.INVISIBLE
                                binding.dot1.visibility = View.INVISIBLE
                                binding.dot2.visibility = View.INVISIBLE
                                binding.dot3.visibility = View.INVISIBLE
                                binding.tvNoEaten.visibility = View.VISIBLE
                                //binding.tvLastMediGuide.text = message
                            } else {
                                Log.d("fetchMain", "Unhandled error code: $code")
                            }
                        } catch (e: Exception) {
                            Log.e("fetchMain", "Error parsing error response: ${e.message}")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<MainPageResponse>, t: Throwable) {
                Log.e("fetchMain", "API Call Failed", t)
            }
        })
    }


    private fun convertTimeTo12HourFormat(time: String): String {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val localTime = LocalTime.parse(time, timeFormatter)
        val hour = localTime.hour
        val minute = localTime.minute
        val amPm = if (hour < 12) "오전" else "오후"
        val formattedHour = if (hour % 12 == 0) 12 else hour % 12
        return "$amPm $formattedHour:${String.format("%02d", minute)}"
    }

    override fun onResume() {
        fetchMain()
        val calendar = Calendar.getInstance()
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        //binding.homeMonth.text = month

        val sharedPreferences = requireActivity().getSharedPreferences("userName", MODE_PRIVATE)
        userName = sharedPreferences.getString("userName", "Loading name failed")
        binding.userTxt.text = userName
        super.onResume()
    }
}

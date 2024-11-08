package com.example.pillmate

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.*

class MyHealthEditD2Activity : AppCompatActivity() {

    private lateinit var btnNext: Button
    private val dateEditTexts = mutableListOf<EditText>()
    private val diseaseStartDates = mutableMapOf<String, LocalDate>()
    private lateinit var selectedDiseases: ArrayList<String>
    private lateinit var savedDiseaseDates: Map<String, LocalDate>
    private lateinit var selectedSymptoms: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard2)

        val backButton: ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            onBackPressed()
        }

        selectedDiseases = intent.getStringArrayListExtra("selectedDiseases") ?: arrayListOf()
        val receivedDates = intent.getSerializableExtra("savedDiseaseDates") as? HashMap<String, String> ?: hashMapOf()
        selectedSymptoms = intent.getStringArrayListExtra("selectedSymptoms") ?: arrayListOf()

        // String -> LocalDate 변환
        savedDiseaseDates = receivedDates.mapValues { LocalDate.parse(it.value) }

        Log.d("MyHealthEditD2Activity", "Selected Diseases: $selectedDiseases")
        Log.d("MyHealthEditD2Activity", "Saved Disease Dates: $savedDiseaseDates")
        Log.d("MyHealthEditD2Activity", "Selected Symptoms: $selectedSymptoms")

        setupDateInputs()
        setupNextButton()
    }

    private fun setupDateInputs() {
        val linearLayout: LinearLayout = findViewById(R.id.linearLayout)

        // 현재 날짜를 가져옴
        val currentDate = LocalDate.now()

        selectedDiseases.forEach { disease ->
            val horizontalLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, dpToPx(10f).toInt(), 0, dpToPx(10f).toInt())
            }
            linearLayout.addView(horizontalLayout)

            val diseaseTextView = TextView(this).apply {
                text = disease
                textSize = 20f
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
                setTextColor(Color.parseColor("#3E3E3E"))
            }
            horizontalLayout.addView(diseaseTextView)

            val dateEditText = EditText(this).apply {
                //hint = "2024년 11월"
                hint = "${currentDate.year}년 ${currentDate.monthValue}월"
                isFocusable = false
                setBackgroundResource(R.drawable.onboard_et_gray)
                layoutParams = LinearLayout.LayoutParams(
                    0, resources.getDimensionPixelSize(R.dimen.onboard_et_height), 2f
                )
                gravity = Gravity.CENTER
                setHintTextColor(Color.parseColor("#6C6B6B"))

                // 저장된 날짜가 있으면 표시
                val savedDate = savedDiseaseDates[disease]
                if (savedDate != null) {
                    setText(formatDate(savedDate))
                    setBackgroundResource(R.drawable.onboard_et_blue)
                }

                setOnClickListener {
                    showDatePickerDialog(this)
                }
            }
            horizontalLayout.addView(dateEditText)
            dateEditTexts.add(dateEditText)
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.date_picker)

        val yearPicker: NumberPicker = dialog.findViewById(R.id.yearPicker)
        val monthPicker: NumberPicker = dialog.findViewById(R.id.monthPicker)
        val btnCancel: TextView = dialog.findViewById(R.id.btn_cancel)
        val btnConfirm: TextView = dialog.findViewById(R.id.btn_confirm)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 100..currentYear + 100).map { "${it}년" }.toTypedArray()
        val months = (1..12).map { "${it}월" }.toTypedArray()

        yearPicker.minValue = 0
        yearPicker.maxValue = years.size - 1
        yearPicker.displayedValues = years
        yearPicker.value = years.indexOf("${currentYear}년")

        monthPicker.minValue = 0
        monthPicker.maxValue = months.size - 1
        monthPicker.displayedValues = months
        monthPicker.value = Calendar.getInstance().get(Calendar.MONTH)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val selectedYear = years[yearPicker.value].removeSuffix("년").toInt()
            val selectedMonth = months[monthPicker.value].removeSuffix("월").toInt()

            val selectedDate = LocalDate.of(selectedYear, selectedMonth, 1)
            editText.setText(formatDate(selectedDate))
            editText.setBackgroundResource(R.drawable.onboard_et_blue)
            diseaseStartDates[editText.hint.toString()] = selectedDate
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun formatDate(date: LocalDate): String {
        return "${date.year}년 ${date.monthValue}월"
    }

    private fun setupNextButton() {
        btnNext = findViewById(R.id.btn_next)
        btnNext.setOnClickListener {
            if (dateEditTexts.all { it.text.isNotBlank() }) {
                // 질병 정보 설정 (startDate를 LocalDate 형식으로 변환)
                val diseases = dateEditTexts.mapIndexed { index, editText ->
                    val diseaseName = selectedDiseases[index]
                    val dateText = editText.text.toString()

                    // 입력된 날짜 문자열을 LocalDate로 변환
                    val year = dateText.substringBefore("년").trim().toInt()
                    val month = dateText.substringAfter("년").substringBefore("월").trim().toInt()
                    val day = 1  // 정확한 일자가 없으면 기본값으로 1을 사용
                    val startDate = LocalDate.of(year, month, day).toString()

                    // 변환된 startDate를 사용하여 객체 생성
                    EditPMyHealthInfo(disease = diseaseName, startDate = startDate)
                }

                // 기존 증상 정보 유지
                val symptoms = selectedSymptoms.map { symptom ->
                    EditPMySymptom(name = symptom)
                }

                // 요청 객체 생성
                val request = EditPMyHealthInfoRequest(diseases = diseases, symptoms = symptoms)

                // PATCH 요청 보내기
                RetrofitApi.getRetrofitService.updateEditMyHealthInfo(request).enqueue(object :
                    Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@MyHealthEditD2Activity, "건강 정보가 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                            // MyHealthInfoActivity로 이동
                            val intent = Intent(this@MyHealthEditD2Activity, MyHealthInfoActivity::class.java)
                            startActivity(intent)
                            finish() // 현재 액티비티 종료
                        } else {
                            Toast.makeText(this@MyHealthEditD2Activity, "업데이트에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@MyHealthEditD2Activity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        Log.e("MyHealthEditD2Activity", "onFailure: ${t.message}", t)
                    }
                })
            } else {
                Toast.makeText(this, "모든 날짜를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}

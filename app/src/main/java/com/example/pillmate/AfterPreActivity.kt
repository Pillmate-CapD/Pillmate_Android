package com.example.pillmate

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pillmate.databinding.ActivityAfterPreBinding
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID


class AfterPreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAfterPreBinding

//    private val secretKey: String by lazy {
//        BuildConfig.OCR_SECRET_KEY
//    }
//
//    private val invokeUrl: String by lazy {
//        BuildConfig.INVOKE_URL
//    }

    private val template_secretKey: String by lazy {
        BuildConfig.TEMPLATE_SECRET_KEY
    }

    private val template_invokeUrl: String by lazy {
        BuildConfig.TEMPLATE_INVOKE_URL
    }

    companion object {
        val BOUNDARY: String by lazy {
            "----WebKitFormBoundary${UUID.randomUUID().toString().replace("-", "")}"
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 설정
        binding = ActivityAfterPreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 이미지 파일 경로를 받음
        val photoPath = intent.getStringExtra("photoPath")
        if (photoPath != null) {
            Log.d("AfterPreActivity", "받은 사진 경로: $photoPath")
            val bitmap = BitmapFactory.decodeFile(photoPath)

            if (bitmap != null) {
                Log.d("AfterPreActivity", "비트맵 정보: 너비=${bitmap.width}, 높이=${bitmap.height}")
                binding.imgMediCamera.setImageBitmap(bitmap)
            } else {
                Log.e("AfterPreActivity", "비트맵 로드 실패: $photoPath")
            }
        } else {
            Log.e("AfterPreActivity", "이미지 파일 경로가 전달되지 않았습니다.")
        }

        binding.btnNext.setOnClickListener {
            photoPath?.let {
                val bitmap = BitmapFactory.decodeFile(it)

                if (bitmap == null) {
                    Log.e("TemplateOCR", "비트맵 생성 실패. 파일 경로: $it")
                    return@let
                }

                Log.d("TemplateOCR", "비트맵 정보: 너비=${bitmap.width}, 높이=${bitmap.height}")
                //performTemplateOcrWithFormData(bitmap)
                performTemplateOcrWithJson(bitmap)
                binding.loadingLayout.visibility = View.VISIBLE
            } ?: Log.e("TemplateOCR", "이미지 파일을 찾을 수 없습니다.")
        }

        Glide.with(this).load(R.raw.loading).into(binding.recogIngImage)

    }

    private fun getBitmapFromLocalFile(): Bitmap {
        val drawableId = R.drawable.ocr_img // drawable 파일 이름이 medi3.jpg인 경우
        return BitmapFactory.decodeResource(resources, drawableId)
    }


    private fun performTemplateOcrWithJson(bitmap: Bitmap) {
        if (!isNetworkAvailable()) {
            Log.e("TemplateOCR", "인터넷 연결이 없습니다.")
            return
        }

        Thread {
            try {
                val ocrResponse = sendTemplateOcrRequestWithJson(bitmap)
                runOnUiThread { processTemplateOcrResponse(ocrResponse) }
            } catch (e: Exception) {
                Log.e("TemplateOCR", "OCR 요청에 실패했습니다: ${e.message}", e)
            }
        }.start()
    }


    private fun sendTemplateOcrRequestWithJson(bitmap: Bitmap): String {
        val url = URL(template_invokeUrl)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            doOutput = true
            doInput = true
            useCaches = false
            readTimeout = 30000
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("X-OCR-SECRET", template_secretKey)
        }

        try {
            // JSON 요청 본문 생성
            val jsonRequest = JSONObject().apply {
                put("version", "V2")
                put("requestId", UUID.randomUUID().toString())
                put("timestamp", System.currentTimeMillis())
                put("lang", "ko") // 언어 설정
                put("templateId", "34479") // 템플릿 ID
                val imagesArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("format", "jpg")
                        put("name", "ocr_image") // 유효한 이름을 설정합니다.
                        put("data", bitmapToBase64(bitmap)) // Base64 이미지 데이터 추가
                    })
                }
                put("images", imagesArray)
            }.toString()

            // 로그 출력
            Log.d("TemplateOCR", "JSON 요청 본문: $jsonRequest")

            // 요청 본문 전송
            connection.outputStream.use { output ->
                output.write(jsonRequest.toByteArray())
                output.flush()
            }

            // 서버 응답 처리
            val responseCode = connection.responseCode
            if (responseCode != 200) {
                val errorMessage = connection.errorStream.bufferedReader().readText()
                Log.e("TemplateOCR", "서버 오류 응답: $errorMessage")
                throw Exception("HTTP 오류 코드: $responseCode")
            }

            val response = connection.inputStream.bufferedReader().readText()
            Log.d("TemplateOCR", "서버 응답: $response")
            return response
        } catch (e: Exception) {
            Log.e("TemplateOCR", "요청 처리 중 오류 발생: ${e.message}", e)
            throw e
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

        // 로그 출력
        Log.d("TemplateOCR", "Base64 이미지 데이터 크기: ${base64Image.length}")
        return base64Image
    }


    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        // 로그 추가
        Log.d("TemplateOCR", "비트맵을 바이트 배열로 변환. 크기: ${stream.toByteArray().size}")

        return stream.toByteArray()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnected == true
    }


    private fun processTemplateOcrResponse(response: String) {
        try {
            val jsonResponse = JSONObject(response)
            val images = jsonResponse.getJSONArray("images")

            // 'fields'는 'images' 배열의 첫 번째 객체 안에 있음
            if (images.length() > 0) {
                val firstImage = images.getJSONObject(0)

                // 'fields' 키 확인
                if (!firstImage.has("fields")) {
                    Log.e("TemplateOCR", "JSON 응답에 'fields' 키가 없습니다: $response")
                    navigateToFailActivity()
                    return
                }

                val fields = firstImage.getJSONArray("fields")

                if (fields.length() == 0) {
                    Log.e("TemplateOCR", "필드가 비어 있습니다.")
                    navigateToFailActivity()
                    return
                }

                // 추출된 데이터 저장
                val extractedData = mutableListOf<Map<String, String>>()
                for (i in 0 until fields.length()) {
                    val field = fields.getJSONObject(i)
                    val name = field.optString("name", "Unknown")
                    val value = field.optString("inferText", "Unknown")
                    extractedData.add(mapOf("name" to name, "value" to value))
                }

                Log.d("TemplateOCR", "추출된 데이터: $extractedData")

                // 의약품 명칭과 관련 데이터를 매핑
                val medicines = mutableListOf<Map<String, String>>()
                val dosageList = mutableListOf<String>()
                val frequencyList = mutableListOf<String>()
                val durationList = mutableListOf<String>()
                var hospitalName: String? = null

                extractedData.forEach {
                    when {
                        it["name"]?.contains("의약품 명칭") == true -> medicines.add(mapOf("name" to it["value"]!!))
                        it["name"]?.contains("투약량") == true -> dosageList.add(it["value"]!!)
                        it["name"]?.contains("투여횟수") == true -> frequencyList.add(it["value"]!!)
                        it["name"]?.contains("총투약일수") == true -> durationList.add(it["value"]!!)
                        it["name"]?.contains("병원명") == true -> hospitalName = it["value"]
                    }
                }

                // 약물 정보와 투약 데이터를 결합
                medicines.forEachIndexed { index, medicine ->
                    medicines[index] = medicine.toMutableMap().apply {
                        this["dosage"] = dosageList.getOrNull(index) ?: "정보 없음"
                        this["frequency"] = frequencyList.getOrNull(index) ?: "정보 없음"
                        this["duration"] = durationList.getOrNull(index) ?: "정보 없음"
                    }
                }

                Log.d("UpdatedData", "Updated Medicines Data: $medicines")
                Log.d("HospitalName", "병원명: $hospitalName")

                // fetchMediInfo 호출하여 약물 정보 가져오기
                fetchMediInfo(medicines, hospitalName)
            } else {
                Log.e("TemplateOCR", "JSON 응답에 'images' 배열이 비어 있습니다.")
                navigateToFailActivity()
            }
        } catch (e: Exception) {
            Log.e("TemplateOCR", "OCR 응답 처리 중 오류 발생: ${e.message}", e)
            navigateToFailActivity()
        }
    }

    private fun fetchMediInfo(
        updatedData: MutableList<Map<String, String>>,
        hospitalName: String?
    ) {
        val service = RetrofitApi.getRetrofitService // Retrofit 인스턴스 가져오기

        // "name" 값만 추출하여 MediInfoRequest 리스트로 변환
        val nameList = updatedData.mapNotNull { data ->
            data["name"]?.let { MediInfoRequest(it) } // "name"이 null이 아니면 MediInfoRequest로 변환
        }

        // 서버로 MediInfoRequest 리스트를 POST 요청으로 전송
        val call = service.postMediInfo(nameList) // List<MediInfoRequest>를 전송
        call.enqueue(object : Callback<List<MediInfoResponse>> {
            override fun onResponse(
                call: Call<List<MediInfoResponse>>,
                response: Response<List<MediInfoResponse>>
            ) {
                if (response.isSuccessful) {
                    val mediInfoList = response.body() ?: emptyList()

                    mediInfoList.forEach { mediInfo ->
                        Log.d(
                            "sendNamesToServer",
                            "약물 정보 수신 성공: ${mediInfo.name}, ${mediInfo.photo}, ${mediInfo.category}"
                        )

                        // updatedData에서 해당 이름을 가진 항목을 찾아 업데이트
                        updatedData.find { it["name"] == mediInfo.name }?.let { data ->
                            val updatedEntry = data.toMutableMap().apply {
                                this["photo"] = mediInfo.photo ?: "이미지 없음" // 사진이 null일 경우 기본값 설정
                                this["category"] = if (mediInfo.category == "db에서 해당 약을 찾을 수 없습니다") {
                                    "" // "db에서 해당 약을 찾을 수 없습니다"면 빈 문자열로 설정
                                } else {
                                    mediInfo.category ?: "" // null일 경우에도 빈 문자열로 설정
                                }
                            }
                            // updatedData 리스트에서 기존 항목을 새로운 값으로 교체
                            val index = updatedData.indexOf(data)
                            if (index != -1) {
                                updatedData[index] = updatedEntry
                            }
                        }
                    }

                    // **응답받은 데이터 수만큼만 처리**
                    val filteredUpdatedData = updatedData.filter { it["name"]?.isNotEmpty() == true }

                    // 업데이트된 데이터를 로그로 출력
                    Log.d("FilteredUpdatedData", "최종 전달할 데이터: $filteredUpdatedData")

                    // 모든 데이터를 업데이트한 후 Intent로 PreMediActivity로 전환
                    val intent = Intent(this@AfterPreActivity, PreMediActivity::class.java).apply {
                        putExtra("updatedData", ArrayList(filteredUpdatedData)) // ArrayList로 변환하여 전달
                        putExtra("hospitalName", hospitalName) // 병원명도 함께 전달
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("sendNamesToServer", "Error: ${response.code()}")
                    // 실패 시 에러 처리 (예: Toast 표시)
                }
            }

            override fun onFailure(call: Call<List<MediInfoResponse>>, t: Throwable) {
                Log.e("sendNamesToServer", "API 호출 실패: ${t.message}")
                // 실패 시 에러 처리 (예: Toast 표시)
            }
        })
    }

    private fun navigateToFailActivity() {
        val intent = Intent(this@AfterPreActivity, FailActivity::class.java).apply {
            putExtra("CheckActivity", "preActivity") // 실패의 원인을 표시하거나 다음 액션을 위해 데이터를 전달
        }
        startActivity(intent)
        overridePendingTransition(0, 0) // 애니메이션 없음
        finish()
    }
}
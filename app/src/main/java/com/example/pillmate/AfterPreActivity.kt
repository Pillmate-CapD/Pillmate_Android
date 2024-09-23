package com.example.pillmate

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pillmate.databinding.ActivityAfterPreBinding
import org.json.JSONArray
import org.json.JSONObject
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

    private val secretKey: String by lazy {
        BuildConfig.OCR_SECRET_KEY
    }

    private val invokeUrl: String by lazy {
        BuildConfig.INVOKE_URL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 설정
        binding = ActivityAfterPreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 이미지 파일 경로를 받음
        val photoPath = intent.getStringExtra("photoPath")
        if (photoPath != null) {
            // BitmapFactory를 사용하여 이미지 파일을 Bitmap으로 로드
            val bitmap = BitmapFactory.decodeFile(photoPath)

            // ImageView에 비트맵 설정
            binding.imgMediCamera.setImageBitmap(bitmap)
        }

        binding.btnX.setOnClickListener{
            finish()
        }

        binding.btnAgain.setOnClickListener{
            finish()
        }

        binding.btnNext.setOnClickListener {
            //OCR 실행을 여기서 바로 할건지 아니면,
//            if(photoPath != null){
//                // BitmapFactory를 사용하여 이미지 파일을 Bitmap으로 로드
//                val bitmap = BitmapFactory.decodeFile(photoPath)
//
//                performOcrWithBitmap(bitmap)
//                binding.loadingLayout.visibility = View.VISIBLE
//            }

            performOcrWithLocalImage()
            //performOcrWithBitmap(bitmap)
            binding.loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun getBitmapFromLocalFile(): Bitmap {
        val drawableId = R.drawable.ocr_img // drawable 파일 이름이 medi3.jpg인 경우
        return BitmapFactory.decodeResource(resources, drawableId)
    }

    private fun performOcrWithLocalImage() {
        val bitmap = getBitmapFromLocalFile()
        performOcrWithBitmap(bitmap)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnected == true
    }

    private fun performOcrWithBitmap(bitmap: Bitmap) {
        if (!isNetworkAvailable()) {
            runOnUiThread {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
               //binding.tvOcr.text = "No internet connection"
            }
            return
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        Thread {
            try {
                Log.d("OCR_REQUEST", "Connecting to URL: $invokeUrl")
                val url = URL(invokeUrl)
                val con = url.openConnection() as HttpURLConnection
                con.useCaches = false
                con.doInput = true
                con.doOutput = true
                con.readTimeout = 30000
                con.requestMethod = "POST"
                val boundary = "----" + UUID.randomUUID().toString().replace("-", "")
                con.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                con.setRequestProperty("X-OCR-SECRET", secretKey)

                val json = JSONObject().apply {
                    put("version", "V2")
                    put("requestId", UUID.randomUUID().toString())
                    put("timestamp", System.currentTimeMillis())
                    put("lang", "ko")  // 언어를 지정합니다. 예: 한국어 "ko"
                    put("enableTableDetection", true)  // 표 인식을 활성화

                    val imageObject = JSONObject().apply {
                        put("format", "jpg")
                        put("name", "demo")
                    }

                    put("images", JSONArray().put(imageObject))
                }

                val postParams = json.toString()

                con.connect()
                Log.d("OCR_REQUEST", "Connected. Writing data...")
                DataOutputStream(con.outputStream).use { wr ->
                    writeMultiPart(wr, postParams, byteArray, boundary)
                    wr.close()
                }

                val responseCode = con.responseCode
                Log.d("OCR_REQUEST", "Response code: $responseCode")
                val response = StringBuffer()
                BufferedReader(InputStreamReader(if (responseCode == 200) con.inputStream else con.errorStream)).use { br ->
                    var inputLine: String?
                    while (br.readLine().also { inputLine = it } != null) {
                        response.append(inputLine)
                    }
                }

                runOnUiThread {
                    if (responseCode == 200) {
                        //binding.tvOcr.text = "OCR 성공: ${response}"
                        Log.d("OCR_RESPONSE", "OCR 성공: ${response}")

                        // 응답을 기반으로 데이터를 분류하고 저장
                        processOcrResponse(response.toString())
                    } else {
                        //binding.tvOcr.text = "OCR 실패: ${response}"
                        Log.e("OCR_RESPONSE", "OCR 실패: ${response}")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    //binding.tvOcr.text = "OCR 에러: ${e.message}"
                    Log.e("OCR_REQUEST", "OCR 에러: ${e.message}", e)
                }
                e.printStackTrace()
            }
        }.start()
    }

    private fun processOcrResponse(response: String) {
        try {
            val jsonResponse = JSONObject(response)
            val images = jsonResponse.getJSONArray("images")
            val tables = images.getJSONObject(0).getJSONArray("tables")

            // 결과를 저장할 리스트
            val extractedData = mutableListOf<Map<String, String>>()

            var isProcessingDrugs = false

            // 필드를 저장할 변수 선언
            var nameColumnIndex = -1
            var dosageColumnIndex = -1
            var frequencyColumnIndex = -1
            var durationColumnIndex = -1

            // 중복 방지용 Set
            val processedRows = mutableSetOf<Int>()

            for (tableIndex in 0 until tables.length()) {
                val cells = tables.getJSONObject(tableIndex).getJSONArray("cells")

                for (i in 0 until cells.length()) {
                    val cell = cells.getJSONObject(i)
                    val rowIndex = cell.getInt("rowIndex")
                    val columnIndex = cell.getInt("columnIndex")

                    // 중복된 row 처리 방지
                    if (processedRows.contains(rowIndex)) {
                        Log.d("OCR_PROCESSING", "중복된 rowIndex: $rowIndex 처리 건너뜀")
                        continue
                    }

                    // 셀 텍스트 결합
                    val cellTextLines = cell.getJSONArray("cellTextLines")
                    val cellTextBuilder = StringBuilder()

                    for (lineIndex in 0 until cellTextLines.length()) {
                        val line = cellTextLines.getJSONObject(lineIndex)
                        val cellWords = line.getJSONArray("cellWords")

                        for (wordIndex in 0 until cellWords.length()) {
                            val word = cellWords.getJSONObject(wordIndex)
                            cellTextBuilder.append(word.getString("inferText")).append(" ")
                        }
                    }

                    val cellText = cellTextBuilder.toString().trim()
                    Log.d(
                        "OCR_PROCESSING",
                        "셀 처리 중: rowIndex=$rowIndex, columnIndex=$columnIndex, 추출된 셀 텍스트=$cellText"
                    )

                    // '처방 의약품의 명칭' 셀을 찾으면 플래그 설정
                    if ("처방의약품의명칭" in cellText.replace(" ", "")) {
                        isProcessingDrugs = true
                        Log.d("OCR_PROCESSING", "'처방의약품의 명칭' 발견. 플래그 설정")
                        nameColumnIndex = columnIndex
                        Log.d("OCR_PROCESSING", "명칭 컬럼 인덱스 설정: $nameColumnIndex")
                        continue
                    }

                    // 플래그가 설정된 이후의 셀들을 처리 (실제 약품 정보 추출)
                    if (isProcessingDrugs) {
                        // 필드 이름을 찾음
                        when {
                            "1회 투약량" in cellText -> {
                                dosageColumnIndex = columnIndex
                                Log.d("OCR_PROCESSING", "1회 투약량 컬럼 인덱스 설정: $dosageColumnIndex")
                            }
                            "1일 투여횟수" in cellText -> {
                                frequencyColumnIndex = columnIndex
                                Log.d("OCR_PROCESSING", "1일 투여횟수 컬럼 인덱스 설정: $frequencyColumnIndex")
                            }
                            "총 투약일수" in cellText -> {
                                durationColumnIndex = columnIndex
                                Log.d("OCR_PROCESSING", "총 투약일수 컬럼 인덱스 설정: $durationColumnIndex")
                            }
                        }

                        // 필드 인덱스가 모두 설정된 경우, 데이터를 한 번에 추출
                        if (nameColumnIndex != -1 && dosageColumnIndex != -1 && frequencyColumnIndex != -1 &&
                            durationColumnIndex != -1
                        ) {

                            // 데이터를 추출
                            Log.d("OCR_PROCESSING", "데이터 추출 시작")

                            val name = cleanName(getNextCellValue(cells, rowIndex, nameColumnIndex))
                            val dosage = cleanDosage(getNextCellValue(cells, rowIndex, dosageColumnIndex))
                            val frequency = cleanFrequency(
                                getNextCellValue(
                                    cells,
                                    rowIndex,
                                    frequencyColumnIndex
                                )
                            )
                            val duration = cleanDuration(
                                getNextCellValue(
                                    cells,
                                    rowIndex,
                                    durationColumnIndex
                                )
                            )

                            // 이름이 [로 시작하거나 숫자로 시작하는 경우에만 저장
                            if (name.startsWith("[") || name.matches(Regex("^\\d.*"))) {
                                // 데이터 저장
                                extractedData.add(
                                    mapOf(
                                        "명칭" to name,
                                        "1회 투약량" to dosage,
                                        "1일 투여횟수" to frequency,
                                        "총 투약일수" to duration,
                                    )
                                )

                                // 해당 row 처리 완료로 설정
                                processedRows.add(rowIndex)
                            } else {
                                Log.d("OCR_PROCESSING", "명칭 조건에 맞지 않아 저장하지 않음: $name")
                            }
                        }
                    }
                }
            }

            // 최종적으로 추출된 데이터를 로그로 출력
            extractedData.forEach { data ->
                Log.d("ExtractedData", "명칭: ${data["명칭"]}, 1회 투약량: ${data["1회 투약량"]}, 1일 투여횟수: ${data["1일 투여횟수"]}, 총 투약일수: ${data["총 투약일수"]}")
                // 파일 경로를 AfterPreActivity로 전달
            }

            // 여기서 전달하기 전에 명칭을 서버로 보내서 서버에서 해당 약이 있는지 확인하고 사진을 받아와야 함.
            // 받아온 후에 해당 내용을 PreMediActivity로 사진이랑 같이 전달하기
            val intent = Intent(this, PreMediActivity::class.java)
            startActivity(intent)  // AfterPreActivity 실행


        } catch (e: Exception) {
            Log.e("OCR_PROCESSING", "Error processing OCR response: ${e.message}")
            e.printStackTrace()
        }
    }

    //TODO: [급여][]아클펜정_(. 이런식으로 명칭을 받고 있어서 해당 내용을 수정해서 적어야 함
    private fun cleanName(name: String): String {
        Log.d("CLEAN_FUNCTION", "이름 정제 중: $name")
        return name.replace(Regex("\\d|\\(.*?\\)"), "").trim()
    }

    private fun cleanDosage(dosage: String): String {
        Log.d("CLEAN_FUNCTION", "1회 투약량 정제 중: $dosage")
        return dosage.replace(Regex(" cc| gm| 개| 정"), "").trim() + " cc"
    }

    private fun cleanFrequency(frequency: String): String {
        Log.d("CLEAN_FUNCTION", "1일 투여횟수 정제 중: $frequency")
        return frequency.replace(Regex("\\s+"), "").replace(Regex(" cc| gm| 개| 정"), " 번")
    }

    private fun cleanDuration(duration: String): String {
        Log.d("CLEAN_FUNCTION", "총 투약일수 정제 중: $duration")
        return duration.replace(Regex("\\D"), "").ifEmpty { "1" }
    }



    private fun getNextCellValue(cells: JSONArray, rowIndex: Int, columnIndex: Int): String {
        for (j in 0 until cells.length()) {
            val nextCell = cells.getJSONObject(j)
            if (nextCell.getInt("rowIndex") == rowIndex && nextCell.getInt("columnIndex") == columnIndex) {
                val cellTextLines = nextCell.getJSONArray("cellTextLines")
                val stringBuilder = StringBuilder()

                for (k in 0 until cellTextLines.length()) {
                    val line = cellTextLines.getJSONObject(k)
                    val cellWords = line.getJSONArray("cellWords")

                    for (l in 0 until cellWords.length()) {
                        val word = cellWords.getJSONObject(l)
                        stringBuilder.append(word.getString("inferText")).append(" ")
                    }
                }

                val cellValue = stringBuilder.toString().trim()
                Log.d("OCR_PROCESSING", "다음 셀 값 추출: $cellValue")
                return cellValue  // 공백 제거
            }
        }
        Log.d("OCR_PROCESSING", "다음 셀 값이 비어있음")
        return ""
    }


    @Throws(IOException::class)
    private fun writeMultiPart(out: OutputStream, jsonMessage: String, byteArray: ByteArray, boundary: String) {
        val sb = StringBuilder()
        sb.append("--").append(boundary).append("\r\n")
        sb.append("Content-Disposition:form-data; name=\"message\"\r\n\r\n")
        sb.append(jsonMessage)
        sb.append("\r\n")

        out.write(sb.toString().toByteArray(Charsets.UTF_8))
        out.flush()

        out.write(("--$boundary\r\n").toByteArray(Charsets.UTF_8))
        val fileString = StringBuilder()
        fileString.append("Content-Disposition:form-data; name=\"file\"; filename=\"ocr_img.jpg\"\r\n")
        fileString.append("Content-Type: application/octet-stream\r\n\r\n")
        out.write(fileString.toString().toByteArray(Charsets.UTF_8))
        out.flush()

        out.write(byteArray)
        out.write("\r\n".toByteArray(Charsets.UTF_8))

        out.write(("--$boundary--\r\n").toByteArray(Charsets.UTF_8))
        out.flush()
    }

}

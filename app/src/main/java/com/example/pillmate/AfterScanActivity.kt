package com.example.pillmate

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.pillmate.databinding.ActivityAfterPreBinding
import com.example.pillmate.databinding.ActivityAfterScanBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AfterScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAfterScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAfterScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Glide.with(this).load(R.raw.loading).override(560, 560).into(binding.scanImgImage)


        // Intent로부터 이미지 파일 경로를 받음
        val photoPath = intent.getStringExtra("photoPath")
        var bitmap: Bitmap? = null

        if (photoPath != null) {
            // BitmapFactory를 사용하여 이미지 파일을 Bitmap으로 로드
            bitmap = BitmapFactory.decodeFile(photoPath)
            // ImageView에 비트맵 설정
            binding.imgMediCamera.setImageBitmap(bitmap)
        }

        // 나머지 버튼 클릭 리스너 설정
        binding.btnX.setOnClickListener {
            this@AfterScanActivity.finish()
        }

        binding.btnAgain.setOnClickListener {
            this@AfterScanActivity.finish()
        }

        binding.btnNext.setOnClickListener {
            //OCR 실행을 여기서 바로 할건지 아니면,
            if(photoPath != null){
                // BitmapFactory를 사용하여 이미지 파일을 Bitmap으로 로드
                val bitmap = BitmapFactory.decodeFile(photoPath)

                mediScanWithBitmap(bitmap)
                binding.loadingLayout.visibility = View.VISIBLE
            }

            //performOcrWithLocalImage()
            //performOcrWithBitmap(bitmap)
            binding.loadingLayout.visibility = View.VISIBLE
        }

        Glide.with(this).load(R.raw.loading).into(binding.scanImgImage)

//        binding.btnNext.setOnClickListener {
//
//
//            if (photoPath != null) {
//                // BitmapFactory를 사용하여 이미지 파일을 Bitmap으로 로드
//                val bitmap = BitmapFactory.decodeFile(photoPath)
//
//                if (bitmap != null) {
//                    mediScanWithBitmap(bitmap)
//                    binding.loadingLayout.visibility = View.VISIBLE
//                    this@AfterScanActivity.finish()
//                } else {
//                    Log.e("AfterScanActivity", "Bitmap이 null입니다.")
//                }
//            } else {
//                Log.e("AfterScanActivity", "photoPath가 null입니다.")
//            }
//        }
    }

    private fun createMultipartBodyFromBitmap(bitmap: Bitmap, fileName: String): MultipartBody.Part {
        val file = convertBitmapToFile(bitmap, fileName)
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    private fun convertBitmapToFile(bitmap: Bitmap, fileName: String): File {
        val file = File(cacheDir, fileName)
        file.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapData = bos.toByteArray()

        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()

        return file
    }

    private fun mediScanWithBitmap(bitmap: Bitmap) {
        Log.d("mediScanWithBitmap", "Bitmap 준비 완료, 파일 생성 시작")

        val filePart = createMultipartBodyFromBitmap(bitmap, "temp_image.jpg")
        Log.d("mediScanWithBitmap", "MultipartBody 생성 완료")

        val service = RetrofitApi.getMediRetrofitService
        val call = service.postScanMedi(filePart)

        Log.d("mediScanWithBitmap", "API 호출 시작")

        call.enqueue(object : Callback<List<MediScanResponse>> {
            override fun onResponse(
                call: Call<List<MediScanResponse>>,
                response: Response<List<MediScanResponse>>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null && responseData.isNotEmpty()) {
                        // 데이터를 ArrayList로 묶어서 전달
                        val dataList = arrayListOf(
                            responseData[0].category,
                            responseData[0].name,
                            responseData[0].photo
                        )
                        Log.d("ScanMedi", "알약 인식 성공: $dataList")
                        val intent =
                            Intent(this@AfterScanActivity, ScanFinActivity::class.java).apply {
                                putStringArrayListExtra("dataList", dataList)
                                Log.d("dataList", "dataList: $dataList")
                            }
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("After Scan API Error", "응답 실패: 데이터가 비어 있음")
                        navigateToFailActivity("알약 인식 실패: 데이터가 비어 있음")
                    }
                } else {
                    Log.e("After Scan API Error", "응답 실패: 코드 ${response.code()}")
                    navigateToFailActivity("알약 인식 실패: 응답 코드 ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MediScanResponse>>, t: Throwable) {
                Log.e("API Error", "네트워크 오류: ${t.message}")
                navigateToFailActivity("네트워크 오류: ${t.message}")
            }
        })
    }


    private fun navigateToFailActivity(reason: String) {
        Log.e("FailActivity", "이동 사유: $reason")
        val intent = Intent(this@AfterScanActivity, FailActivity::class.java).apply {

        }
        startActivity(intent)
        overridePendingTransition(0, 0) // 애니메이션 없음
        finish()
    }

}

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

        // Intent로부터 이미지 파일 경로를 받음
        val photoPath = intent.getStringExtra("photoPath")
        if (photoPath != null) {
            // BitmapFactory를 사용하여 이미지 파일을 Bitmap으로 로드
            val bitmap = BitmapFactory.decodeFile(photoPath)

            // ImageView에 비트맵 설정
            binding.imgMediCamera.setImageBitmap(bitmap)
        }

        // img_new_test_medi 이미지를 API에 전송
        mediScanWithLocalImage()

        // 나머지 버튼 클릭 리스너 설정
        binding.btnX.setOnClickListener {
            binding.loadingLayout.visibility = View.GONE
            this@AfterScanActivity.finish()
        }

        binding.btnAgain.setOnClickListener {
            binding.loadingLayout.visibility = View.GONE
            this@AfterScanActivity.finish()
        }

        binding.btnNext.setOnClickListener {
            binding.loadingLayout.visibility = View.VISIBLE
            this@AfterScanActivity.finish()
        }
    }

    private fun getBitmapFromLocalFile(): Bitmap {
        val drawableId = R.drawable.img_new_test_medi
        return BitmapFactory.decodeResource(resources, drawableId)
    }

    private fun mediScanWithLocalImage() {
        val bitmap = getBitmapFromLocalFile()
        mediScanWithBitmap(bitmap)
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
        val filePart = createMultipartBodyFromBitmap(bitmap, "temp_image.jpg")
        val service = RetrofitApi.getMediRetrofitService

        val call = service.postScanMedi(filePart)
        call.enqueue(object : Callback<MediScanResponse> {
            override fun onResponse(call: Call<MediScanResponse>, response: Response<MediScanResponse>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null) {
                        // 데이터를 ArrayList로 묶어서 전달
                        val dataList = arrayListOf(responseData.category, responseData.name, responseData.photo)
                        val intent = Intent(this@AfterScanActivity, ScanFinActivity::class.java).apply {
                            putStringArrayListExtra("dataList", dataList)
                            Log.d("dataList","dataList: ${dataList}")
                        }
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.e("After Scan API Error", "응답 실패")
                }
            }

            override fun onFailure(call: Call<MediScanResponse>, t: Throwable) {
                Log.e("API Error", "에러: ${t.message}")
            }
        })
    }
}

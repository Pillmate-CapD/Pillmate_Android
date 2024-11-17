package com.example.pillmate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.TypedValue
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pillmate.databinding.ActivityCameraBinding
import com.example.pillmate.databinding.ActivityMediScanBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date

class EatMediScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediScanBinding
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var cameraCaptureSessions: CameraCaptureSession
    private lateinit var backgroundHandler: Handler
    private lateinit var backgroundThread: HandlerThread
    private var pillName: String = "Unknown"


    private var isFlashOn = false

    private val textureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.titleTxt.text = "먹을 약 촬영"
        // Intent로부터 pillName을 받음
        pillName = intent.getStringExtra("pill_name") ?: "Unknown"

        binding.cameraPreview.surfaceTextureListener = textureListener

        binding.btnCapture.setOnClickListener {
            takePicture()
        }

        binding.btnFlash.setOnClickListener {
            toggleFlash()
        }

        binding.btnBack.setOnClickListener{
            this@EatMediScanActivity.finish()
        }
    }

    private fun openCamera() {
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = manager.cameraIdList[0]
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val imageDimension = map?.getOutputSizes(SurfaceTexture::class.java)?.get(0)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
                return
            }
            manager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice.close()
            this@EatMediScanActivity.finish()
        }
    }

    private fun createCameraPreview() {
        try {
            val texture = binding.cameraPreview.surfaceTexture!!
            texture.setDefaultBufferSize(binding.cameraPreview.width, binding.cameraPreview.height)
            val surface = Surface(texture)
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            cameraDevice.createCaptureSession(Collections.singletonList(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    if (cameraDevice == null) return
                    cameraCaptureSessions = session
                    updatePreview()
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Toast.makeText(this@EatMediScanActivity, "Configuration change", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun updatePreview() {
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        if (isFlashOn) {
            captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH)
        } else {
            captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF)
        }
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun takePicture() {
        if (cameraDevice == null) return

        val bitmap = binding.cameraPreview.bitmap

        if (bitmap != null) {
            // 300dp를 픽셀 단위로 변환
//            val intent = Intent(this, RecognizeIngActivity::class.java)
//            startActivity(intent)
            val sizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 340f, resources.displayMetrics).toInt()

            // 미리보기의 중심을 계산
            val centerX = bitmap.width / 2
            val centerY = bitmap.height / 2

            // 크롭 영역의 좌표를 계산
            val left = centerX - sizeInPixels / 2
            val top = centerY - sizeInPixels / 2
            val right = centerX + sizeInPixels / 2
            val bottom = centerY + sizeInPixels / 2

            // 크롭된 비트맵을 생성
            val croppedBitmap = Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)

            val file = createImageFile()
            saveBitmap(croppedBitmap, file)

            runOnUiThread {
                // 파일 경로를 AfterPreActivity로 전달
                val intent = Intent(this, AfterEatMediScanActivity::class.java)
                intent.putExtra("photoPath", file?.absolutePath)
                intent.putExtra("pill_name", pillName)
                startActivity(intent)  // AfterPreActivity 실행
                this@EatMediScanActivity.finish()
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap, file: File?) {
        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                output?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            )
        } catch (ex: IOException) {
            null
        }
    }

    private fun toggleFlash() {
        isFlashOn = !isFlashOn
        updatePreview()
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("Camera Background")
        backgroundThread.start()
        backgroundHandler = Handler(backgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread.quitSafely()
        try {
            backgroundThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (binding.cameraPreview.isAvailable) {
            openCamera()
        } else {
            binding.cameraPreview.surfaceTextureListener = textureListener
        }
    }

    override fun onPause() {
        super.onPause()
        stopBackgroundThread()
        if (::cameraDevice.isInitialized) {
            cameraDevice.close()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 200
    }
}
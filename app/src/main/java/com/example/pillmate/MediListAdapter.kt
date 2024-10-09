package com.example.pillmate

import AllFragment
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.MenuInflater
import android.widget.PopupMenu
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Layout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MediListAdapter(private var medicines: List<MediListResponse>, private val context: Context, private val editMediActivityLauncher: ActivityResultLauncher<Intent>,private val fragment: AllFragment) : RecyclerView.Adapter<MediListAdapter.MediViewHolder>() {

    companion object {
        const val EDIT_REQUEST_CODE = 1001 // 적절한 값으로 상수 선언
    }

    inner class MediViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.findViewById(R.id.image_view)
        val title: TextView = itemView.findViewById(R.id.medication_title)
        val info: TextView = itemView.findViewById(R.id.medication_info)
        val time: TextView = itemView.findViewById(R.id.medication_time)
        val realTime: TextView = itemView.findViewById(R.id.medication_realTime)
        val zoom: View = itemView.findViewById(R.id.btn_img_zoom)
        val option: View = itemView.findViewById(R.id.btn_modi) // 옵션 버튼
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pill_list, parent, false)
        return MediViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediViewHolder, position: Int) {
        val medi = medicines[position]

        // Title 설정
        holder.title.text = medi.name

        // Info 포맷팅 및 설정
        val formattedInfo = "${medi.amount}정 | 매일 ${medi.timesPerDay}회 | ${medi.day}일"
        holder.info.text = formattedInfo

        // Time 설정 (첫 번째 timeSlot 사용)
        if (medi.timeSlotList.isNotEmpty()) {
            val timeSlot = medi.timeSlotList[0] // 첫 번째 timeSlot 사용
            val formattedTime = "${timeSlot.spinnerTime}"
            val formattedRealTime = "${timeSlot.pickerTime}"

            holder.realTime.text = formatTime(formattedRealTime)
            holder.time.text = formattedTime
        } else {
            holder.time.text = "시간대 없음"
            holder.realTime.text = "시간대 없음"
        }

        // 이미지 로딩 (Glide 사용)
        Glide.with(holder.itemView.context)
            .load(medi.picture)
            .placeholder(R.drawable.bg_zoom_null) // 기본 이미지
            .into(holder.picture)

        // 줌 버튼 클릭 리스너 설정
        holder.zoom.setOnClickListener {
            showImageDialog(medi.picture, medi.name)
        }

        // 옵션 버튼 클릭 시 팝업 윈도우 생성
        holder.option.setOnClickListener {
            showCustomPopup(holder.option, medi, medi.id)
        }

        holder.picture.setOnClickListener{
            showImageDialog(medi.picture, medi.name)
        }
    }

    override fun getItemCount(): Int {
        return medicines.size
    }

    private fun showImageDialog(imageUrl: String, mediName: String) {
        // Dialog 생성
        val dialog = Dialog(context, R.style.CustomDialogTheme) // 전체 화면 테마 적용
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.dia_pic_zoom)
        val name = dialogView.findViewById<TextView>(R.id.dia_medi_name)
        Log.d("ImageDialog", "Image URL: $imageUrl")

        // Glide를 사용하여 이미지 설정
        Glide.with(context)
            .load(imageUrl)
            .into(imageView)

        // 약 이름 설정
        name.text = mediName

        dialog.setContentView(dialogView)

        // 다이얼로그 윈도우 속성 설정
        dialog.window?.apply {
            setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT) // 전체 화면
            setBackgroundDrawableResource(android.R.color.transparent) // 투명 배경 설정
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) // 배경 흐리게 설정
            setDimAmount(0.3f) // 배경 흐림 정도 설정
        }

        dialog.show()
    }

    // 커스텀 팝업 윈도우 생성
    private fun showCustomPopup(anchorView: View, medi: MediListResponse, id:Int) {
        // 팝업 레이아웃 인플레이트
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu, null)

        // PopupWindow 생성
        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true // 클릭 가능하게 설정
        )

        // 그림자가 보이도록 설정 (배경이 없을 경우 기본적으로 그림자가 안보임)
        popupWindow.elevation = 10f

        // 수정하기 클릭 이벤트 처리
        popupView.findViewById<ConstraintLayout>(R.id.btn_pop_edit).setOnClickListener {
            // 수정하기 처리
            val intent = Intent(context, EditMediActivity::class.java)
            intent.putExtra("medi_data", medi) // MediListResponse 객체 전체를 전달
            //context.startActivity(intent)

            editMediActivityLauncher.launch(intent)
            popupWindow.dismiss() // 팝업 닫기
        }

        // 삭제하기 클릭 이벤트 처리
        popupView.findViewById<ConstraintLayout>(R.id.btn_pop_del).setOnClickListener {
            // 삭제하기 처리
            popupWindow.dismiss() // 팝업 닫기
            showDelDialog(id)
        }

        // 팝업 위치 설정 및 보여주기
        popupWindow.showAsDropDown(anchorView, -380, 0, Gravity.START)
    }

    // 시간 포맷 변환 함수
    private fun formatTime(time: String): String {
        return try {
            // 원본 시간 파싱
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(time)

            // 오전/오후 포맷으로 변환
            val outputFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
            outputFormat.format(date ?: Date()) // date가 null일 경우 현재 시간을 반환
        } catch (e: Exception) {
            time // 변환 실패 시 원본 시간 반환
        }
    }

    // 데이터 업데이트 메서드 추가
    fun updateData(newMedicines: List<MediListResponse>) {
        this.medicines = newMedicines
        notifyDataSetChanged()
    }

    private fun showDelDialog(id: Int) {
        // Dialog 생성
        val dialog = Dialog(context, R.style.CustomDialogTheme)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null)

        dialog.setContentView(dialogView)

        // 다이얼로그 윈도우 속성 설정
        dialog.window?.apply {
            //decorView.setBackgroundColor(Color.parseColor("#66000000"))
            setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT) // 전체 화면
            setBackgroundDrawableResource(android.R.color.transparent) // 투명 배경 설정
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) // 배경 흐리게 설정
            setDimAmount(0.3f) // 배경 흐림 정도 설정
        }

        // '삭제' 버튼 클릭 이벤트 처리
        val btnDelete = dialogView.findViewById<TextView>(R.id.btn_dia_del)
        btnDelete.setOnClickListener {
            // 삭제 로직 추가 (예시: 해당 약 리스트를 삭제하는 코드 추가)
            deleteMedicine(id)

            // 다이얼로그 닫기
            dialog.dismiss()
        }

        // '취소' 버튼 클릭 이벤트 처리
        val btnCancel = dialogView.findViewById<TextView>(R.id.btn_dia_cancel)
        btnCancel.setOnClickListener {
            dialog.dismiss() // 다이얼로그 닫기
        }

        dialog.show()
    }

    private fun deleteMedicine(id: Int){
        val service = RetrofitApi.getRetrofitService
        val call = service.delMedicine(id)

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    Log.d("deleteMedicine", "delete, 약물 삭제 성공")
                    showPerfectToast("해당 약이 삭제됐어요.")

                    // 삭제 후 AllFragment에서 약 리스트 다시 불러오기
                    fragment.fetchMedicineList() // AllFragment의 메서드 호출
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
                showPerfectToast("API 에러")
                Log.d("deleteMedicine", "delete, 약물 삭제 실패")
            }

        })
    }

    // 커스텀 토스트 메시지를 띄우는 함수
    private fun showPerfectToast(message: String) {
        // 커스텀 토스트 레이아웃을 인플레이트
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_perfect_toast, null)

        // 메시지 설정
        val textView = layout.findViewById<TextView>(R.id.tv_toast)
        textView.text = message

        // 커스텀 토스트 생성 및 설정
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM, 0, 80) // 화면 하단에 표시
        toast.show()
    }
}

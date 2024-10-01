package com.example.pillmate

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Layout

class MediListAdapter(private var medicines: List<MediListResponse>, private val context: Context) : RecyclerView.Adapter<MediListAdapter.MediViewHolder>() {

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
            .placeholder(R.drawable.bg_null) // 기본 이미지
            .into(holder.picture)

        // 줌 버튼 클릭 리스너 설정
        holder.zoom.setOnClickListener {
            showImageDialog(medi.picture, medi.name)
        }

        // 옵션 버튼 클릭 시 팝업 윈도우 생성
        holder.option.setOnClickListener {
            showCustomPopup(holder.option, medi)
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
    private fun showCustomPopup(anchorView: View, medi: MediListResponse) {
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
            context.startActivity(intent)
            popupWindow.dismiss() // 팝업 닫기
        }

        // 삭제하기 클릭 이벤트 처리
        popupView.findViewById<ConstraintLayout>(R.id.btn_pop_del).setOnClickListener {
            // 삭제하기 처리
            popupWindow.dismiss() // 팝업 닫기
            showDelDialog()
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

    private fun showDelDialog() {
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
            // 예: deleteMedicine(medicineId)

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
}

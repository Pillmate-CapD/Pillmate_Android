package com.example.pillmate

import AllFragment
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.example.capdi_eat_test.MyPillListFragment
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediListAdapter(
    private var medicines: List<MediListResponse>,
    private val context: Context,
    private val fragment: AllFragment,
    private val viewModel: SharedViewModel // SharedViewModel 주입
) : RecyclerView.Adapter<MediListAdapter.MediViewHolder>() {

    companion object {
        const val EDIT_REQUEST_CODE = 1001
    }

    inner class MediViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.findViewById(R.id.image_view)
        val title: TextView = itemView.findViewById(R.id.medication_title)
        val amount: TextView = itemView.findViewById(R.id.medication_amount)
        val frequency: TextView = itemView.findViewById(R.id.medication_frequency)
        val duration: TextView = itemView.findViewById(R.id.medication_duration)
        //val info: TextView = itemView.findViewById(R.id.medication_info)
        val time: TextView = itemView.findViewById(R.id.medication_time)
        val realTime: TextView = itemView.findViewById(R.id.medication_realTime)
        val zoom: View = itemView.findViewById(R.id.btn_img_zoom)
        val option: View = itemView.findViewById(R.id.btn_modi)
        val category: TextView = itemView.findViewById(R.id.tv_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pill_list, parent, false)
        return MediViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediViewHolder, position: Int) {
        val medi = medicines[position]

        holder.title.text = medi.name
//        val formattedInfo = "${medi.amount}정 | 매일 ${medi.timesPerDay}회 | ${medi.day}일"
//        holder.info.text = formattedInfo
        holder.amount.text = "${medi.amount}정"
        holder.frequency.text = "매일 ${medi.timesPerDay}회"
        holder.duration.text = "${medi.day}일"

        if (medi.timeSlotList.isNotEmpty()) {
            val timeSlot = medi.timeSlotList[0]
            val formattedTime = "${timeSlot.spinnerTime}"
            val formattedRealTime = "${timeSlot.pickerTime}"

            holder.realTime.text = formatTime(formattedRealTime)
            holder.time.text = "${formattedTime} /"
        } else {
            holder.time.text = "시간대 없음"
            holder.realTime.text = "시간대 없음"
        }

        // 카테고리에 따라 텍스트와 배경색 설정
        holder.category.text = medi.category // 예: "심혈관질환"
        val background = holder.category.background as GradientDrawable
        when (medi.category) {
            "심혈관질환" -> {
                background.setColor(Color.parseColor("#40FFCEDF"))
                holder.category.setTextColor(Color.parseColor("#FD5592"))
            }
            "고혈압" -> {
                background.setColor(Color.parseColor("#E6EBFA"))
                holder.category.setTextColor(Color.parseColor("#1E54DF"))
            }
            "당뇨" -> {
                background.setColor(Color.parseColor("#D6F0EF"))
                holder.category.setTextColor(Color.parseColor("#0CBBB2"))
            }
            "고지혈증" -> {
                background.setColor(Color.parseColor("#B0FFBCB8"))
                holder.category.setTextColor(Color.parseColor("#FF453A"))
            }
            "호흡기질환" -> {
                background.setColor(Color.parseColor("#5CEDF2A3"))
                holder.category.setTextColor(Color.parseColor("#E2B100"))
            }
            "기타" -> {
                background.setColor(Color.parseColor("#F0DDF7"))
                holder.category.setTextColor(Color.parseColor("#951FC0"))
            }
            else -> {
                background.setColor(Color.parseColor("#F0DDF7"))
                holder.category.setTextColor(Color.parseColor("#951FC0"))
            }
        }

        // 이미지 로딩 (Glide 사용)
        Glide.with(holder.itemView.context)
            .load(medi.picture)
            .placeholder(R.drawable.bg_zoom_null)
            .into(holder.picture)

        // 줌 버튼 클릭 리스너 설정
        holder.zoom.setOnClickListener {
            showImageDialog(medi.picture, medi.name)
        }

        // 옵션 버튼 클릭 시 팝업 윈도우 생성
        holder.option.setOnClickListener {
            showCustomPopup(holder.option, medi, medi.id)
        }

        holder.picture.setOnClickListener {
            showImageDialog(medi.picture, medi.name)
        }
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

    override fun getItemCount(): Int {
        return medicines.size
    }

    // updateData 메서드 추가
    fun updateData(newMedicines: List<MediListResponse>) {
        this.medicines = newMedicines
        notifyDataSetChanged() // RecyclerView 갱신
    }

    private fun showImageDialog(imageUrl: String, mediName: String) {
        val dialog = Dialog(context, R.style.CustomDialogTheme)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.dia_pic_zoom)
        val name = dialogView.findViewById<TextView>(R.id.dia_medi_name)

        Glide.with(context).load(imageUrl).into(imageView)
        name.text = mediName

        dialog.setContentView(dialogView)
        dialog.window?.apply {
            setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(android.R.color.transparent)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.3f)
        }

        dialog.show()
    }

    private fun showCustomPopup(anchorView: View, medi: MediListResponse, id: Int) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu, null)
        val popupWindow = PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true)

        popupWindow.elevation = 10f

        popupView.findViewById<ConstraintLayout>(R.id.btn_pop_edit).setOnClickListener {
            val intent = Intent(context, EditMediActivity::class.java)
            intent.putExtra("medi_data", medi)
            context.startActivity(intent)
            popupWindow.dismiss()
        }

        popupView.findViewById<ConstraintLayout>(R.id.btn_pop_del).setOnClickListener {
            popupWindow.dismiss()
            showDelDialog(id)
        }

        popupWindow.showAsDropDown(anchorView, -380, 0, Gravity.START)
    }

    private fun showDelDialog(id: Int) {
        val dialog = Dialog(context, R.style.CustomDialogTheme)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null)
        dialog.setContentView(dialogView)

        dialog.window?.apply {
            setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(android.R.color.transparent)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.3f)
        }

        val btnDelete = dialogView.findViewById<TextView>(R.id.btn_dia_del)
        btnDelete.setOnClickListener {
            deleteMedicine(id)
            dialog.dismiss()
        }

        val btnCancel = dialogView.findViewById<TextView>(R.id.btn_dia_cancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteMedicine(id: Int) {
        val service = RetrofitApi.getRetrofitService
        val call = service.delMedicine(id)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("deleteMedicine", "delete, 약물 삭제 완료")
                    //showPerfectToast("해당 약이 삭제됐어요.")

                    // 약물 삭제 후 ViewModel을 통해 데이터 갱신
                    viewModel.deleteMedicine(id)

                    // AllFragment에서 약 리스트 다시 불러오기
                    fragment.fetchMedicineList()

                    // context를 사용하여 Intent 생성
                    val intent = Intent(context, AddMediFinActivity::class.java)
                    triggerPillAlarmWorker(context)
                    intent.putExtra("editMessage", "약 삭제 완료")
                    context.startActivity(intent)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                showPerfectToast("API 에러")
                Log.d("deleteMedicine", "delete, 약물 삭제 실패")
            }
        })
    }
    // PillAlarmWorker를 트리거하는 함수
    private fun triggerPillAlarmWorker(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<PillAlarmWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "PillAlarmWorker",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun showPerfectToast(message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_perfect_toast, null)
        val textView = layout.findViewById<TextView>(R.id.tv_toast)
        textView.text = message

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM, 0, 80)
        toast.show()
    }
}

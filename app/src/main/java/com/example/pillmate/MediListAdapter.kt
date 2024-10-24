package com.example.pillmate

import AllFragment
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
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
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capdi_eat_test.MyPillListFragment
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
        val info: TextView = itemView.findViewById(R.id.medication_info)
        val time: TextView = itemView.findViewById(R.id.medication_time)
        val realTime: TextView = itemView.findViewById(R.id.medication_realTime)
        val zoom: View = itemView.findViewById(R.id.btn_img_zoom)
        val option: View = itemView.findViewById(R.id.btn_modi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pill_list, parent, false)
        return MediViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediViewHolder, position: Int) {
        val medi = medicines[position]

        holder.title.text = medi.name
        val formattedInfo = "${medi.amount}정 | 매일 ${medi.timesPerDay}회 | ${medi.day}일"
        holder.info.text = formattedInfo

        if (medi.timeSlotList.isNotEmpty()) {
            val timeSlot = medi.timeSlotList[0]
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
                    Log.d("deleteMedicine", "delete, 약물 삭제 성공")
                    //showPerfectToast("해당 약이 삭제됐어요.")

                    // 약물 삭제 후 ViewModel을 통해 데이터 갱신
                    viewModel.deleteMedicine(id)

                    // AllFragment에서 약 리스트 다시 불러오기
                    fragment.fetchMedicineList()

                    // context를 사용하여 Intent 생성
                    val intent = Intent(context, AddMediFinActivity::class.java)
                    intent.putExtra("editMessage", "약 삭제가 완료되었어요!")
                    context.startActivity(intent)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                showPerfectToast("API 에러")
                Log.d("deleteMedicine", "delete, 약물 삭제 실패")
            }
        })
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

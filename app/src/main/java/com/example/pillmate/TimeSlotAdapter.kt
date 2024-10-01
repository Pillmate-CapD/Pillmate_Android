import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.R
import com.example.pillmate.TimeSlotItem

class TimeSlotAdapter(
    private val timeSlots: MutableList<TimeSlotItem>,
    private val onSpinnerClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    inner class TimeSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val spinnerTime: TextView = itemView.findViewById(R.id.spinner_time)
        val pickerTime: TextView = itemView.findViewById(R.id.picker_time)
        val deleteButton: View = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.time_slot, parent, false)
        return TimeSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val timeSlotItem = timeSlots[position]

        // 스피너와 텍스트에 시간대 데이터 설정
        holder.spinnerTime.text = timeSlotItem.timeLabel
        holder.pickerTime.text = timeSlotItem.time

        holder.spinnerTime.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onSpinnerClickListener(adapterPosition) // 클릭 시 BottomSheet를 여는 함수 호출
            }
        }

        // pickerTime 클릭 시 커스텀 다이얼로그 호출
        holder.pickerTime.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                showTimePickerDialog(holder.pickerTime.context, adapterPosition)
            }
        }

        // spinnerTime의 색상 설정 (선택 여부에 따라)
        if (timeSlotItem.isTimeSelected) {
            holder.spinnerTime.setTextColor(Color.BLACK)
            holder.spinnerTime.isSelected= true

        } else {
            holder.spinnerTime.setTextColor(Color.GRAY)
        }

        // pickerTime의 색상 설정 (시간 변경 여부에 따라)
        if (timeSlotItem.isTimeChanged) {
            holder.pickerTime.setTextColor(Color.BLACK)
        } else {
            holder.pickerTime.setTextColor(Color.GRAY)
        }

        // 삭제 버튼 클릭 시 해당 시간대 제거
        holder.deleteButton.setOnClickListener { view ->
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                removeTimeSlot(adapterPosition, view)
            }
        }
    }

    fun getTimeSlots(): List<TimeSlotItem> {
        return timeSlots
    }

    override fun getItemCount(): Int {
        return timeSlots.size
    }

    // 시간대 추가 함수
    fun addTimeSlot(item: TimeSlotItem) {
        timeSlots.add(item)
        notifyItemInserted(timeSlots.size - 1)
    }

    // 시간대 제거 함수
    fun removeTimeSlot(position: Int, view: View) {
        if (timeSlots.size > 1) {
            val removedItem = timeSlots.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, timeSlots.size)
        } else {
            Toast.makeText(view.context, "최소 한 개의 시간대가 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 시간대 업데이트 함수
    fun updateTimeSlot(position: Int, newTimeLabel: String) {
        val item = timeSlots[position]
        item.timeLabel = newTimeLabel
        item.isTimeSelected = true // spinnerTime이 선택되었음을 설정
        notifyItemChanged(position)
    }

    // 커스텀 타임피커 다이얼로그를 표시하는 함수
    private fun showTimePickerDialog(context: Context, position: Int) {
        // Dialog 생성
        val dialog = Dialog(context, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.time_picker2) // 커스텀 레이아웃 설정

        // TimePicker 참조 및 설정
        val timePicker = dialog.findViewById<TimePicker>(R.id.am_timePicker)
        timePicker.setIs24HourView(false) // 12시간 형식으로 설정

        // currentTime에서 "오전"/"오후"와 시간을 분리
        val currentTime = timeSlots[position].time
        val amPm = currentTime.split(" ")[0] // "오전" 또는 "오후"
        val time = currentTime.split(" ")[1] // "06:00"
        val hour = time.split(":")[0].toInt()
        val minute = time.split(":")[1].toInt()

        // 오전/오후에 따라 시간을 24시간 형식으로 변환
        val hourIn24Format = if (amPm == "오후" && hour < 12) {
            hour + 12
        } else if (amPm == "오전" && hour == 12) {
            0
        } else {
            hour
        }

        // TimePicker에 시간 설정
        timePicker.hour = hourIn24Format
        timePicker.minute = minute

        // 확인 및 취소 버튼 설정
        val btnCancel = dialog.findViewById<TextView>(R.id.btn_cancel)
        val btnConfirm = dialog.findViewById<TextView>(R.id.btn_confirm)

        // 취소 버튼 클릭 리스너 설정
        btnCancel.setOnClickListener {
            dialog.dismiss() // 다이얼로그 닫기
        }

        // 확인 버튼 클릭 리스너 설정
        btnConfirm.setOnClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute

            // 선택한 시간을 오전/오후 형식으로 변환
            val amPm = if (selectedHour >= 12) "오후" else "오전"
            val formattedHour = if (selectedHour > 12) selectedHour - 12 else if (selectedHour == 0) 12 else selectedHour

            // 새로운 시간 포맷
            val newTime = String.format("%s %02d:%02d", amPm, formattedHour, selectedMinute)

            // 시간 정보 업데이트
            timeSlots[position].time = newTime
            timeSlots[position].isTimeChanged = true // 시간 변경 여부 설정
            notifyItemChanged(position)
            dialog.dismiss() // 다이얼로그 닫기
        }

        // 다이얼로그 스타일 및 크기 설정
        dialog.show() // 다이얼로그 표시
    }


}

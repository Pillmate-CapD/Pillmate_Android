import android.app.AlertDialog
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
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.time_picker, null)
        builder.setView(dialogView)

        // TimePicker 참조 및 설정
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true) // 24시간 형식 사용
        val currentTime = timeSlots[position].time
        val hour = currentTime.split(":")[0].toInt()
        val minute = currentTime.split(":")[1].toInt()
        timePicker.hour = hour
        timePicker.minute = minute

        // 확인 및 취소 버튼 설정
        val btnCancel = dialogView.findViewById<TextView>(R.id.btn_cancel)
        val btnConfirm = dialogView.findViewById<TextView>(R.id.btn_confirm)

        val dialog = builder.create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute
            val newTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            timeSlots[position].time = newTime
            timeSlots[position].isTimeChanged = true // 시간 변경 여부 설정
            notifyItemChanged(position)
            dialog.dismiss()
        }

        dialog.show()
    }
}

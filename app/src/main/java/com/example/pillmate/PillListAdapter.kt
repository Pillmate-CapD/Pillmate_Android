package com.example.pillmate

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


class PillListAdapter(
    private val pillListItem: ArrayList<PillListItem>,
    private val fragment: HomeFragment,
    private val preferencesHelper: PreferencesHelper,
    private val userId: Int // 추가된 userId
) : RecyclerView.Adapter<PillListAdapter.PillListViewHolder>() {

    private val REQUEST_CODE_EAT_MEDI = 1001
    private val REQUEST_CODE_SCHEDULE_EXACT_ALARM = 1002

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pill_list, parent, false)

        return PillListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pillListItem.size
    }

    override fun onBindViewHolder(holder: PillListViewHolder, position: Int) {
        val item = pillListItem[position]
        holder.bind(item, position)
    }

    inner class PillListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val time: TextView = itemView.findViewById(R.id.pill_list_time)
        private val pillName: TextView = itemView.findViewById(R.id.pill_list_name)
        private val pillImg: View = itemView.findViewById(R.id.img_pill)

        private val pill_done: View = itemView.findViewById(R.id.bt_eat_done)
        private val pill_now: View = itemView.findViewById(R.id.bt_eat_now)
        private val pill_before: View = itemView.findViewById(R.id.bt_eat_will)

        //private val pill_switch: SwitchCompat = itemView.findViewById(R.id.switch_alarm)

        fun bind(item: PillListItem, position: Int) {
            // 시간 및 약물 이름 설정
            time.text = item.time
            pillName.text = item.name

            //pill_switch.switchMinWidth = 51

            // 현재 시간 설정
            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)

            // 시간 추출
            val itemHour = item.extractHour()
            val itemMinute = item.extractMinute()

            // 현재 시간과 항목의 시간을 비교하여 배경 색상 결정
            val itemTimeInMinutes = itemHour * 60 + itemMinute
            val currentTimeInMinutes = currentHour * 60 + currentMinute

            //setAlarm(20,"트윈스타정")

//            // 2시간 이상 지났을 때 알람 발생
//            if (currentTimeInMinutes >= itemTimeInMinutes + 120 && !item.isEaten) {
//                saveMissedPillAlarm(item.name)
//            }

            if (item.isEaten) {
                // 아이템이 완료된 경우
                itemView.setBackgroundResource(R.drawable.custom_pill_background) // 기본 색상
                //pillImg.setBackgroundResource(R.drawable.img_pill_none) // 약 이미지 복용전과 복용 예정인 경우의 상태
                pill_done.visibility = View.VISIBLE
                pill_now.visibility = View.INVISIBLE

                pill_before.visibility = View.INVISIBLE
                itemView.setOnClickListener(null) // 클릭 비활성화

                // 텍스트 색상 직접 지정 (HEX 값을 사용하여 파란색으로 설정)
                time.setTextColor(Color.parseColor("#494949"))
                pillName.setTextColor(Color.parseColor("#494949"))
            }
//            else if(currentTimeInMinutes >= itemTimeInMinutes + 60){
//                saveMissedPillAlarm(item.name)
//
//                // 현재 시간이 항목의 시간보다 2시간이상 지났거나 먹어야하는데 아직 안먹은 경우
//                itemView.setBackgroundResource(R.drawable.bg_pill_list_check2) // 배경 색상 변경
//                pill_done.visibility = View.INVISIBLE
//                pill_before.visibility = View.INVISIBLE
//                pill_now.visibility= View.VISIBLE
//
//                // 텍스트 색상 직접 지정 (HEX 값을 사용하여 파란색으로 설정)
//                time.setTextColor(Color.parseColor("#1E54DF"))
//                pillName.setTextColor(Color.parseColor("#1E54DF"))
//
//
//                itemView.setOnClickListener {
//                    // EatMediActivity로 이동
//                    val intent = Intent(fragment.requireContext(), EatMediActivity::class.java)
//                    intent.putExtra("pill_name", item.name)
//                    intent.putExtra("pill_time", item.time)
//                    intent.putExtra("position", position) // 클릭된 아이템의 position을 전달
//                    fragment.startActivityForResult(intent, REQUEST_CODE_EAT_MEDI)
//                }
//            }
            else if (currentTimeInMinutes >= itemTimeInMinutes + 120) {
                saveMissedPillAlarm(item.name)
                // 알람이 발생했을 때

                // 현재 시간이 항목의 시간보다 2시간이상 지났거나 먹어야하는데 아직 안먹은 경우
                itemView.setBackgroundResource(R.drawable.bg_pill_list_check2) // 배경 색상 변경
                pill_done.visibility = View.INVISIBLE
                pill_before.visibility = View.INVISIBLE
                pill_now.visibility = View.VISIBLE

                // 텍스트 색상 직접 지정 (HEX 값을 사용하여 파란색으로 설정)
                time.setTextColor(Color.parseColor("#1E54DF"))
                pillName.setTextColor(Color.parseColor("#1E54DF"))


                itemView.setOnClickListener {
                    // EatMediActivity로 이동
                    val intent = Intent(fragment.requireContext(), EatMediActivity::class.java)
                    intent.putExtra("pill_name", item.name)
                    intent.putExtra("pill_time", item.time)
                    intent.putExtra("pill_id",item.medicineId)
                    intent.putExtra("alarm_id",item.alarmId)
                    intent.putExtra("position", position) // 클릭된 아이템의 position을 전달
                    intent.putExtra("source", "list") //하늘 추가
                    fragment.startActivityForResult(intent, REQUEST_CODE_EAT_MEDI)
                }
            } else if (currentTimeInMinutes >= itemTimeInMinutes - 60) {
                // 현재 시간이 항목의 시간보다 한 시간 전이거나 같은 경우
                itemView.setBackgroundResource(R.drawable.bg_pill_list_check2) // 배경 색상 변경
                pill_done.visibility = View.INVISIBLE
                pill_before.visibility = View.INVISIBLE
                pill_now.visibility = View.VISIBLE

                // 텍스트 색상 직접 지정 (HEX 값을 사용하여 파란색으로 설정)
                time.setTextColor(Color.parseColor("#1E54DF"))
                pillName.setTextColor(Color.parseColor("#1E54DF"))

                itemView.setOnClickListener {
                    // EatMediActivity로 이동
                    val intent = Intent(fragment.requireContext(), EatMediActivity::class.java)
                    intent.putExtra("pill_name", item.name)
                    intent.putExtra("pill_time", item.time)
                    intent.putExtra("pill_id",item.medicineId)
                    intent.putExtra("alarm_id",item.alarmId)
                    intent.putExtra("position", position) // 클릭된 아이템의 position을 전달
                    fragment.startActivityForResult(intent, REQUEST_CODE_EAT_MEDI)
                }

            } else {
                // 그 외의 경우 기본 배경 색상 사용
                itemView.setBackgroundResource(R.drawable.custom_pill_background) // 기본 색상
                pillImg.setBackgroundResource(R.drawable.img_pill_none) // 약 이미지 복용전과 복용 예정인 경우의 상태

                pill_done.visibility = View.INVISIBLE
                pill_now.visibility = View.INVISIBLE
                pill_before.visibility = View.VISIBLE

                // 텍스트 색상 직접 지정 (HEX 값을 사용하여 파란색으로 설정)
                time.setTextColor(Color.parseColor("#898989"))
                pillName.setTextColor(Color.parseColor("#898989"))
            }
        }

        private fun saveMissedPillAlarm(pillName: String) {
            val context = itemView.context
            val alarmMessage = "'$pillName'을/를 복용할 시간입니다."
            val currentTimestamp = Date()

            // 현재 날짜로 날짜 형식 설정
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormatter.format(currentTimestamp)

            CoroutineScope(Dispatchers.IO).launch {
                val alarmDatabase = AlarmDatabase.getInstance(context)
                val existingLogs = alarmDatabase.alarmLogDao()
                    .getLogsForDate(alarmMessage, currentDate) // 같은 날 저장된 로그 확인

                if (existingLogs.isEmpty()) { // 같은 날짜에 중복된 로그가 없을 때만 저장
                    saveAlarmToDatabase(context, alarmMessage, currentTimestamp, userId)
                } else {
                    Log.d(
                        "AlarmDatabase",
                        "Duplicate entry for today, not inserting: $alarmMessage"
                    )
                }
            }
        }

        fun saveAlarmToDatabase(context: Context, message: String, timestamp: Date, userId: Int) {
            val alarmLog = AlarmLog(userId = userId, message = message, timestamp = timestamp)
            val alarmDatabase = AlarmDatabase.getInstance(context)

            CoroutineScope(Dispatchers.IO).launch {
                val existingLog = alarmDatabase.alarmLogDao().findLog(message, timestamp)
                if (existingLog == null) {
                    alarmDatabase.alarmLogDao().insertLog(alarmLog)
                    Log.d("AlarmDatabase", "Data inserted: $alarmLog")
                } else {
                    Log.d("AlarmDatabase", "Duplicate entry not inserted: $alarmLog")
                }
            }
        }

        private fun setAlarm(hour: Int, minute: Int, pillName: String) {
            val context = itemView.context
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("pill_name", pillName)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val canScheduleExactAlarms = alarmManager.canScheduleExactAlarms()
                if (!canScheduleExactAlarms) {
                    Log.w("PillListAdapter", "Requesting exact alarm permission.")
                    val intent = Intent().apply {
                        action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    }
                    fragment.startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM)
                    return
                }
            }

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                // Log the alarm time
                val formattedTime = "%02d:%02d:%02d".format(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)
                )
                Log.d("PillListAdapter", "Alarm set for: $formattedTime, Pill: $pillName")
            } catch (e: SecurityException) {
                Log.e("PillListAdapter", "SecurityException: ${e.message}")
                // Handle SecurityException gracefully, perhaps by requesting necessary permissions
            }
        }
    }

    fun updateItems(newItems: List<PillListItem>) {
        pillListItem.clear()
        pillListItem.addAll(newItems)
        notifyDataSetChanged()
    }
}
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class PillListAdapter(private val pillListItem: ArrayList<PillListItem>, private val fragment: Fragment) :
    RecyclerView.Adapter<PillListAdapter.PillListViewHolder>() {

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

        private val pill_right: View = itemView.findViewById(R.id.pill_right)
        private val pill_list_go: View = itemView.findViewById(R.id.pill_list_go)
        private val pill_done: View = itemView.findViewById(R.id.pill_done)
        private val pill_before: View = itemView.findViewById(R.id.pill_before)

        fun bind(item: PillListItem, position: Int) {
            // 시간 및 약물 이름 설정
            time.text = item.time
            pillName.text = item.name

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



            if (item.isCompleted) {
                // 아이템이 완료된 경우
                itemView.setBackgroundResource(R.drawable.custom_pill_background) // 기본 색상
                pill_done.visibility = View.VISIBLE
                pill_list_go.visibility = View.INVISIBLE
                pill_right.visibility = View.INVISIBLE
                pill_before.visibility = View.INVISIBLE
                itemView.setOnClickListener(null) // 클릭 비활성화
            } else if (currentTimeInMinutes >= itemTimeInMinutes - 60) {
                // 현재 시간이 항목의 시간보다 한 시간 전이거나 같은 경우
                itemView.setBackgroundResource(R.drawable.bg_pill_list_check) // 배경 색상 변경
                pill_done.visibility = View.INVISIBLE
                pill_before.visibility = View.INVISIBLE
                pill_list_go.visibility = View.VISIBLE
                pill_right.visibility = View.VISIBLE


                // 임시 설정 (30초 후에 알람 설정)
                //setAlarm(30, "트윈스타정")
                // 알람 설정
                setAlarm(itemHour, itemMinute, item.name)

                itemView.setOnClickListener {
                    // EatMediActivity로 이동
                    val intent = Intent(fragment.requireContext(), EatMediActivity::class.java)
                    intent.putExtra("pill_name", item.name)
                    intent.putExtra("pill_time", item.time)
                    intent.putExtra("position", position) // 클릭된 아이템의 position을 전달
                    fragment.startActivityForResult(intent, REQUEST_CODE_EAT_MEDI)
                }
            } else {
                // 그 외의 경우 기본 배경 색상 사용
                itemView.setBackgroundResource(R.drawable.custom_pill_background) // 기본 색상
                pill_done.visibility = View.INVISIBLE
                pill_list_go.visibility = View.INVISIBLE
                pill_right.visibility = View.INVISIBLE
                pill_before.visibility = View.VISIBLE
            }
        }

        private fun setAlarm(hour: Int, minute: Int, pillName: String) {
            val context = itemView.context
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("pill_name", pillName)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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

        private fun setAlarm(seconds: Int, pillName: String) {
            val context = itemView.context
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = AlarmReceiver.ACTION_RESTART_SERVICE // 추가된 부분
                putExtra("pill_name", pillName)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.SECOND, seconds)
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
}
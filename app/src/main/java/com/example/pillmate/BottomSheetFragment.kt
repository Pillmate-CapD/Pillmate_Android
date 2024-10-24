package com.example.pillmate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pillmate.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetBinding
    private var listener: BottomSheetListener? = null

    interface BottomSheetListener {
        fun onAlarmDismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? BottomSheetListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        binding.btnCancelAlarm.setOnClickListener {
            dismiss()
        }

        binding.btnFinAlarm.setOnClickListener {
            // 알람 서비스 중지
            val serviceIntent = Intent(context, AlarmService::class.java)
            context?.stopService(serviceIntent)

            // 알람 매니저에서 알람을 제거 (선택 사항)
            // val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            // val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            //     PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
            // }
            // alarmManager.cancel(alarmIntent)

            listener?.onAlarmDismiss()
            dismiss()
        }

        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }
}
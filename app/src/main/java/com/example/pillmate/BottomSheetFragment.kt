package com.example.pillmate

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pillmate.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
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
        //binding = FragmentBottomSheetBinding.inflate(inflater, container, false)


        binding.btnCancelAlarm.setOnClickListener {
            dismiss()
        }

        // 얘는 그냥 지금 당장 알람 소리만 끄는 것 같음
        binding.btnFinAlarm.setOnClickListener {

//            // 알람 서비스 중지
//            val serviceIntent = Intent(context, AlarmService::class.java)
//            context?.stopService(serviceIntent)
//
//            // 알람 매니저에서 알람을 제거 (선택 사항)
//            // val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            // val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
//            //     PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
//            // }
//            // alarmManager.cancel(alarmIntent)
//
//            listener?.onAlarmDismiss()
            dismiss()
        }

        binding.btnCheckAlarm.setOnClickListener{
            // TODO - 알람을 on/off 하는 기능 추가하기


        }

        return binding.root
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
//        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//
//        dialog.window?.apply {
//            setBackgroundDrawableResource(android.R.color.transparent)
//            setDimAmount(0.3f)  // 배경 흐림 정도 설정
//            decorView.setBackgroundColor(Color.parseColor("#66000000")) // 검정색 + 40% 투명도
//        }
//
//        return dialog
//    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }
}
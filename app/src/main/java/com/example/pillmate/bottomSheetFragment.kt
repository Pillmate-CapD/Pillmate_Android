package com.example.pillmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pillmate.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class bottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater,container, false)

        binding.btnCancelAlarm.setOnClickListener {
            dismiss()
        }

        // 알람 끄기 누르면 아예 해당 약 알람을 꺼버리도록
        binding.btnFinAlarm.setOnClickListener {

        }


        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

}
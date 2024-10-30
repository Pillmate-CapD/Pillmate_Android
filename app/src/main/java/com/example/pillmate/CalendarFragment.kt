package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pillmate.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        // editDiaryButton 클릭 시 HealthDiary1Activity로 이동
        binding.editDiaryButton.setOnClickListener {
            val intent = Intent(requireContext(), HealthDiary1Activity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}

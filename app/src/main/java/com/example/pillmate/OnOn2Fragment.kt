package com.example.pillmate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pillmate.databinding.ActivityOnon2Binding

class OnOn2Fragment : Fragment() {
    private var _binding: ActivityOnon2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityOnon2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "다음" 버튼 클릭 이벤트 (ViewPager2 페이지 넘기기)
        binding.btnNext.setOnClickListener {
            // ViewPager2로 페이지를 넘깁니다.
            val activity = requireActivity() as OnOnActivity
            activity.binding.viewPager2.currentItem = 2 // OnOn2Fragment로 넘어감
        }

        // "건너뛰기" 버튼 클릭 이벤트
        binding.btnSkip.setOnClickListener {
            val intent = Intent(requireContext(), StartActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.pillmate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import com.example.capdi_eat_test.MPTabPagerAdapter
import com.example.pillmate.databinding.FragmentHomeBinding
import com.example.pillmate.databinding.FragmentListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator


class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_list 레이아웃을 인플레이트합니다.
        binding = FragmentListBinding.inflate(inflater, container, false)
        //val view = inflater.inflate(R.layout.fragment_list, container, false)

        // ViewPager와 TabLayout을 설정합니다.
        val viewPager = binding.tabPager
        val tabLayout = binding.tabLayout

        // ViewPager에 어댑터 설정
        val adapter = MPTabPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter

        // TabLayout과 ViewPager를 연결
        tabLayout.setupWithViewPager(viewPager)


        binding.btnPrecription.setOnClickListener {
            val intent = Intent(requireContext(), PrescriptActivity::class.java)
            startActivity(intent)
        }

        //val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        //val prescriptBtn = view.findViewById<FloatingActionButton>(R.id.btn_precription)

        var isFabOpen = false

        binding.fab.setOnClickListener {
            if (!isFabOpen) {
                // FAB가 열릴 때의 애니메이션
                binding.btnPrecription.visibility = View.VISIBLE
                binding.tvPrescript.visibility = View.VISIBLE
                binding.btnPillRecog.visibility= View.VISIBLE
                binding.btnPillWrite.visibility= View.VISIBLE
                binding.tvPillRecog.visibility= View.VISIBLE
                binding.tvPillWrite.visibility= View.VISIBLE

                binding.viewWhite.visibility=View.VISIBLE

                // FAB와 동일한 위치로 설정
                binding.btnPrecription.translationY = binding.fab.translationY
                binding.tvPrescript.translationY = binding.fab.translationY
                binding.btnPillRecog.translationY = binding.fab.translationY
                binding.btnPillWrite.translationY = binding.fab.translationY
                binding.tvPillRecog.translationY = binding.fab.translationY
                binding.tvPillWrite.translationY = binding.fab.translationY

                // 각각 다른 지속 시간을 가진 버튼 애니메이션
                val precriptionButtonAnimator = ObjectAnimator.ofFloat(binding.btnPrecription, "translationY", binding.fab.translationY, binding.fab.translationY - 180f)
                precriptionButtonAnimator.duration = 700  // 300ms 지속

                val pillRecogButtonAnimator = ObjectAnimator.ofFloat(binding.btnPillRecog, "translationY", binding.fab.translationY, binding.fab.translationY - 360f)
                pillRecogButtonAnimator.duration = 700  // 500ms 지속

                val pillWriteButtonAnimator = ObjectAnimator.ofFloat(binding.btnPillWrite, "translationY", binding.fab.translationY, binding.fab.translationY - 530f)
                pillWriteButtonAnimator.duration = 700  // 700ms 지속

                // 각각 다른 지속 시간을 가진 텍스트 애니메이션
                val prescriptTextAnimator = ObjectAnimator.ofFloat(binding.tvPrescript, "translationY", binding.fab.translationY, binding.fab.translationY - 180f)
                prescriptTextAnimator.duration = 700  // 300ms 지속

                val pillRecogTextAnimator = ObjectAnimator.ofFloat(binding.tvPillRecog, "translationY", binding.fab.translationY, binding.fab.translationY - 360f)
                pillRecogTextAnimator.duration = 700  // 500ms 지속

                val pillWriteTextAnimator = ObjectAnimator.ofFloat(binding.tvPillWrite, "translationY", binding.fab.translationY, binding.fab.translationY - 530f)
                pillWriteTextAnimator.duration = 700  // 700ms 지속

                // 동시에 애니메이션 시작
                precriptionButtonAnimator.start()
                pillRecogButtonAnimator.start()
                pillWriteButtonAnimator.start()

                prescriptTextAnimator.start()
                pillRecogTextAnimator.start()
                pillWriteTextAnimator.start()

                isFabOpen = true
            } else {

                // FAB가 닫힐 때의 애니메이션
                val precriptionButtonAnimator = ObjectAnimator.ofFloat(binding.btnPrecription, "translationY", binding.fab.translationY - 180f, binding.fab.translationY)
                precriptionButtonAnimator.duration = 700

                val pillRecogButtonAnimator = ObjectAnimator.ofFloat(binding.btnPillRecog, "translationY", binding.fab.translationY - 360f, binding.fab.translationY)
                pillRecogButtonAnimator.duration = 700

                val pillWriteButtonAnimator = ObjectAnimator.ofFloat(binding.btnPillWrite, "translationY", binding.fab.translationY - 530f, binding.fab.translationY)
                pillWriteButtonAnimator.duration = 700

                val prescriptTextAnimator = ObjectAnimator.ofFloat(binding.tvPrescript, "translationY", binding.fab.translationY - 180f, binding.fab.translationY)
                prescriptTextAnimator.duration = 700

                val pillRecogTextAnimator = ObjectAnimator.ofFloat(binding.tvPillRecog, "translationY", binding.fab.translationY - 360f, binding.fab.translationY)
                pillRecogTextAnimator.duration = 700

                val pillWriteTextAnimator = ObjectAnimator.ofFloat(binding.tvPillWrite, "translationY", binding.fab.translationY - 530f, binding.fab.translationY)
                pillWriteTextAnimator.duration = 700

                precriptionButtonAnimator.start()
                pillRecogButtonAnimator.start()
                pillWriteButtonAnimator.start()

                prescriptTextAnimator.start()
                pillRecogTextAnimator.start()
                pillWriteTextAnimator.start()

                precriptionButtonAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.btnPrecription.visibility = View.INVISIBLE
                        binding.tvPrescript.visibility = View.INVISIBLE
                        binding.btnPillRecog.visibility = View.INVISIBLE
                        binding.tvPillRecog.visibility = View.INVISIBLE
                        binding.btnPillWrite.visibility = View.INVISIBLE
                        binding.tvPillWrite.visibility = View.INVISIBLE
                        binding.viewWhite.visibility=View.INVISIBLE
                    }
                })

                isFabOpen = false
            }
        }


        //return view
        return binding.root
    }


}
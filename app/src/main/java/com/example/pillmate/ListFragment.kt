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


    //private lateinit var binding: FragmentListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_list 레이아웃을 인플레이트합니다.
        //binding = FragmentListBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // ViewPager와 TabLayout을 설정합니다.
        val viewPager = view.findViewById<ViewPager>(R.id.tab_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        // ViewPager에 어댑터 설정
        val adapter = MPTabPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter

        // TabLayout과 ViewPager를 연결
        tabLayout.setupWithViewPager(viewPager)

        val prescriptBtn = view.findViewById<FloatingActionButton>(R.id.btn_precription)
        prescriptBtn.setOnClickListener {
            val intent = Intent(requireContext(), PrescriptActivity::class.java)
            startActivity(intent)
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        //val prescriptBtn = view.findViewById<FloatingActionButton>(R.id.btn_precription)

        var isFabOpen = false

        fab.setOnClickListener {
            if (!isFabOpen) {
                // FAB가 열릴 때의 애니메이션
                prescriptBtn.visibility = View.VISIBLE
                prescriptBtn.translationY = fab.translationY // FAB와 동일한 위치로 설정
                val animator = ObjectAnimator.ofFloat(prescriptBtn, "translationY", fab.translationY, fab.translationY - 180f)
                animator.duration = 300
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.start()
                isFabOpen = true
            } else {
                // FAB가 닫힐 때의 애니메이션
                val animator = ObjectAnimator.ofFloat(prescriptBtn, "translationY", fab.translationY - 180f, fab.translationY)
                animator.duration = 300
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.start()
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        prescriptBtn.visibility = View.INVISIBLE
                    }
                })
                isFabOpen = false
            }
        }

        return view
        //return binding.root
    }


}
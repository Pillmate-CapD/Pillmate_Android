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
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private var isFabOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)

        val viewPager = binding.tabPager
        val tabLayout = binding.tabLayout

        val adapter = MPTabPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        binding.btnPrecription.setOnClickListener {
            val intent = Intent(requireContext(), PrescriptActivity::class.java)
            startActivity(intent)
            binding.btnPrecription.visibility = View.INVISIBLE
            binding.tvPrescript.visibility = View.INVISIBLE
            binding.btnPillRecog.visibility = View.INVISIBLE
            binding.tvPillRecog.visibility = View.INVISIBLE
            binding.btnPillWrite.visibility = View.INVISIBLE
            binding.tvPillWrite.visibility = View.INVISIBLE
            binding.viewWhite.visibility = View.INVISIBLE

            // FAB를 닫는 메서드 호출
            if (isFabOpen) {
                closeFab()
            }
        }

        binding.btnPillWrite.setOnClickListener {
            val intent = Intent(requireContext(), WriteMediActivity::class.java)
            startActivity(intent)
            binding.btnPrecription.visibility = View.INVISIBLE
            binding.tvPrescript.visibility = View.INVISIBLE
            binding.btnPillRecog.visibility = View.INVISIBLE
            binding.tvPillRecog.visibility = View.INVISIBLE
            binding.btnPillWrite.visibility = View.INVISIBLE
            binding.tvPillWrite.visibility = View.INVISIBLE
            binding.viewWhite.visibility = View.INVISIBLE

            // FAB를 닫는 메서드 호출
            if (isFabOpen) {
                closeFab()
            }
        }

        binding.btnPillRecog.setOnClickListener {
            val intent = Intent(requireContext(), MediScanActivity::class.java)
            startActivity(intent)
            binding.btnPrecription.visibility = View.INVISIBLE
            binding.tvPrescript.visibility = View.INVISIBLE
            binding.btnPillRecog.visibility = View.INVISIBLE
            binding.tvPillRecog.visibility = View.INVISIBLE
            binding.btnPillWrite.visibility = View.INVISIBLE
            binding.tvPillWrite.visibility = View.INVISIBLE
            binding.viewWhite.visibility = View.INVISIBLE

            // FAB를 닫는 메서드 호출
            if (isFabOpen) {
                closeFab()
            }
        }

        binding.fab.setOnClickListener {
            if (!isFabOpen) {
                openFab()
            } else {
                closeFab()
            }
        }

        return binding.root
    }

    private fun openFab() {
        binding.btnPrecription.visibility = View.VISIBLE
        binding.tvPrescript.visibility = View.VISIBLE
        binding.btnPillRecog.visibility = View.VISIBLE
        binding.btnPillWrite.visibility = View.VISIBLE
        binding.tvPillRecog.visibility = View.VISIBLE
        binding.tvPillWrite.visibility = View.VISIBLE
        binding.viewWhite.visibility = View.VISIBLE

        val precriptionButtonAnimator = ObjectAnimator.ofFloat(binding.btnPrecription, "translationY", binding.fab.translationY, binding.fab.translationY - 180f)
        precriptionButtonAnimator.duration = 300

        val pillRecogButtonAnimator = ObjectAnimator.ofFloat(binding.btnPillRecog, "translationY", binding.fab.translationY, binding.fab.translationY - 360f)
        pillRecogButtonAnimator.duration = 300

        val pillWriteButtonAnimator = ObjectAnimator.ofFloat(binding.btnPillWrite, "translationY", binding.fab.translationY, binding.fab.translationY - 530f)
        pillWriteButtonAnimator.duration = 300

        val prescriptTextAnimator = ObjectAnimator.ofFloat(binding.tvPrescript, "translationY", binding.fab.translationY, binding.fab.translationY - 180f)
        prescriptTextAnimator.duration = 300

        val pillRecogTextAnimator = ObjectAnimator.ofFloat(binding.tvPillRecog, "translationY", binding.fab.translationY, binding.fab.translationY - 340f)
        pillRecogTextAnimator.duration = 300

        val pillWriteTextAnimator = ObjectAnimator.ofFloat(binding.tvPillWrite, "translationY", binding.fab.translationY, binding.fab.translationY - 510f)
        pillWriteTextAnimator.duration = 300

        precriptionButtonAnimator.start()
        pillRecogButtonAnimator.start()
        pillWriteButtonAnimator.start()

        prescriptTextAnimator.start()
        pillRecogTextAnimator.start()
        pillWriteTextAnimator.start()

        isFabOpen = true
    }

    private fun closeFab() {
        val precriptionButtonAnimator = ObjectAnimator.ofFloat(binding.btnPrecription, "translationY", binding.fab.translationY - 180f, binding.fab.translationY)
        precriptionButtonAnimator.duration = 300

        val pillRecogButtonAnimator = ObjectAnimator.ofFloat(binding.btnPillRecog, "translationY", binding.fab.translationY - 360f, binding.fab.translationY)
        pillRecogButtonAnimator.duration = 300

        val pillWriteButtonAnimator = ObjectAnimator.ofFloat(binding.btnPillWrite, "translationY", binding.fab.translationY - 530f, binding.fab.translationY)
        pillWriteButtonAnimator.duration = 300

        val prescriptTextAnimator = ObjectAnimator.ofFloat(binding.tvPrescript, "translationY", binding.fab.translationY - 180f, binding.fab.translationY)
        prescriptTextAnimator.duration = 300

        val pillRecogTextAnimator = ObjectAnimator.ofFloat(binding.tvPillRecog, "translationY", binding.fab.translationY - 340f, binding.fab.translationY)
        pillRecogTextAnimator.duration = 300

        val pillWriteTextAnimator = ObjectAnimator.ofFloat(binding.tvPillWrite, "translationY", binding.fab.translationY - 510f, binding.fab.translationY)
        pillWriteTextAnimator.duration = 300

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
                binding.viewWhite.visibility = View.INVISIBLE
            }
        })

        isFabOpen = false
    }

    override fun onResume() {
        super.onResume()

        // 화면에 돌아올 때도 FAB를 닫아줌
        if (isFabOpen) {
            closeFab()
        }
    }
}

package com.example.pillmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pillmate.databinding.ActivityOnonBinding

class OnOnActivity : AppCompatActivity() {
    lateinit var binding: ActivityOnonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewPager2 설정
        binding.viewPager2.adapter = OnboardingPagerAdapter(this)
    }

    // FragmentStateAdapter 구현
    private inner class OnboardingPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4 // 총 Fragment 개수

        override fun createFragment(position: Int) = when (position) {
            0 -> OnOn1Fragment()
            1 -> OnOn2Fragment()
            2 -> OnOn3Fragment()
            3 -> OnOn4Fragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}

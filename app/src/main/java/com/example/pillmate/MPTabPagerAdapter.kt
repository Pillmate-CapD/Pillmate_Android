package com.example.capdi_eat_test

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MPTabPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        // 탭에 따른 Fragment 반환
        return when (position) {
            0 -> MyPillListFragment()  // 약 리스트 Fragment
            1 -> AlarmSetFragment()  // 알람 Fragment
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }

    override fun getCount(): Int {
        // 탭 개수 반환
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        // 탭의 제목 설정
        return when (position) {
            0 -> "약 리스트"
            1 -> "알람"
            else -> null
        }
    }
}
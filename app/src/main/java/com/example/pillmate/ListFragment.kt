package com.example.pillmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.capdi_eat_test.MPTabPagerAdapter
import com.google.android.material.tabs.TabLayout

class ListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_list 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // ViewPager와 TabLayout을 설정합니다.
        val viewPager = view.findViewById<ViewPager>(R.id.tab_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        // ViewPager에 어댑터 설정
        val adapter = MPTabPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter

        // TabLayout과 ViewPager를 연결
        tabLayout.setupWithViewPager(viewPager)

        return view
    }


}
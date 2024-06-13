package com.example.capdi_eat_test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.pillmate.R

class MyPillListFragment : Fragment() {
    private var isFirstTime = true // 처음인지 여부를 나타내는 플래그
    private lateinit var AllBt: Button
    private lateinit var bt2: Button
    private lateinit var bt3: Button
    private lateinit var bt4: Button
    private lateinit var bt5: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mypill_list, container, false)

        // 처음에는 All 버튼
        if (isFirstTime) {
            // 프래그먼트 컨테이너에 이미 추가된 프래그먼트가 있는지 확인
            val fragment = parentFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment == null) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, AllFragment())
                    .commit()
            }
            isFirstTime = false // 다음에는 이 부분이 실행되지 않도록 플래그 변경
        }

        // 전체 버튼 클릭 시
        AllBt = view.findViewById(R.id.AllBt)
        AllBt.setOnClickListener {
            // 올버튼 프래그먼트를 보여줌
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AllFragment())
                .commit()

            // 버튼 배경색 및 텍스트 색상 변경
            AllBt.setBackgroundResource(R.drawable.active_pilllist)
            AllBt.setTextColor(resources.getColor(android.R.color.white))
            bt2.setBackgroundResource(R.drawable.inactive_pilllist)
            bt2.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt3.setBackgroundResource(R.drawable.inactive_pilllist)
            bt3.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt4.setBackgroundResource(R.drawable.inactive_pilllist)
            bt4.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt5.setBackgroundResource(R.drawable.inactive_pilllist)
            bt5.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
        }

        // 버튼2 클릭 시
        bt2 = view.findViewById(R.id.Bt2)
        bt2.setOnClickListener {
            // 버튼2 프래그먼트를 보여줌
            //parentFragmentManager.beginTransaction()
            //    .replace(R.id.fragment_container, Bt2Fragment())
            //    .commit()

            // 버튼 배경색 및 텍스트 색상 변경
            bt2.setBackgroundResource(R.drawable.active_pilllist)
            bt2.setTextColor(resources.getColor(android.R.color.white))
            AllBt.setBackgroundResource(R.drawable.inactive_pilllist)
            AllBt.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt3.setBackgroundResource(R.drawable.inactive_pilllist)
            bt3.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt4.setBackgroundResource(R.drawable.inactive_pilllist)
            bt4.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt5.setBackgroundResource(R.drawable.inactive_pilllist)
            bt5.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
        }

        // 버튼 3 클릭 시
        bt3 = view.findViewById(R.id.Bt3)
        bt3.setOnClickListener {
            // 프래그먼트를 보여줌
            // 원하는 프래그먼트로 변경하도록 코드 작성
            // 예: parentFragmentManager.beginTransaction().replace(R.id.fragment_container, YourFragment()).commit()

            // 버튼 배경색 및 텍스트 색상 변경
            bt3.setBackgroundResource(R.drawable.active_pilllist)
            bt3.setTextColor(resources.getColor(android.R.color.white))
            AllBt.setBackgroundResource(R.drawable.inactive_pilllist)
            AllBt.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt2.setBackgroundResource(R.drawable.inactive_pilllist)
            bt2.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt4.setBackgroundResource(R.drawable.inactive_pilllist)
            bt4.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt5.setBackgroundResource(R.drawable.inactive_pilllist)
            bt5.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
        }

        // 버튼 4 클릭 시
        bt4 = view.findViewById(R.id.Bt4)
        bt4.setOnClickListener {
            // 프래그먼트를 보여줌
            // 원하는 프래그먼트로 변경하도록 코드 작성
            // 예: parentFragmentManager.beginTransaction().replace(R.id.fragment_container, YourFragment()).commit()

            // 버튼 배경색 및 텍스트 색상 변경
            bt4.setBackgroundResource(R.drawable.active_pilllist)
            bt4.setTextColor(resources.getColor(android.R.color.white))
            AllBt.setBackgroundResource(R.drawable.inactive_pilllist)
            AllBt.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt2.setBackgroundResource(R.drawable.inactive_pilllist)
            bt2.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt3.setBackgroundResource(R.drawable.inactive_pilllist)
            bt3.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt5.setBackgroundResource(R.drawable.inactive_pilllist)
            bt5.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
        }

        // 버튼 5 클릭 시
        bt5 = view.findViewById(R.id.Bt5)
        bt5.setOnClickListener {
            // 프래그먼트를 보여줌
            // 원하는 프래그먼트로 변경하도록 코드 작성
            // 예: parentFragmentManager.beginTransaction().replace(R.id.fragment_container, YourFragment()).commit()

            // 버튼 배경색 및 텍스트 색상 변경
            bt5.setBackgroundResource(R.drawable.active_pilllist)
            bt5.setTextColor(resources.getColor(android.R.color.white))
            AllBt.setBackgroundResource(R.drawable.inactive_pilllist)
            AllBt.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt2.setBackgroundResource(R.drawable.inactive_pilllist)
            bt2.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt3.setBackgroundResource(R.drawable.inactive_pilllist)
            bt3.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
            bt4.setBackgroundResource(R.drawable.inactive_pilllist)
            bt4.setTextColor(0xFF3E3E3E.toInt()) // 직접 색상 코드로 설정
        }

        return view
    }
}
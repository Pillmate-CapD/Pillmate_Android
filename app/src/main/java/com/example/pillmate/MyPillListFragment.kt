package com.example.capdi_eat_test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.CalendarFragment
import com.example.pillmate.ListCategoryAdapter
import com.example.pillmate.R
import com.example.pillmate.RetrofitApi
import com.example.pillmate.databinding.FragmentMypillListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPillListFragment : Fragment() {
    private var isFirstTime = true // 처음인지 여부를 나타내는 플래그

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: ListCategoryAdapter

    private var _binding: FragmentMypillListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ViewBinding 인플레이션
        _binding = FragmentMypillListBinding.inflate(inflater, container, false)
        val view = binding.root

        // 처음에는 All 버튼
        if (isFirstTime) {
            val fragment = parentFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment == null) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, AllFragment())
                    .commit()
            }
            isFirstTime = false
        }

        // 리사이클러뷰 설정
        val categories = listOf("전체", "고지혈증", "고혈압", "당뇨", "기타")
        categoryAdapter = ListCategoryAdapter(categories) { position ->
            // 클릭된 아이템에 대한 처리 로직
            Toast.makeText(context, "${categories[position]} 클릭됨", Toast.LENGTH_SHORT).show()
        }

        binding.recyCategory.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
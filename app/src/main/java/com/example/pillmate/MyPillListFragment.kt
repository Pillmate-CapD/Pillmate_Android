package com.example.capdi_eat_test

import AllFragment
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.ListCategoryAdapter
import com.example.pillmate.MediListResponse
import com.example.pillmate.R
import com.example.pillmate.RetrofitApi
import com.example.pillmate.SharedViewModel
import com.example.pillmate.databinding.FragmentMypillListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPillListFragment : Fragment() {
    private var isFirstTime = true // 처음인지 여부를 나타내는 플래그
    private lateinit var viewModel: SharedViewModel

    private lateinit var categoryAdapter: ListCategoryAdapter
    private var _binding: FragmentMypillListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ViewBinding 인플레이션
        _binding = FragmentMypillListBinding.inflate(inflater, container, false)

        // ViewModel 설정
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // ViewModel에서 약물 리스트를 관찰하고 RecyclerView 업데이트
        viewModel.medicineList.observe(viewLifecycleOwner, { medicines ->
            updateCategoryAdapter(medicines)  // 약물이 변경되면 카테고리도 갱신
        })

        // 처음에 AllFragment가 표시되도록 설정
        if (isFirstTime) {
            val fragment = parentFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment == null) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, AllFragment())
                    .commit()
            }
            isFirstTime = false
        }

        // API 호출을 통해 약물 리스트 및 카테고리 가져오기
        fetchMedicineListAndCategories()

        return binding.root
    }

    // API 호출을 통해 약물 리스트 및 카테고리 정보를 가져옴
    private fun fetchMedicineListAndCategories() {
        val service = RetrofitApi.getRetrofitService
        val call = service.getMediAll()  // 약 리스트를 가져오는 API 호출

        call.enqueue(object : Callback<List<MediListResponse>> {
            override fun onResponse(
                call: Call<List<MediListResponse>>,
                response: Response<List<MediListResponse>>
            ) {
                if (response.isSuccessful) {
                    val mediList = response.body()
                    if (!mediList.isNullOrEmpty()) {
                        // 약물 리스트를 ViewModel에 설정
                        viewModel.setMedicineList(mediList)

                        // 카테고리 추출 및 중복 제거
                        val categories = mediList.map { it.category }.distinct()

                        if (categories.isNotEmpty()) {
                            // 카테고리 리스트에 "전체" 항목 추가
                            val updatedCategories = listOf("전체") + categories

                            // 카테고리 어댑터 설정
                            setupCategoryAdapter(updatedCategories)

                            // 카테고리 뷰를 보여줌
                            //binding.recyCategory.visibility = View.VISIBLE
                        } else {
                            // 카테고리가 없으면 카테고리 뷰를 숨김
                            //binding.recyCategory.visibility = View.GONE
                        }
                    } else {
                        // 약물이 없는 경우 카테고리 뷰를 숨김
                        //binding.recyCategory.visibility = View.GONE
                        Log.d("MyPillListFragment", "약물이 없습니다.")
                    }
                } else {
                    Log.d("MyPillListFragment", "약 리스트 가져오기 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MediListResponse>>, t: Throwable) {
                Log.e("MyPillListFragment", "API 호출 실패", t)
            }
        })
    }


    // 카테고리 어댑터 설정 및 RecyclerView와 연결
    private fun setupCategoryAdapter(categories: List<String>) {
        categoryAdapter = ListCategoryAdapter(categories) { position ->
            val selectedCategory = categories[position]

            // AllFragment에서 선택된 카테고리에 맞게 필터링 함수 호출
            val fragment = parentFragmentManager.findFragmentById(R.id.fragment_container) as? AllFragment
            fragment?.filterMedicineList(selectedCategory)
        }

//        binding.recyCategory.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = categoryAdapter
//        }
    }

    // 카테고리 어댑터 설정
    private fun updateCategoryAdapter(medicineList: List<MediListResponse>) {
        val categories = medicineList.map { it.category }.distinct()
        val updatedCategories = listOf("전체") + categories
        setupCategoryAdapter(updatedCategories)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // Fragment가 재개될 때 약물 리스트 및 카테고리 다시 가져옴
        fetchMedicineListAndCategories()
    }
}

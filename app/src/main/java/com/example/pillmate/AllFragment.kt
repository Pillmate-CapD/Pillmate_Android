import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capdi_eat_test.MyPillListFragment
import com.example.pillmate.MediListAdapter
import com.example.pillmate.MediListAdapter.Companion.EDIT_REQUEST_CODE
import com.example.pillmate.MediListResponse
import com.example.pillmate.R
import com.example.pillmate.RetrofitApi
import com.example.pillmate.SharedViewModel
import com.example.pillmate.databinding.FragmentAllbtnBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllFragment : Fragment() {

    private lateinit var binding: FragmentAllbtnBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MediListAdapter
    private var medicineList: List<MediListResponse> = emptyList() // API로부터 받은 원본 데이터
    private var filteredList: List<MediListResponse> = emptyList() // 필터링된 데이터
    private var selectedCategory: String = "전체" // 기본값은 항상 '전체'

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllbtnBinding.inflate(inflater, container, false)

        // RecyclerView 및 MediListAdapter 설정
        adapter = MediListAdapter(emptyList(), requireContext(), this, SharedViewModel())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 처음에는 기본적으로 전체 카테고리로 리스트를 보여줌
        fetchMedicineList()

        // 바인딩된 루트 뷰 반환
        return binding.root
    }

    fun fetchMedicineList() {
        val service = RetrofitApi.getRetrofitService
        val call = service.getMediAll()

        call.enqueue(object : Callback<List<MediListResponse>> {
            override fun onResponse(
                call: Call<List<MediListResponse>>,
                response: Response<List<MediListResponse>>
            ) {
                if (response.isSuccessful) {
                    val mediList = response.body()

                    if (mediList.isNullOrEmpty()) {
                        binding.nonMediLayout.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        // 받은 데이터 저장
                        medicineList = mediList

                        // 기본적으로 '전체' 카테고리를 선택한 상태에서 필터링
                        filterMedicineList("전체")
                    }
                } else {
                    Log.d("AllFragment", "약 리스트 가져오기 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MediListResponse>>, t: Throwable) {
                Log.e("AllFragment", "API 호출 실패", t)
            }
        })
    }

    // 선택된 카테고리에 따라 필터링된 리스트를 업데이트하는 함수
    fun filterMedicineList(category: String) {
        selectedCategory = category // 선택된 카테고리 저장

        filteredList = if (category == "전체") {
            medicineList // 전체 카테고리 선택 시 원본 데이터 그대로 사용
        } else {
            medicineList.filter { it.category == category } // 선택된 카테고리에 맞춰 필터링
        }

        // 필터링된 리스트로 어댑터 업데이트
        adapter.updateData(filteredList)

        if (filteredList.isEmpty()) {
            binding.nonMediLayout.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.nonMediLayout.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        fetchMedicineList()

        super.onResume()
    }
}


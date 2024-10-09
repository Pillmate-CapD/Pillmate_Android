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
import com.example.pillmate.MediListAdapter
import com.example.pillmate.MediListAdapter.Companion.EDIT_REQUEST_CODE
import com.example.pillmate.MediListResponse
import com.example.pillmate.R
import com.example.pillmate.RetrofitApi
import com.example.pillmate.databinding.FragmentAllbtnBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllFragment : Fragment() {

    private lateinit var binding: FragmentAllbtnBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MediListAdapter
    private var medicineList: List<MediListResponse> = emptyList()

    private val editMediActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("AllFragment", "resultCode: ${result.resultCode}")

        if (result.resultCode == Activity.RESULT_OK) {
            val isMediEdited = result.data?.getBooleanExtra("isMediEdited", false) ?: false
            if (isMediEdited) {
                // 약물이 수정되었을 때 fetchMedicineList 호출
                fetchMedicineList()
                Log.d("AllFragment", "약물이 수정되어 fetchMedicineList 호출됨")
            }
        } else {
            Log.d("AllFragment", "RESULT_OK가 아님")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllbtnBinding.inflate(inflater, container, false)

        // RecyclerView 및 MediListAdapter 설정
        adapter = MediListAdapter(emptyList(), requireContext(), editMediActivityLauncher, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 약 리스트 가져오기
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

                    if(mediList.isNullOrEmpty()){
                        binding.nonMediLayout.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                    else{
                        mediList.let {
                            medicineList = it
                            adapter.updateData(it)
                        }
                        binding.nonMediLayout.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
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

    override fun onResume() {
        fetchMedicineList()

        super.onResume()
    }
}


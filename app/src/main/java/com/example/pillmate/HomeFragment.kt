package com.example.pillmate

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillmate.databinding.FragmentHomeBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dateAdapter: DateAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var pillListAdapter: PillListAdapter
    private val categoryItems = mutableListOf<CategoryItem>()

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(requireContext())
    }

    private val REQUEST_CODE_EAT_MEDI = 1001

    private val dateViewModel: DateViewModel by viewModels()
    private val pillViewModel: PillViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카테고리 아이템 추가
        categoryItems.add(CategoryItem("전체", true))
        categoryItems.add(CategoryItem("고혈압"))
        categoryItems.add(CategoryItem("고지혈증"))
        categoryItems.add(CategoryItem("당뇨"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 홈화면 => 주간 달력
        dateAdapter = DateAdapter(dateViewModel.dateItems as ArrayList<DateItem>)
        binding.dateList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dateList.adapter = dateAdapter

        // 홈화면 => 약 카테고리 파트
        categoryAdapter = CategoryAdapter(categoryItems as ArrayList<CategoryItem>)
        binding.pillCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.pillCategory.adapter = categoryAdapter

        // 홈화면 => 약 리스트 파트
        pillListAdapter = PillListAdapter(arrayListOf(), this, preferencesHelper)
        binding.pillList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.pillList.adapter = pillListAdapter

        binding.alertImg.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.alarmListActivity)
        }

        binding.homePillProgressBar.setMaxProgress(100)
        binding.homePillProgressBar.setProgress(60)

        // ViewModel을 사용하여 데이터 로드
        pillViewModel.loadPillItems(
            listOf(
                PillListItem("오전 8:00", "트윈스타정"),
                PillListItem("오전 11:00", "디아미크롱서방정"),
                PillListItem("오후 12:00", "파스틱정"),
                PillListItem("오후 6:30", "파스틱정"),
                PillListItem("오후 9:00", "바이토린")
            )
        )

        pillViewModel.pillItems.observe(viewLifecycleOwner) { pillItems ->
            pillListAdapter.updateItems(pillItems)
        }

        binding.btnPillList.setOnClickListener {
            findNavController().navigate(R.id.listFragment)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EAT_MEDI && resultCode == Activity.RESULT_OK) {
            val completed = data?.getBooleanExtra("completed", false) ?: false
            val position = data?.getIntExtra("position", -1) ?: -1
            if (completed && position != -1) {
                pillViewModel.setPillCompleted(position, true)
                pillListAdapter.notifyItemChanged(position)
            }
        }
    }
}
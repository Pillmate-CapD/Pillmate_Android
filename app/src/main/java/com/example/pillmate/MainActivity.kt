package com.example.pillmate

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pillmate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.bottomNavi.itemIconTintList = null

        binding.apply {
            // NavController 획득
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            // BottomNavigationView와 NavController 연결
            bottomNavi.setupWithNavController(navController)

            binding.bottomNavi.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.homeFragment -> {
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true) // 홈 프래그먼트로 돌아갈 때 다른 프래그먼트를 모두 제거
                            .build()

                        navController.navigate(R.id.homeFragment, null, navOptions)
                        true
                    }
                    else -> {
                        navController.navigate(item.itemId) // 다른 메뉴는 기본 동작 수행
                        true
                    }
                }
            }
        }
    }
}
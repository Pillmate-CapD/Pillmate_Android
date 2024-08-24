package com.example.pillmate

import android.os.Bundle
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            // NavController 획득
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            //val navController = findNavController(R.id.nav_host_fragment)
            bottomNavi.setupWithNavController(navController)

            bottomNavi.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.homeFragment -> {
                        if (navController.currentDestination?.id != R.id.homeFragment) {
                            navController.popBackStack(R.id.homeFragment, true)
                            navController.navigate(R.id.homeFragment)
                        }
                        true
                    }
                    R.id.listFragment -> {
                        if (navController.currentDestination?.id != R.id.listFragment) {
                            navController.navigate(R.id.listFragment)
                        }
                        true
                    }
                    else -> false
                }
            }

            //navController.navigate(R.id.homeFragment, null, NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build())

        }
    }
}
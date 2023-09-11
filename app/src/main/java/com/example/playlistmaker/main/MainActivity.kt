package com.example.playlistmaker.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigationView: BottomNavigationView = binding.navigationView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        navigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addPlayListFragment, R.id.playerFragment, R.id.playListFragment -> {
                    if (binding.navigationView.visibility == View.VISIBLE) {
                        binding.navigationView.animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)
                        binding.navigationView.animate()
                        binding.navigationView.visibility = View.GONE
                    }
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                }

                else -> {
                    if (binding.navigationView.visibility == View.GONE) {
                        binding.navigationView.visibility = View.VISIBLE
                        binding.navigationView.animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up)
                        binding.navigationView.animate()
                    }
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                }
            }
        }

    }
}
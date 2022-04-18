package com.example.healthring.splashscreen

import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.example.healthring.R

class SplashScreen : Fragment (R.layout.splash_screen_fragment) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_main)

        support

    }

    private fun setContentView(activityMain: Int) {

    }

}
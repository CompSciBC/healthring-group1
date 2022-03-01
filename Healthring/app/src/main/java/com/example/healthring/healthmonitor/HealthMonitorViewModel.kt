package com.example.healthring.healthmonitor

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthring.R

class HealthMonitorViewModel: ViewModel() {

    val hRateColor = MutableLiveData<Drawable>()
    val bOxygenColor = MutableLiveData<Drawable>()
    val bPressureColor = MutableLiveData<Drawable>()
}
package com.example.healthring.fitnesstracker

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FitnessTrackerViewModel: ViewModel() {
    val stepsColor = MutableLiveData<Drawable>()
    val distanceColor = MutableLiveData<Drawable>()
    val caloriesColor = MutableLiveData<Drawable>()
}
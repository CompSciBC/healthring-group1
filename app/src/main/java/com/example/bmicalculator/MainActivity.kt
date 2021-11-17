package com.example.bmicalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bmicalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calculateButton.setOnClickListener { calculateBmi() }
    }

    fun calculateBmi() {
        val stringInFeetField = binding.heightFeet.text.toString()
        val feet = stringInFeetField.toDouble()
        val stringInInchField = binding.heightInch.text.toString()
        val inch = stringInInchField.toDouble()
        val totalHeight = feet * 12 + inch

        val stringInWeightField = binding.weightPounds.text.toString()
        val weight = stringInWeightField.toDouble()

        val result = (703 * weight) / (totalHeight * totalHeight)
        val formattedResult: Double = String.format("%.2f", result).toDouble()
        binding.bmi.text = formattedResult.toString()

    }
}
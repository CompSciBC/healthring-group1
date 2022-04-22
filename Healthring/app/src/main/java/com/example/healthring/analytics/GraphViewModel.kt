package com.example.healthring.analytics

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.healthring.model.SensorData
import com.example.healthring.model.Sensors
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.ArrayList

class GraphViewModel: ViewModel() {
    fun getBarChartData(sensor: Sensors): BarData {
        val values = ArrayList<BarEntry>()
        val currentDateTime = LocalDateTime.now()
        // comparison date, starting from the oldest day. In this case, the day from one week ago
        var comparisonDate = currentDateTime.with(TemporalAdjusters.previous(
            DayOfWeek.of(currentDateTime.dayOfWeek.value)))
        Log.i("GRAPHVIEWMODEL", "Comparison date: ${comparisonDate.toString()}")
        for (i in 0..6) {
            var sumOfData = 0
            var numOfDataPoints = 0
            val sensorDataWithMatchingDate = sensorDataList!!.filter {
                comparisonDate.dayOfMonth == getEpochFromSensorData(it.date).dayOfMonth
                        && comparisonDate.monthValue == getEpochFromSensorData(it.date).monthValue
            }
            for(sensorRecord in sensorDataWithMatchingDate) {
                sumOfData += getSensorValueToInt(sensor, sensorRecord)
                numOfDataPoints++
            }
            if (sumOfData != 0) {
                values.add(BarEntry(i.toFloat(), (sumOfData / numOfDataPoints).toFloat()))
            } else {
                values.add(BarEntry(i.toFloat(), 0f))
            }
            comparisonDate = comparisonDate.plusDays(1)
        }

        val set1 = BarDataSet(values, "DataSet 1")
        set1.color = Color.rgb(20, 204, 201)
        val data = BarData(set1)
        data.setValueTextSize(10f);
        data.barWidth = 0.9f;
        data.setValueTextColor(Color.BLACK)
        return data
    }

    private fun getSensorValueToInt(sensor: Sensors, record: SensorData): Int {
        return when(sensor) {
            Sensors.STEPS -> record.steps.toInt()
            Sensors.B_OXYGEN -> record.blood_oxygen.toInt()
            Sensors.B_PRESSURE -> record.blood_pressure.toInt()
            Sensors.DISTANCE -> record.distance.toInt()
            Sensors.CALORIES -> record.calories_burnt.toInt()
            Sensors.H_RATE -> record.heart_rate.toInt()
        }
    }

    private fun getEpochFromSensorData(sensorDate: String): LocalDateTime {
        return LocalDateTime.parse(sensorDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}
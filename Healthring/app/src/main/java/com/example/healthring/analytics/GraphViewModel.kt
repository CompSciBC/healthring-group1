package com.example.healthring.analytics

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.healthring.R
import com.example.healthring.model.SensorData
import com.example.healthring.model.Sensors
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.ArrayList

class GraphViewModel: ViewModel() {
    fun getBarChartData(sensor: Sensors, timeScale: TimeScale): BarData {
        val values = ArrayList<BarEntry>()
        when (timeScale) {
            TimeScale.WEEKLY -> filterWeeklySensorDataReturnBarEntries(values, sensor)
            TimeScale.DAILY -> filterDailySensorDataReturnBarEntries(values, sensor)
            else -> Log.e("GRAPHVIEWMODEL", "No timescale was selected.")
        }
        Log.i("GRAPHVIEWMODEL", "Number of bars: ${values.toString()}")
        val set1 = BarDataSet(values, "DataSet 1")
        set1.setGradientColor(Color.rgb(20, 94, 204), Color.rgb(20, 204, 201))
        set1.barBorderWidth = 0.5f
        val data = BarData(set1)
        data.setValueTextSize(24f)
        data.barWidth = 0.85f
        data.setValueTextColor(Color.BLACK)
        data.setDrawValues(false)
        return data
    }

    private fun filterWeeklySensorDataReturnBarEntries(values: ArrayList<BarEntry>, sensor: Sensors) {
        val currentDateTime = LocalDateTime.now()
        // comparison date, starting from the oldest day. In this case, the day from one week ago
        var comparisonDate = currentDateTime.with(TemporalAdjusters.previous(
            DayOfWeek.of(currentDateTime.dayOfWeek.value)))
        var sumOfData = 0
        var numOfDataPoints = 0
        for (i in 0..6) {
            val sensorDataWithMatchingDate = sensorDataList!!.filter {
                comparisonDate.dayOfMonth == getEpochFromSensorData(it.date).dayOfMonth
                        && comparisonDate.monthValue == getEpochFromSensorData(it.date).monthValue
            }
            for(sensorRecord in sensorDataWithMatchingDate) {
                sumOfData += getSensorValueToInt(sensor, sensorRecord)
                numOfDataPoints++
            }
            if (sumOfData != 0) {
                values.add(BarEntry(i.toFloat() , (sumOfData / numOfDataPoints).toFloat()))
            } else {
                values.add(BarEntry(i.toFloat(), 0f))
            }
            comparisonDate = comparisonDate.plusDays(1)
        }
    }

    private fun filterDailySensorDataReturnBarEntries(values: ArrayList<BarEntry>, sensor: Sensors) {
        val currentDateTime = LocalDateTime.now()
        var comparisonDate = currentDateTime.minusDays(1)
        var sumOfData = 0
        var numOfDataPoints = 0
        for (i in 0..7) {
            val sensorDataWithMatchingDate = sensorDataList!!.filter {
                comparisonDate.dayOfMonth == getEpochFromSensorData(it.date).dayOfMonth
                        && comparisonDate.monthValue == getEpochFromSensorData(it.date).monthValue
                        && (comparisonDate <= getEpochFromSensorData(it.date) && (getEpochFromSensorData(it.date) <= comparisonDate.plusHours(3)))
            }
            for (sensorRecord in sensorDataWithMatchingDate) {
                sumOfData += getSensorValueToInt(sensor, sensorRecord)
                numOfDataPoints++
            }
            if (sumOfData != 0) {
                values.add(BarEntry(i.toFloat(), (sumOfData / numOfDataPoints).toFloat()))
            } else {
                values.add(BarEntry(i.toFloat(), 0f))
            }
            comparisonDate = comparisonDate.plusHours(3)
            Log.i("COMPARISON", "Comparison date: ${comparisonDate}")
        }
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
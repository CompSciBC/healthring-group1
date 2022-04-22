package com.example.healthring.analytics
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.app.CoreComponentFactory
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthring.R
import com.example.healthring.databinding.GraphFragmentBinding
import com.example.healthring.model.DataViewModel
import com.example.healthring.model.SensorData
import com.example.healthring.model.Sensors
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.math.absoluteValue

var sensorDataList: MutableList<SensorData>? = null
var graphStartingSensor: Sensors = Sensors.H_RATE
var barChart: BarChart? = null


class GraphFragment: Fragment(R.layout.graph_fragment), AdapterView.OnItemSelectedListener {

    private var binding : GraphFragmentBinding? = null
    private val dataVM: DataViewModel by activityViewModels()
    private val _plotTitle = MutableLiveData("Heart Rate")
    val plotTitle : LiveData<String>
        get() = _plotTitle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = GraphFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            graphFragment = this@GraphFragment
            dataViewModel = dataVM
        }
        _plotTitle.value = when (dataVM.graphStartingSensor) {
            Sensors.STEPS -> "Steps"
            Sensors.B_OXYGEN -> "Blood Oxygen"
            Sensors.B_PRESSURE -> "Blood Pressure"
            Sensors.DISTANCE -> "Distance"
            Sensors.CALORIES -> "Calories"
            Sensors.H_RATE -> "Heart Rate"
        }
        sensorDataList = dataVM.sensorDataList
        graphStartingSensor = dataVM.graphStartingSensor

        // prepare the spinner
        val spinner: Spinner = binding?.dataSelectSpinner!!
        ArrayAdapter.createFromResource(requireContext(), R.array.sensor_names_array, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
    }

    private fun plotBOxygen() {
        graphStartingSensor = Sensors.B_OXYGEN
        _plotTitle.value = "Blood Oxygen"
        refreshGraph()
    }

    private fun plotBPressure() {
        graphStartingSensor = Sensors.B_PRESSURE
        _plotTitle.value = "Blood Pressure"
        refreshGraph()
    }

    private fun plotSteps() {
        graphStartingSensor = Sensors.STEPS
        _plotTitle.value = "Steps"
        refreshGraph()
    }

    private fun plotDistance() {
        graphStartingSensor = Sensors.DISTANCE
        _plotTitle.value = "Distance"
        refreshGraph()
    }

    private fun plotCalories() {
        graphStartingSensor = Sensors.CALORIES
        _plotTitle.value = "Calories"
        refreshGraph()
    }

    private fun plotHRate() {
        graphStartingSensor = Sensors.H_RATE
        _plotTitle.value = "Heart Rate"
        refreshGraph()
    }

    private fun refreshGraph() {
        barChart?.data = getBarChartData(graphStartingSensor)
        barChart?.notifyDataSetChanged()
        barChart?.invalidate();
        barChart?.animateY(500)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.i("GRAPHFRAG", "${parent?.getItemAtPosition(position)} was selected.")
        val itemSelected = parent?.getItemAtPosition(position)
        when(itemSelected) {
            "HEART RATE" -> plotHRate()
            "BLOOD PRESSURE" -> plotBPressure()
            "BLOOD OXYGEN" -> plotBOxygen()
            "STEPS" -> plotSteps()
            "DISTANCE" -> plotDistance()
            "CALORIES" -> plotCalories()
            else -> {
                Log.e("GRAPHFRAG", "Error occurred during graph data spinner selection")
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i("GRAPHFRAG", "Nothing was selected.")
    }
}

// a guarentee must be made before this binding adapter function is called: SensorDataList must not be
// null, and dataVM's graphStartingSensor must not be null
@BindingAdapter("android:setBarData")
fun setLineGraphData(chart: BarChart, sensor: Sensors) {
    // bar chart properties
    barChart = chart
    chart.setDrawBarShadow(false);
    chart.setDrawValueAboveBar(true);
    chart.description.isEnabled = false;
    chart.setMaxVisibleValueCount(60);
    chart.setPinchZoom(false)
    chart.setDrawGridBackground(false);
    chart.legend.isEnabled = false
    chart.invalidate()
    chart.animateY(500)


    // must specify which type of data the bar chart should open with
    chart.data = getBarChartData(sensor)
    // setting the x-axis values
    val xAxis = chart.xAxis
    val currentDateTime = LocalDateTime.now()
    var xAxisDayOfWeek = currentDateTime.with(TemporalAdjusters.previous(DayOfWeek.of(currentDateTime.dayOfWeek.value)))
    // x-axis formatter needs a list of strings to represent the days of the week
    val weekdays = ArrayList<String>()
    for (i in 0..6) {
        xAxisDayOfWeek = xAxisDayOfWeek.plusDays(1)
        when (xAxisDayOfWeek.dayOfWeek.value) {
            1 -> weekdays.add("Mon")
            2 -> weekdays.add("Tue")
            3 -> weekdays.add("Wed")
            4 -> weekdays.add("Thu")
            5 -> weekdays.add("Fri")
            6 -> weekdays.add("Sat")
            7 -> weekdays.add("Sun")
        }
    }
    xAxis.valueFormatter = IndexAxisValueFormatter(weekdays)
    xAxis.textColor = Color.BLACK
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    // y-axis left side
    val leftAxis = chart.axisLeft
    leftAxis.textColor = Color.BLACK
    // y-axis right side
    val rightAxis = chart.axisRight
    rightAxis.textColor = Color.BLACK
}

private fun getBarChartData(sensor: Sensors): BarData {
    val values = ArrayList<BarEntry>()
    val currentDateTime = LocalDateTime.now()
    // comparison date, starting from the oldest day. In this case, the day from one week ago
    var comparisonDate =
        currentDateTime.with(TemporalAdjusters.previous(DayOfWeek.of(currentDateTime.dayOfWeek.value)))
    for (i in 0..6) {
        var sumOfData = 0
        var numOfDataPoints = 0
        // looping through sensor data, summing sensor data grouped by day of the week
        for (sData in 0 until sensorDataList?.size!!) {
            val sDate = sensorDataList!![sData].date
            // string date needs to be converted to a LocalDateTime
            val date: LocalDateTime =
                LocalDateTime.parse(sDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            if (comparisonDate.dayOfMonth == date.dayOfMonth && comparisonDate.monthValue == date.monthValue) {
                sumOfData += when (sensor) {
                    Sensors.STEPS -> sensorDataList!![sData].steps.toInt()
                    Sensors.B_OXYGEN -> sensorDataList!![sData].blood_oxygen.toInt()
                    Sensors.B_PRESSURE -> sensorDataList!![sData].blood_pressure.toInt()
                    Sensors.DISTANCE -> sensorDataList!![sData].distance.toInt()
                    Sensors.CALORIES -> sensorDataList!![sData].calories_burnt.toInt()
                    Sensors.H_RATE -> sensorDataList!![sData].heart_rate.toInt()
                }
                numOfDataPoints++
            }
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


package com.example.healthring.analytics
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.app.CoreComponentFactory
import androidx.core.view.get
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.core.view.size
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.GraphFragmentBinding
import com.example.healthring.model.DataViewModel
import com.example.healthring.model.SensorData
import com.example.healthring.model.Sensors
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

var sensorDataList: MutableList<SensorData>? = null

enum class TimeScale {
    WEEKLY, DAILY, HOURLY
}

class GraphFragment: Fragment(R.layout.graph_fragment), AdapterView.OnItemSelectedListener {

    private var binding : GraphFragmentBinding? = null
    private val dataVM: DataViewModel by activityViewModels()
    private val viewmodel: GraphViewModel by viewModels()

    var barChart: BarChart? = null
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
            Sensors.STEPS -> "STEPS"
            Sensors.B_OXYGEN -> "BLOOD OXYGEN"
            Sensors.B_PRESSURE -> "BLOOD PRESSURE"
            Sensors.DISTANCE -> "DISTANCE"
            Sensors.CALORIES -> "CALORIES"
            Sensors.H_RATE -> "HEART RATE"
        }
        sensorDataList = dataVM.sensorDataList
        barChart = binding?.barChart
        setBarData()
        Log.i("GRAPHFRAGMENT", Sensors.B_PRESSURE.toString())
        // prepare the spinner
        val dataSelectSpinner: Spinner = binding?.dataSelectSpinner!!
        ArrayAdapter.createFromResource(requireContext(), R.array.sensor_names_array, R.layout.custom_graph_sensor_spinner).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dataSelectSpinner.adapter = adapter
            dataSelectSpinner.setSelection(adapter.getPosition(_plotTitle.value))
        }
        dataSelectSpinner.onItemSelectedListener = this


        val timescaleSelectSpinner: Spinner = binding?.graphPlotTimescale!!
        ArrayAdapter.createFromResource(requireContext(), R.array.graph_xaxis_timescale, R.layout.custom_graph_timescale_spinner).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            timescaleSelectSpinner.adapter = adapter
        }
        timescaleSelectSpinner.onItemSelectedListener = this
    }

    private fun plotBOxygen() {
        _plotTitle.value = "BLOOD OXYGEN"
        dataVM.graphStartingSensor = Sensors.B_OXYGEN
        refreshGraph()
    }

    private fun plotBPressure() {
        _plotTitle.value = "BLOOD PRESSURE"
        dataVM.graphStartingSensor = Sensors.B_PRESSURE
        refreshGraph()
    }

    private fun plotSteps() {
        _plotTitle.value = "STEPS"
        dataVM.graphStartingSensor = Sensors.STEPS
        refreshGraph()
        binding?.barChart?.data?.setValueTextSize(15f)
    }

    private fun plotDistance() {
        _plotTitle.value = "DISTANCE"
        dataVM.graphStartingSensor = Sensors.DISTANCE
        refreshGraph()
    }

    private fun plotCalories() {
        _plotTitle.value = "CALORIES"
        dataVM.graphStartingSensor = Sensors.CALORIES
        refreshGraph()
        binding?.barChart?.data?.setValueTextSize(15f)
    }

    private fun plotHRate() {
        _plotTitle.value = "HEART RATE"
        dataVM.graphStartingSensor = Sensors.H_RATE
        refreshGraph()
    }

    private fun changePlotTimescaleToWeekly() {
        dataVM.timeScale = TimeScale.WEEKLY
        refreshGraph()
    }

    private fun changePlotTimescaleToDaily() {
        dataVM.timeScale = TimeScale.DAILY
        refreshGraph()
    }

    private fun refreshGraph() {
        Log.i("GRAPHFRAGMENT", "Starting Sensor: ${dataVM.graphStartingSensor}")
        barChart?.data = viewmodel.getBarChartData(dataVM.graphStartingSensor, dataVM.timeScale)
        setBarChartXAxis()
        binding?.barChart?.barData?.setValueTextSize(24f)
        barChart?.notifyDataSetChanged()
        barChart?.invalidate();
        barChart?.animateY(500)
    }

    fun navigateUp() {
        findNavController().navigateUp()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.i("GRAPHFRAG", "${parent?.getItemAtPosition(position)} was selected.")
        val itemSelected = parent?.getItemAtPosition(position)
        if(parent?.count == 2) {
            when(itemSelected) {
                "WEEKLY" -> changePlotTimescaleToWeekly()
                "DAILY" -> changePlotTimescaleToDaily()
                else -> Log.e("GRAPHFRAG", "Error occurred during graph data spinner selection")
            }
        } else {
            when(itemSelected) {
                "HEART RATE" -> plotHRate()
                "BLOOD PRESSURE" -> plotBPressure()
                "BLOOD OXYGEN" -> plotBOxygen()
                "STEPS" -> plotSteps()
                "DISTANCE" -> plotDistance()
                "CALORIES" -> plotCalories()
                else -> Log.e("GRAPHFRAG", "Error occurred during graph data spinner selection")
            }
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i("GRAPHFRAG", "Nothing was selected.")
    }

    private fun setBarData() {
        setBarChartProperties()
        barChart?.data = viewmodel.getBarChartData(dataVM.graphStartingSensor, dataVM.timeScale)
        setBarChartXAxis()
    }

    private fun setBarChartProperties() {
        barChart?.setDrawBarShadow(false)
        barChart?.setDrawValueAboveBar(true)
        barChart?.description?.isEnabled = false
        barChart?.setPinchZoom(false)
        barChart?.legend?.isEnabled = false
        barChart?.invalidate()
        barChart?.animateY(500)
        barChart?.setDrawGridBackground(false)
        barChart?.setExtraOffsets(5f, 0f, 5f, 20f)
        barChart?.setDrawBorders(true)
        barChart?.setBorderWidth(3f)
        barChart?.setBorderColor(Color.BLACK)
    }

    private fun setBarChartXAxis() {
        val xAxis = barChart?.xAxis
        // x-axis formatter needs a list of strings to represent the days of the week
        val aAxisLabels: ArrayList<String> = when (dataVM.timeScale) {
            TimeScale.WEEKLY -> createWeeklyXAxisLabels()
            TimeScale.DAILY -> createDailyXAxisLabels()
            else -> ArrayList()
        }
        xAxis?.valueFormatter = IndexAxisValueFormatter(aAxisLabels)
        xAxis?.textColor = Color.BLACK
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        xAxis?.textSize = 20f
        xAxis?.yOffset = 10f
        xAxis?.setDrawGridLines(true)

        setDefaultXAxisProperties()
        val yAxisLeft = barChart?.axisLeft
        val yAxisRight = barChart?.axisRight
        when(dataVM.graphStartingSensor) {
            Sensors.H_RATE -> {
                yAxisLeft?.spaceTop = 250f
                yAxisRight?.spaceTop = 250f
            }
            Sensors.B_PRESSURE -> {
                yAxisLeft?.spaceTop = 250f
                yAxisRight?.spaceTop = 250f
            }
            Sensors.B_OXYGEN -> {
                yAxisLeft?.spaceTop = 300f
                yAxisRight?.spaceTop = 300f
            }
            else -> {
                yAxisLeft?.spaceMax = 20f
                yAxisRight?.spaceMax = 20f
                Log.i("FULLRANGE", "Full range: ${yAxisRight?.mAxisRange}")
            }
        }
    }

    private fun setDefaultXAxisProperties() {
        // y-axis left side
        with(barChart?.axisLeft) {
            this?.textColor = Color.BLACK
            this?.textSize = 14f
            this?.axisMinimum = 0f
            this?.setDrawGridLines(true)
        }
        // y-axis right side
        with(barChart?.axisRight) {
            this?.textColor = Color.BLACK
            this?.textSize = 14f
            this?.axisMinimum = 0f
            this?.setDrawGridLines(false)
        }
    }

    private fun createWeeklyXAxisLabels(): ArrayList<String> {
        val weekdays = ArrayList<String>()
        val currentDateTime = LocalDateTime.now()
        var xAxisDayOfWeek = currentDateTime.with(TemporalAdjusters.previous(DayOfWeek.of(currentDateTime.dayOfWeek.value)))
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
        return weekdays
    }

    private fun createDailyXAxisLabels(): ArrayList<String> {
        val weekdays = ArrayList<String>()
        val currentDateTime = LocalDateTime.now()
        var currentHour = currentDateTime.with(DayOfWeek.of(currentDateTime.dayOfWeek.value).minus(1))
        for (i in 0..7) {
            currentHour = currentHour.plusHours(3)
            when (currentHour.hour) {
                in 0..2 -> weekdays.add("12-3")
                in 3..5 -> weekdays.add("3-6")
                in 6..8 -> weekdays.add("6-9")
                in 9..11 -> weekdays.add("9-12")
                in 12..14 -> weekdays.add("12-3")
                in 15..17 -> weekdays.add("3-6")
                in 18..20 -> weekdays.add("6-9")
                in 21..23 -> weekdays.add("9-12")
            }
        }
        return weekdays
    }
}



package com.example.healthring.analytics
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.GraphFragmentBinding
import com.example.healthring.model.DataViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate

class GraphFragment: Fragment(R.layout.graph_fragment) {

    private var binding : GraphFragmentBinding? = null
    private val dataVM: DataViewModel by activityViewModels()

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
    }
}

@BindingAdapter("android:setPieData")
fun setLineGraphData(chart: LineChart, string: String) {
    Log.i("GRAPHFRAG", string)
    chart.description.isEnabled = false
    chart.setTouchEnabled(true)
    chart.dragDecelerationFrictionCoef = 0.9f

    chart.isDragEnabled = true
    chart.setScaleEnabled(true)
    chart.setDrawGridBackground(false)
    chart.isHighlightPerDragEnabled = true

    chart.setBackgroundColor(Color.WHITE)
    chart.setViewPortOffsets(0f, 0f, 0f, 0f)

    val values = listOf(
        Entry(1f, 10f),
        Entry(2f, 12f),
        Entry(3f, 4f),
        Entry(4f, 7f),
        Entry(5f, 2f)
    )

    val set1 = LineDataSet(values, "DataSet 1")
    set1.axisDependency = YAxis.AxisDependency.LEFT;
    set1.color = ColorTemplate.getHoloBlue();
    set1.valueTextColor = ColorTemplate.getHoloBlue();
    set1.lineWidth = 1.5f;
    set1.setDrawCircles(false);
    set1.setDrawValues(false);
    set1.fillAlpha = 65;
    set1.fillColor = ColorTemplate.getHoloBlue();
    set1.highLightColor = Color.rgb(244, 117, 117);
    set1.setDrawCircleHole(false);

    val data = LineData(set1)
    data.setValueTextColor(Color.WHITE);
    data.setValueTextSize(9f);

    chart.data = data
}
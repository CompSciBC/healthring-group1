package com.example.healthring.healthmonitor

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.databinding.HealthMonitorFragmentBinding
import com.example.healthring.model.DataViewModel
import com.example.healthring.model.EmailData
import com.example.healthring.model.Sensors
import com.google.android.play.core.internal.t
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.lang.Thread.sleep


class HealthMonitorFragment : Fragment(R.layout.health_monitor_fragment){

    private var binding : HealthMonitorFragmentBinding? = null
    private val dataVM: DataViewModel by activityViewModels()
    private val viewModel: HealthMonitorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = HealthMonitorFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            // pass instance of the viewmodel and HealthMonitorFragment
            healthMonitorFragment = this@HealthMonitorFragment
            dataViewModel = dataVM
            healthMonitorViewModel = viewModel
        }
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }
        if(!dataVM.updatingSensors) {
            GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                dataVM.updateSensorValues()
            }
            dataVM.updatingSensors = true
        }
        if(!dataVM.grabbedWeeklyData) {
            dataVM.setLoadingGraphAsTrue()
            GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                dataVM.asyncGrabReportData(Sensors.H_RATE,"week")
            }
            dataVM.grabbedWeeklyData = true
        }
        dataVM.heart_rate.observe(viewLifecycleOwner, Observer { if(dataVM.enableEmailNotifications.value!! == true && dataVM.heart_rate.value!! > 150) {
            Log.d("Observe:", "Test 1")
            val email = Amplify.Auth.currentUser.username
            var title = ""
            var body = ""
            if(dataVM.emailHeartRate.value!! == true) {
                val newTitle = title.plus("Critical Heart Rate Alert")
                title = newTitle
                val newBody = body.plus("Your heart rate is critically high at ${dataVM.heart_rate.value}bpm. ")
                body = newBody
                Log.i("TestPrint_Title", title)
                Log.i("TestPrint_Body", body)
            }
            if(dataVM.emailBloodPressure.value!! == true && dataVM.blood_pressure.value!! >= 150) {
                if (title.isEmpty()) {
                    val newTitle = title.plus("Critical Blood Pressure Alert")
                    title = newTitle
                }
                val newBody = body.plus("Your blood pressure is critically high at ${dataVM.blood_pressure.value}mmHg. ")
                body = newBody
                Log.i("TestPrint_Title", title)
                Log.i("TestPrint_Body", body)
            }
            if(dataVM.emailBloodOxygen.value!! == true && dataVM.blood_oxygen.value!! < 70) {
                if (title.isEmpty()) {
                    val newTitle = title.plus("Critical Blood Oxygen Alert")
                    title = newTitle
                }
                val newBody = body.plus("Your blood oxygen is critically low at ${dataVM.blood_oxygen.value}%.")
                body = newBody
                Log.i("TestPrint_Title", title)
                Log.i("TestPrint_Body", body)
            }
            if(title.isNotEmpty()) {
                val emailData = EmailData(email, title, body)
                GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                    dataVM.postEmailNotification(emailData)
                    Log.d("Post Email Call", "${emailData.title}")
                }
            }
        }
        })

//        if(dataVM.enableEmailNotifications.value == true) {
//            Log.d("Post Email Call", "Inside HealthFragment")
//            val email = Amplify.Auth.currentUser.username
//            val title = ""
//            val body = ""
//            if(dataVM.emailHeartRate.value == true && dataVM.heart_rate.value!! >= 150) {
//                title.plus("Critical HeartRate Alert")
//                body.plus("Your heart rate is critically high at ${dataVM.heart_rate.value}bpm.\n")
//            }
//            if(dataVM.emailBloodPressure.value == true && dataVM.blood_pressure.value!! >= 150) {
//                if(title.isEmpty()) { title.plus("Critical BloodPressure Alert") }
//                body.plus("Your blood pressure is critically high at ${dataVM.blood_pressure.value}mmHg.\n")
//            }
//            if(dataVM.emailBloodOxygen.value == true && dataVM.blood_oxygen.value!! < 70) {
//                if(title.isEmpty()) { title.plus("Critical BloodPressure Alert") }
//                body.plus("Your blood oxygen is critically low at ${dataVM.blood_oxygen.value}%.")
//            }
//            if(title.isNotEmpty()) {
//                val emailData = EmailData(email, title, body)
//                GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//                    dataVM.postEmailNotification(emailData)
//                    Log.d("Post Email Call", "${emailData.title}")
//                }
//            }
//        }
    }

    fun goToFitnessFragment() {
        findNavController().navigate(R.id.action_healthMonitorFragment_to_fitnessTrackerFragment)
    }

    fun goToTaskFragment() {
        findNavController().navigate(R.id.action_healthMonitorFragment_to_todaysTasksFragment2)
    }

    fun goToProfileFragment() {
        findNavController().navigate(R.id.action_healthMonitorFragment_to_profileFragment)
    }

    fun goToSettingsFragment() {
        findNavController().navigate(R.id.action_healthMonitorFragment_to_settingsFragment)
    }

    fun goToHeartRateGraph() {
        getReportAndGoToGraph(Sensors.H_RATE)
    }

    fun goToBloodPressureGraph() {
        dataVM.graphStartingSensor = Sensors.B_PRESSURE
        getReportAndGoToGraph(Sensors.B_PRESSURE)
    }

    fun goToBloodOxygenGraph() {
        getReportAndGoToGraph(Sensors.B_OXYGEN)
    }

    private fun getReportAndGoToGraph(sensor: Sensors) {
        GlobalScope.launch(Dispatchers.IO) {
            dataVM.asyncGrabReportData(sensor,"day")
            GlobalScope.launch(Dispatchers.Main) {
                findNavController().navigate(R.id.action_healthMonitorFragment_to_graphFragment)
            }
        }
    }

    override fun onDestroy() {
        Log.i("HEALTHFRAGMENT", "Fragment Destroyed.")
        super.onDestroy()
    }

    override fun onPause() {
        Log.i("HEALTHFRAGMENT", "Fragment paused.")
        super.onPause()
    }

    override fun onResume() {
        Log.i("HEALTHFRAGMENT", "Fragment Resumed.")
        hideOrShowSensorColorBubble()
        super.onResume()
    }

    private fun hideOrShowSensorColorBubble() {
        if(dataVM.disableSensorColors.value!!) {
            binding?.sensorColorBubbles?.visibility = View.INVISIBLE
        } else {
            binding?.sensorColorBubbles?.visibility = View.VISIBLE
        }
    }

    fun updateHeartRateColor() {
        binding?.heartRateTracker?.textSize = dataVM.sensorsTextSize.value!!
        binding?.heartRateTrackerTitle?.textSize = dataVM.sensorTitlesTextSize.value!!
        if(dataVM.disableSensorColors.value!!) {
            viewModel.hRateColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.secondary_sesnor_background)
            return
        }
        if(dataVM.heart_rate.value!! < 100) {
            viewModel.hRateColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.blue_border)
        } else if ((dataVM.heart_rate.value!! < 125)) {
            viewModel.hRateColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.green_border)
        } else if ((dataVM.heart_rate.value!! < 150)) {
            viewModel.hRateColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.amber_border)
        } else {
            viewModel.hRateColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.red_border)
        }
    }

    fun updateBloodPressureColor() {
        binding?.bloodPressureTracker?.textSize = dataVM.sensorsTextSize.value!!
        binding?.bloodPressureTrackerTitle?.textSize = dataVM.sensorTitlesTextSize.value!!
        if(dataVM.disableSensorColors.value!!) {
            viewModel.bPressureColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.secondary_sesnor_background)
            return
        }
        if (dataVM.blood_pressure.value!! < 120) {
            viewModel.bPressureColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.blue_border)
        } else if ((dataVM.blood_pressure.value!! < 135)) {
            viewModel.bPressureColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.green_border)
        } else if ((dataVM.blood_pressure.value!! < 150)) {
            viewModel.bPressureColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.amber_border)
        } else {
            viewModel.bPressureColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.red_border)
        }
    }

    fun updateBloodOxygenColor() {
        binding?.bloodOxygenTracker?.textSize = dataVM.sensorsTextSize.value!!
        binding?.bloodOxygenTrackerTitle?.textSize = dataVM.sensorTitlesTextSize.value!!
        if(dataVM.disableSensorColors.value!!) {
            viewModel.bOxygenColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.secondary_sesnor_background)
            return
        }
        if (dataVM.blood_oxygen.value!! < 70) {
            viewModel.bOxygenColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.red_border)
        } else if ((dataVM.blood_oxygen.value!! < 80)) {
            viewModel.bOxygenColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.amber_border)
        } else if ((dataVM.blood_oxygen.value!! < 90)) {
                viewModel.bOxygenColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.green_border)
        } else {
            viewModel.bOxygenColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.blue_border)
        }
    }

}
package com.example.healthring.model

import android.text.Editable
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.lang.Exception
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

var startTime: Long? = null

class DataViewModel: ViewModel() {
    private val _blood_oxygen = MutableLiveData<Double>(0.0)
    val blood_oxygen : LiveData<Double>
        get() = _blood_oxygen

    private val _blood_pressure = MutableLiveData<Double>(0.0)
    val blood_pressure : LiveData<Double>
        get() = _blood_pressure

    private val _calories = MutableLiveData<Int>(0)
    val calories : LiveData<Int>
        get() = _calories

    private val _distance = MutableLiveData<Double>(0.0)
    val distance : LiveData<Double>
        get() = _distance

    private val _heart_rate = MutableLiveData<Int>(0)
    val heart_rate : LiveData<Int>
        get() = _heart_rate

    private val _steps = MutableLiveData<Int>(0)
    val steps : LiveData<Int>
        get() = _steps

    private val _isLoadingGraphData = MutableLiveData(false)
    val isLoadingGraphData : LiveData<Boolean>
        get() = _isLoadingGraphData

    val disableSensorColors = MutableLiveData<Boolean>(false)
    val sensorsTextSize = MutableLiveData<Float>(55f)
    val sensorTitlesTextSize = MutableLiveData<Float>(24f)

    var sensorDataList: MutableList<SensorData>? = null
    // default graph sensor
    var graphStartingSensor: Sensors = Sensors.H_RATE
    var updatingSensors: Boolean = false
    var grabbedWeeklyData: Boolean = false

    private var _token: String? = null
    private var responseCount = 0

    private fun updateIdToken() {
        Amplify.Auth.fetchAuthSession(
            {
                val session = it as AWSCognitoAuthSession
                when (session.identityId.type) {
                    AuthSessionResult.Type.SUCCESS ->
                        setIdToken(session.userPoolTokens.value?.idToken.toString())
                    AuthSessionResult.Type.FAILURE ->
                        Log.w("AuthQuickStart", "Not signed in.", session.identityId.error)
                }
            },
            { Log.e("AuthQuickStart", "Failed to fetch session", it) }
        )
    }

    private fun setIdToken(token: String?) {
        if (token != null) {
            _token = token
        } else {
            Log.e("DATAVIEWMODEL", "User Id Token is null.")
        }
    }

    private fun callDatabaseSensorData() {
        Log.i("RESPONSECOUNT", "Count: $responseCount")
        responseCount++
        updateIdToken()

        val endPath = "sensors"

        val url = urlBuilder(endPath)
        val client = OkHttpClient()
        val request = requestBuilder(url)
        try {
            val response = client.newCall(request).execute()
            val message = requestSuccess(response)
            if(message != null) {
                updateSensorValues(message)
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("DATAVIEWMODEL", "$e")
        }
    }

    private fun callDatabaseReportData(startingSensor: Sensors, time_scale: String = "week") {
        graphStartingSensor = startingSensor
        Log.i("RESPONSECOUNT", "Count: $responseCount")
        responseCount++
        updateIdToken()
        val endPath = "reports"
        val client = OkHttpClient()

        try {
            val url = urlBuilder(endPath, time_scale)
            val request = requestBuilder(url)
            val response = client.newCall(request).execute()
            val message = requestSuccess(response)
            if(message != null) {
                processReportData(message)
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("DATAVIEWMODEL", "$e")
        } catch (e: NullPointerException) {
            Log.e("DATAVIEWMODEL", "No username found")
        }
    }

    private fun requestBuilder(url: HttpUrl): Request {
        try {
            val request = Request.Builder()
                .url(url)
                .header("Authorization", _token!!)
                .build()
            Log.i("TOKEN ID:", _token!!)
            Log.i("REQUEST", request.toString())
            return request
        } catch (e: NullPointerException) {
            Log.e("DATAVIEWMODEL", "Null token was given to the request builder.")
        }
        // this request will always failed
        return Request.Builder().url(url).build()
    }

    private fun urlBuilder(endPath: String, time_scale: String = "week"): HttpUrl {
        val endpoint = "https://vu102pm7vg.execute-api.us-west-2.amazonaws.com/prod/${endPath}"
        return endpoint.toHttpUrl().newBuilder()
            .addQueryParameter("email", Amplify.Auth.currentUser.username)
            .addQueryParameter("time_scale", time_scale)
            .build()
    }

    private fun requestSuccess(response: Response): String? {
        if (response.code == 200) {
            var message = response.body?.string()
            message = message?.substring(1, message.length - 1)
            Log.i("DATAVIEWMODEL", "SUCCESS response message: $message")
            Log.i("DATAVIEWMODEL", "SUCCESS Receive Response At Millis: ${response.receivedResponseAtMillis - response.sentRequestAtMillis}")
            Log.i("DATAVIEWMODEL", "Response Status Code: " + response.code)
            return message
        } else {
            Log.e("DATAVIEWMODEL", "Data retrieval error. Status code: ${response.code}")
        }
        return null
    }

    private fun updateSensorValues(response: String?) {
        // convert json string to a map
        val sensorData = Gson().fromJson(response, SensorData::class.java)
        Log.i("DATAVIEWMODEL", "Sensor Data: ${sensorData}}")
        // update sensor values
        _blood_pressure.postValue(sensorData.blood_pressure)
        _blood_oxygen.postValue(sensorData.blood_oxygen)
        _calories.postValue(sensorData.calories_burnt.toInt())
        _distance.postValue(sensorData.distance)
        _heart_rate.postValue(sensorData.heart_rate.toInt())
        _steps.postValue(sensorData.steps.toInt())
        Log.i("DATAVIEWMODEL", "Current heart rate: ${heart_rate.value}")
    }

    private fun processReportData(response: String?) {
        Log.i("DATAVIEWMODEL", "isLoading: ${isLoadingGraphData.value}")
        // response will most likely be a string of comma-separated JSON objects
        val sensorDataArray: MutableList<String> = response
            ?.split(Regex("\\}, \\{"))!!
            .map { it -> it.trim() }
            .toMutableList()
        // separate the JSON objects in preparation for converting them to SensorData objects
        for (i in sensorDataArray.indices) {
            if (i != 0 && i != sensorDataArray.size-1) {
                sensorDataArray[i] = "{" + sensorDataArray[i] + "}"
            } else if (i != 0) {
                sensorDataArray[i] = "{" + sensorDataArray[i]
            } else {
                sensorDataArray[i] = sensorDataArray[i] + "}"
            }
        }
        // fills the sensorDataList with new records
        createReportDataList(sensorDataArray)
//        Log.i("DATAVIEWMODEL", "${sensorDataList.size} data points collected.")
//        Log.i("DATAVIEWMODEL", "${sensorDataList.slice(1..100).map { it.date }} ")
    }

    // returns a list of SensorData objects pertaining to the requested report
    private fun createReportDataList(data: List<String>) {
        if (sensorDataList == null) {
            sensorDataList = emptyList<SensorData>().toMutableList()
        }
        // convert String JSON to SensorData type
        for (i in data.indices) {
            val sensorData = Gson().fromJson(data[i], SensorData::class.java)
            val dateAsEpoch = sensorData.date.toLong()
            // get LocalDateTime from epoch taken from the database
            val localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dateAsEpoch * 1000),
                ZoneId.systemDefault()
            )
            sensorData.date = localDateTime.toString()
            sensorDataList!!.add(sensorData)
        }
        sensorDataList?.sortBy { it.date }
        // get rid of duplicates
        sensorDataList?.distinctBy { it.date }
        Log.i("DATAVIEWMODEL", "Graph Data Downloaded and Process, Time Elapsed: " +
                "${(System.nanoTime() - startTime!!) / 1000000000f} seconds")
        CoroutineScope(Dispatchers.Main).launch() {
            _isLoadingGraphData.value = false
        }
    }

    suspend fun updateSensorValues() {
        suspend fun fetchData() =
            coroutineScope {
                while(true) {
                    Thread.sleep(500)
                    val grabData = async { callDatabaseSensorData() }
                    grabData.await()
                }
            }
        fetchData()
    }

    suspend fun asyncGrabReportData(startingSensor: Sensors, time_scale: String) {
        // need to be wait until runCallDatabase completes before creating/resuming the graph fragment
        startTime = System.nanoTime()
        suspend fun fetchData() =
            coroutineScope {
                val grabData = async { callDatabaseReportData(startingSensor, time_scale) }
                Log.i("HEALTHFRAGMENT", "Started grabbing report data")
                grabData.await()
                Log.i("HEALTHFRAGMENT", "Finished grabbing report data")
            }
        fetchData()
    }

    fun setLoadingGraphAsTrue() {
        _isLoadingGraphData.value = true
    }

}
package com.example.healthring.model

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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

    var sensorDataList: MutableList<SensorData>? = null
    // default graph sensor
    var graphStartingSensor: Sensors = Sensors.H_RATE

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

    fun callDatabase(endPath: String, isSensorUpdate: Boolean) {
        // TODO: make less calls to updateIdToken
        Log.i("RESPONSECOUNT", "Count: $responseCount")
        responseCount++
        updateIdToken()
        val endpoint = "https://vu102pm7vg.execute-api.us-west-2.amazonaws.com/prod/${endPath}"
        val client = OkHttpClient()

        val url: HttpUrl = endpoint.toHttpUrl().newBuilder()
            .addQueryParameter("email", Amplify.Auth.currentUser.username)
            .build()

        val request = requestBuilder(url)

        try {
            val response = client.newCall(request).execute()

            if (response.code == 200) {
                var message = response.body?.string()
                message = message?.substring(1, message.length - 1)

                if (isSensorUpdate) {
                    updateSensorValues(message)
                } else {
                    processReportData(message)
                }
                Log.i("DATAVIEWMODEL", "SUCCESS response message: $message")
                Log.i("DATAVIEWMODEL", "SUCCESS Receive Response At Millis: ${response.receivedResponseAtMillis - response.sentRequestAtMillis}")
                Log.i("DATAVIEWMODEL", "Response Status Code: " + response.code)
            } else {
                Log.e("DATAVIEWMODEL", "Data retrieval error. Status code: ${response.code}")
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("DATAVIEWMODEL", "$e")
        }
    }

    fun runCallDatabase(endPath: String, isSensorUpdate: Boolean): Boolean {
        // database call can't happen in the main thread
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    callDatabase(endPath, isSensorUpdate)
                } catch (e: InterruptedException) {
                    Log.e("DATAVIEWMODEL", "Thread went haywire, ${e.message}")
                }
            }
        }
        thread.start()
        // don't have to wait for database calls for sensor updates
//        if (!isSensorUpdate) {
//            thread.join()
//        }
        thread.join()
        Log.i("DATAVIEWMODEL", "Thread-${thread.id} Finished.")
        return true
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
        // response will most likely be a string of comma-separated JSON objects
        // TODO: get rid of assertion
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

        sensorDataList = createReportDataList(sensorDataArray)
//        Log.i("DATAVIEWMODEL", "${sensorDataList.size} data points collected.")
//        Log.i("DATAVIEWMODEL", "${sensorDataList.slice(1..100).map { it.date }} ")
    }

    // returns a list of SensorData objects pertaining to the requested report
    private fun createReportDataList(data: List<String>): MutableList<SensorData> {
        val sensorDataList = emptyList<SensorData>().toMutableList()
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
            sensorDataList.add(sensorData)
        }
        sensorDataList.sortBy { it.date }
        return sensorDataList
    }

    private suspend fun asyncGrabReportData() {
        // need to be wait until runCallDatabase completes before creating/resuming the graph fragment
        suspend fun fetchData() =
            coroutineScope {
                val grabData = async { runCallDatabase("reports", false) }
                grabData.await()
            }
        fetchData()
    }

    fun getReportData(startingSensor: Sensors) {
        graphStartingSensor = startingSensor
        // call the suspend function that runs a DynamoDB scan operation for report data
        viewModelScope.launch {
            asyncGrabReportData()
        }
    }

}
package com.example.healthring.model

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.lang.NullPointerException

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

    private var _token: String? = null

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

    private fun callDatabase() {
        // TODO: make less calls to updateIdToken
        updateIdToken()
        val endpoint = "https://vu102pm7vg.execute-api.us-west-2.amazonaws.com/prod/sensors"
        val client = OkHttpClient()

        val url: HttpUrl = endpoint.toHttpUrl().newBuilder()
            .addQueryParameter("email", "alex@filbert.com")
            .build()

        var request: Request? = null
        try {
            request = Request.Builder()
                .url(url)
                .header("Authorization", _token!!)
                .build()
            Log.i("TOKEN ID:", _token!!)
            Log.i("REQUEST", request.toString())
        } catch (e: NullPointerException) {
            Log.e("DATAVIEWMODEL", "Null token was given to the request builder.")
        }

        val response = client.newCall(request!!).execute()
        Log.i("DATAVIEWMODEL", "Message after substring: ${response}")
        if (response.code == 200) {
            var message = response.body?.string()
            message = message?.substring(1, message.length - 1)

            updateSensorValues(message)
            Log.i("DATAVIEWMODEL", "SUCCESS Message: $message")
            Log.i("DATAVIEWMODEL", "SUCCESS Receive Response At Millis: ${response.receivedResponseAtMillis - response.sentRequestAtMillis}")
            Log.i("DATAVIEWMODEL", "SUCCESS: " + response.code)
        } else {
            Log.e("DATAVIEWMODEL", "Data retrieval error. Status code: ${response.code}")
        }
    }

    fun runCallDatabase() {
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    callDatabase()
                } catch (e: InterruptedException) {
                }
            }
        }
        thread.start()
    }

    private fun updateSensorValues(response: String?) {
        // convert json string to a map
        val sensorData = Gson().fromJson(response, SensorData::class.java)
        Log.i("DATAVIEWMODEL", "Sensor Data: ${sensorData}}")
        // update sensor values
        _blood_pressure.postValue(sensorData.blood_oxygen)
        _blood_oxygen.postValue(sensorData.blood_oxygen)
        _calories.postValue(sensorData.calories_burnt.toInt())
        _distance.postValue(sensorData.distance)
        _heart_rate.postValue(sensorData.heart_rate.toInt())
        _steps.postValue(sensorData.steps.toInt())
        Log.i("DATAVIEWMODEL", "Current heart rate: ${heart_rate.value}")
    }

}
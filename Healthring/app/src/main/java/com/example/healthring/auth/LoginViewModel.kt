package com.example.healthring.auth

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel(){

    private val _email = MutableLiveData<String>()
    val email : LiveData<String>
        get() = _email

    private val _password = MutableLiveData<String>()
    val password : LiveData<String>
        get() = _password

    fun afterUserChangeEmail(s: Editable) {
        _email.value = s.toString()
    }

    fun afterUserChangePassword(s: Editable) {
        _password.value = s.toString()
    }
}
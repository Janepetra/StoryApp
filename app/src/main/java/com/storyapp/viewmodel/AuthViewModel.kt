package com.storyapp.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.storyapp.response.Login
import com.storyapp.response.Signup
import com.storyapp.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel (val context: Context): ViewModel() {

    private val _login = MutableLiveData<Login>()
    val login: LiveData<Login> = _login

    private val _signup = MutableLiveData<Signup>()
    val signup: LiveData<Signup> = _signup

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _messageLogin = MutableLiveData<String>()
    val messageLogin: LiveData<String> = _messageLogin

    var error: Boolean = false


    companion object {
        private const val TAG = "AuthViewModel"
    }

    fun getLogin (email: String, password: String) {
        val client = ApiConfig.getApiService().getLogin(email, password)
        client.enqueue(object: Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    error = false
                    response.body()?.let {
                        _login.value = it
                    }
                } else {
                    error = true
                    when (response.code()) {
                        400 -> _messageLogin.value = ("Invalid Email format")
                        401 -> _messageLogin.value = ("Wrong email or password")
                        else -> Log.e(TAG, "Error Message: ${response.message()}")
                    }
                }
            }
            override fun onFailure(call: Call<Login>, t: Throwable) {
                error = true
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getSignup (name: String, email: String, password: String) {
        val client = ApiConfig.getApiService().getSignup(name, email, password)
        client.enqueue(object: Callback<Signup> {
            override fun onResponse(call: Call<Signup>, response: Response<Signup>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let {
                        _signup.value = it
                    }
                } else {
                    error = true
                    when (response.code()) {
                        400 -> _messageLogin.value = ("Invalid Email format")
                        401 -> _messageLogin.value = ("Wrong email or password")
                        else -> Log.e(TAG, "Error Message: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<Signup>, t: Throwable) {
                error = true
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }
}
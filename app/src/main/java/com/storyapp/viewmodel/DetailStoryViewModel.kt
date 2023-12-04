package com.storyapp.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.storyapp.R
import com.storyapp.response.AddStory
import com.storyapp.response.AllStories
import com.storyapp.response.DetailStory
import com.storyapp.response.Story
import com.storyapp.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DetailStoryViewModel(val context: Context): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _addStory = MutableLiveData<AddStory>()
    val addStory: LiveData<AddStory> = _addStory

    val listStory = MutableLiveData<List<Story>>()

    var error = MutableLiveData("")

    private val TAG = "DetailStoryViewModel"

    fun getListStory(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getListStory(token, 30)
        client.enqueue(object : Callback<AllStories> {
            override fun onResponse(call: Call<AllStories>, response: Response<AllStories>) {
                if (response.isSuccessful) {
                    listStory.postValue(response.body()?.listStory)
                } else {
                    error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<AllStories>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("${"error di get list story"} : ${t.message}")
            }
        })
    }

    fun addStory(token: String, image: File, description: String) {
        _isLoading.value = true
        "${image.length() / 1024 / 1024} MB" // manual parse from bytes to Mega Bytes
        //convert desc to requestBody format
        val storyDescription = description.toRequestBody("text/plain".toMediaType())
        //convert photo to requestBody format
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        //make multipartBody for photo
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            image.name,
            requestImageFile
        )
        val client = ApiConfig.getApiService().addStory(token, imageMultipart, storyDescription)
        client.enqueue(object : Callback<AddStory> {
            override fun onResponse(call: Call<AddStory>, response: Response<AddStory>) {
                when (response.code()) {
                    201 -> _addStory.value
                    else -> error.postValue("Error ${response.code()} : ${response.message()}")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<AddStory>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("${"error di add story"} : ${t.message}")
            }
        })
    }
}
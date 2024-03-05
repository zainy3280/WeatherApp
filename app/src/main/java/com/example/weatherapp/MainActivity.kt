package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log.d as d1

// api key :886705b4c1182eb1c69f28eb8c520e20
class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
          fetchweatherdata()
    }
    private fun fetchweatherdata(){
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
val response = retrofit.getWeatherdata("Delhi" ,"022749d254c0a06fed9d0428c5b6ceaa", "metric")
        response.enqueue(object : Callback<Weatherapp>{
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<Weatherapp>, response: Response<Weatherapp>) {
        val responsebody = response.body()
                if(response.isSuccessful&&responsebody!=null){
                    val temperature = responsebody.main.temp.toString()
                  //Log.d("abc" , "onResponse : $temperature")
                    Log.d("abcd", "$temperature")
                }

            }

            override fun onFailure(call: Call<Weatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }


        })
    }
}
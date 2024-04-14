package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

// api key :886705b4c1182eb1c69f28eb8c520e20
class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)

        fetchweatherdata("Gujar Khan") { sunset ->
            // Use sunset value to change background or perform other actions
            val cityTimeZone = "Asia/Karachi" // Set the correct timezone for the city
            changeBackgroundBySunsetTime(sunset, cityTimeZone)
        }


        //fetchweatherdata("Gujar Khan")
        searchcity()

    }

    private fun searchcity() {
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchweatherdata(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchweatherdata(cityname: String,onSunsetReceived: ((Long) -> Unit)? = null){
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
val response = retrofit.getWeatherdata(cityname ,"022749d254c0a06fed9d0428c5b6ceaa", "metric")
        response.enqueue(object : Callback<Weatherapp>{
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<Weatherapp>, response: Response<Weatherapp>) {
        val responsebody = response.body()
                if(response.isSuccessful&&responsebody!=null){
                    val temperature = responsebody.main.temp.toString()
                val humid = responsebody.main.humidity
                    val sunset = responsebody.sys.sunset.toLong()
                    val sunrise = responsebody.sys.sunrise.toLong()
                    val windspeed = responsebody.wind.speed
                    val tempmax = responsebody.main.temp_max
                    val tempmin = responsebody.main.temp_min
                    val seaLevel = responsebody.main.pressure

                    val conditions = responsebody.weather.firstOrNull()?.main?:"uknown"

                    binding.humidity.text = "$humid %"
                    binding.temp.text = "$temperature °C"
                    binding.weather.text = conditions
                    binding.maxTemp.text = "MAX TEMP $tempmax °C "
                    binding.minTemp.text = "MIN TEMP $tempmin °C "
                    binding.windspeed.text = "$windspeed m/s"
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.condition.text = conditions
                    binding.day.text = dayname(System.currentTimeMillis())
                        binding.date.text =date()
                        binding.cityName.text = "$cityname"
                    changeimagebycondtion(conditions)
                    if (onSunsetReceived != null) {
                        onSunsetReceived(sunset)
                    }






                // Log.d("abc" , "onResponse : $temperature")

                }

            }

            override fun onFailure(call: Call<Weatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }


        })

    }

    private fun changeimagebycondtion(conditionS :String) {
when (conditionS){
    "Haze","Partly Cloudy","Clouds","Overcast","Mist","Foggy","Smoke" ->{
        binding.root.setBackgroundResource(R.drawable.colud_background)
        binding.lottieAnimationView.setAnimation(R.raw.cloud)
    }
    "Clear Sky","Sunny","Clear"->{
        binding.root.setBackgroundResource(R.drawable.sunny_background)
        binding.lottieAnimationView.setAnimation(R.raw.sun)
    }
    "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain","Rain","Thunderstorm"->{
        binding.root.setBackgroundResource(R.drawable.rain_background)
        binding.lottieAnimationView.setAnimation(R.raw.rain)
    }
    "Light Snow ","Moderate Snow ","Heavy Snow","BLizzard"->{
        binding.root.setBackgroundResource(R.drawable.snow_background)
        binding.lottieAnimationView.setAnimation(R.raw.snow)
    }
    else ->{
    binding.root.setBackgroundResource(R.drawable.sunny_background)
    binding.lottieAnimationView.setAnimation(R.raw.sun)
}
}
        binding.lottieAnimationView.playAnimation()
    }

    fun dayname(timestamp:Long) :String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())


    }


    private fun date():String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())

    }   private fun time(timestamp:Long):String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))

    }
    // Now modify changeBackgroundBySunsetTime to accept sunsetTime as a parameter
    @RequiresApi(Build.VERSION_CODES.O)
    private fun changeBackgroundBySunsetTime(sunsetTime: Long, cityTimeZone: String) {
        val currentTime = System.currentTimeMillis()

        // Convert sunset time to local time of the city
        val sunsetLocalTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(sunsetTime * 1000), ZoneId.of(cityTimeZone))
        val currentTimeLocal = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.of(cityTimeZone))

        // Check if current time is before or after sunset time
        if (currentTimeLocal.isBefore(sunsetLocalTime)) {
            // Set background for before sunset
            binding.root.setBackgroundResource(R.drawable.sunny_background)
        } else {
            // Set background for after sunset
            binding.root.setBackgroundResource(R.drawable.nightbackground)
        }
    }


}
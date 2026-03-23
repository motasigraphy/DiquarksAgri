package com.diquarks.diquarksagri

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.diquarks.diquarksagri.network.RetrofitInstance
import com.diquarks.diquarksagri.network.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private val apiKey = "c115674ab27a4e9a916212605261203"

    private lateinit var tempText: TextView
    private lateinit var weatherText: TextView
    private lateinit var detailsText: TextView
    private lateinit var locationText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tempText = findViewById(R.id.tempText)
        weatherText = findViewById(R.id.weatherText)
        detailsText = findViewById(R.id.detailsText)
        locationText = findViewById(R.id.locationText)

        // 🔥 API
        getWeather("Rabat")

        // 🔥 BUTTON (Commencer)
        val btn = findViewById<MaterialButton>(R.id.btnCommencer)

        btn.setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
        }
    }

    private fun getWeather(city: String) {

        RetrofitInstance.api
            .getWeather(apiKey, city)
            .enqueue(object : Callback<WeatherResponse> {

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {

                    if (response.isSuccessful) {

                        val data = response.body()

                        if (data != null) {

                            val temp = data.current.temp_c
                            val condition = data.current.condition.text
                            val humidity = data.current.humidity
                            val wind = data.current.wind_kph
                            val cityName = data.location.name

                            runOnUiThread {
                                tempText.text = "${temp.toInt()}°C"
                                weatherText.text = condition
                                locationText.text = cityName
                                detailsText.text =
                                    "Humidity: $humidity% • Wind: $wind km/h"
                            }
                        }

                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "API Error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Toast.makeText(
                        this@HomeActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
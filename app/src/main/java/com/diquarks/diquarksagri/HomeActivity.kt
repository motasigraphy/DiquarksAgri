package com.diquarks.diquarksagri

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.button.MaterialButton
import com.diquarks.diquarksagri.network.RetrofitInstance
import com.diquarks.diquarksagri.network.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_CODE = 100

    private lateinit var txtTemperature: TextView
    private lateinit var txtWeatherDesc: TextView
    private lateinit var txtRecommendations: TextView
    private lateinit var txtDetails: TextView
    private lateinit var txtCity: TextView
    private lateinit var imgWeatherIcon: ImageView

    // API KEY الجديد
    private val apiKey = "c115674ab27a4e9a916212605261203"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        txtTemperature = findViewById(R.id.txtTemperature)
        txtWeatherDesc = findViewById(R.id.txtWeatherDesc)
        txtRecommendations = findViewById(R.id.txtRecommendations)
        txtDetails = findViewById(R.id.txtDetails)
        txtCity = findViewById(R.id.txtCity)
        imgWeatherIcon = findViewById(R.id.imgWeatherIcon)

        val btn = findViewById<MaterialButton>(R.id.btnCommencer)
        btn.setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
        }

        checkLocationPermission()
    }

    // ================= LOCATION =================

    private fun checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )

        } else {

            getLocation()

        }
    }

    private fun getLocation() {

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->

                if (location != null) {

                    val locationQuery =
                        "${location.latitude},${location.longitude}"

                    getWeather(locationQuery)

                } else {

                    txtWeatherDesc.text = "Location unavailable"

                }
            }
    }

    // ================= WEATHER =================

    private fun getWeather(location: String) {

        RetrofitInstance.api
            .getWeather(apiKey, location)
            .enqueue(object : Callback<WeatherResponse> {

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        val weather = response.body()!!

                        val temperature = weather.current.temp_c
                        val description = weather.current.condition.text
                        val humidity = weather.current.humidity
                        val wind = weather.current.wind_kph
                        val city = weather.location.name
                        val condition = weather.current.condition.text

                        txtTemperature.text = "${temperature.toInt()}°C"

                        txtWeatherDesc.text = description

                        txtCity.text = city

                        txtDetails.text =
                            "Humidité: $humidity% • Vent: $wind km/h"

                        txtRecommendations.text =
                            getRecommendations(condition)

                        updateWeatherIcon(condition)

                    } else {

                        txtWeatherDesc.text = "Weather error"

                    }
                }

                override fun onFailure(
                    call: Call<WeatherResponse>,
                    t: Throwable
                ) {

                    txtWeatherDesc.text = "Network error"

                }
            })
    }

    // ================= AI RECOMMENDATIONS =================

    private fun getRecommendations(weather: String): String {

        return when {

            weather.contains("rain", true) -> """
                • Éviter l’arrosage
                • Protéger les plantes
                • Vérifier le drainage
            """.trimIndent()

            weather.contains("sun", true) || weather.contains("clear", true) -> """
                • Arroser les plantes
                • Planter des légumes
                • Travailler le sol
            """.trimIndent()

            weather.contains("cloud", true) -> """
                • Planter des tomates
                • Récolter des fraises
                • Arroser le soir
            """.trimIndent()

            else -> """
                • Observer les plantes
                • Entretenir le jardin
            """.trimIndent()
        }
    }

    // ================= WEATHER ICON =================

    private fun updateWeatherIcon(condition: String) {

        when {

            condition.contains("sun", true) ||
                    condition.contains("clear", true) ->
                imgWeatherIcon.setImageResource(R.drawable.ic_sun)

            condition.contains("cloud", true) ->
                imgWeatherIcon.setImageResource(R.drawable.ic_cloud)

            condition.contains("rain", true) ->
                imgWeatherIcon.setImageResource(R.drawable.ic_rain)

            condition.contains("storm", true) ->
                imgWeatherIcon.setImageResource(R.drawable.ic_storm)

            else ->
                imgWeatherIcon.setImageResource(R.drawable.ic_cloud)
        }
    }

    // ================= PERMISSION RESULT =================

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == LOCATION_PERMISSION_CODE) {

            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                getLocation()

            } else {

                Toast.makeText(
                    this,
                    "Location permission required",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }
}
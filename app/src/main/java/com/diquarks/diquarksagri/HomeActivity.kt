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

    private val apiKey = "faf0bdd2459e7b141c8587bc71028bf5"

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

                    val latitude = location.latitude
                    val longitude = location.longitude

                    getWeather(latitude, longitude)

                } else {

                    txtWeatherDesc.text = "Location unavailable"

                }
            }
            .addOnFailureListener {

                txtWeatherDesc.text = "Location error"

            }
    }

    // ================= WEATHER =================

    private fun getWeather(lat: Double, lon: Double) {

        RetrofitInstance.api
            .getWeather(lat, lon, apiKey)
            .enqueue(object : Callback<WeatherResponse> {

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        val weather = response.body()!!

                        val temperature = weather.main.temp
                        val description = weather.weather[0].description
                        val condition = weather.weather[0].main
                        val humidity = weather.main.humidity
                        val windSpeed = weather.wind.speed
                        val city = weather.name

                        txtTemperature.text = "${temperature.toInt()}°C"

                        txtWeatherDesc.text =
                            description.replaceFirstChar { it.uppercase() }

                        txtCity.text = city

                        txtDetails.text =
                            "Humidité: $humidity% • Vent: $windSpeed m/s"

                        txtRecommendations.text =
                            getRecommendations(condition)

                        updateWeatherIcon(condition)

                    } else {

                        txtWeatherDesc.text = "Weather data error"

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

        return when (weather) {

            "Rain" -> """
                • Éviter l’arrosage
                • Protéger les plantes
                • Vérifier le drainage
            """.trimIndent()

            "Clear" -> """
                • Arroser les plantes
                • Planter des légumes
                • Travailler le sol
            """.trimIndent()

            "Clouds" -> """
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

        when (condition) {

            "Clear" ->
                imgWeatherIcon.setImageResource(R.drawable.ic_sun)

            "Clouds", "Smoke", "Haze", "Mist", "Fog" ->
                imgWeatherIcon.setImageResource(R.drawable.ic_cloud)

            "Rain" ->
                imgWeatherIcon.setImageResource(R.drawable.ic_rain)

            "Thunderstorm" ->
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
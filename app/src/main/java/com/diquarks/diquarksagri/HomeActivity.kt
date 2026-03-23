package com.diquarks.diquarksagri

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
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
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    // 🔥 APPLY LANGUAGE
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", "fr")

        val locale = Locale(lang!!)
        Locale.setDefault(locale)

        val config = newBase.resources.configuration
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    private val LOCATION_PERMISSION_CODE = 100

    private lateinit var txtTemperature: TextView
    private lateinit var txtWeatherDesc: TextView
    private lateinit var txtRecommendations: TextView
    private lateinit var txtDetails: TextView
    private lateinit var txtCity: TextView
    private lateinit var imgWeatherIcon: ImageView

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

                    val lat = location.latitude
                    val lon = location.longitude

                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(lat, lon, 1)

                        if (!addresses.isNullOrEmpty()) {

                            val cityName =
                                addresses[0].locality
                                    ?: addresses[0].subAdminArea
                                    ?: addresses[0].adminArea

                            if (cityName != null) {
                                txtCity.text = cityName
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val locationQuery = "$lat,$lon"
                    getWeather(locationQuery)

                } else {
                    txtWeatherDesc.text = "Location unavailable"
                }
            }
            .addOnFailureListener {
                txtWeatherDesc.text = "Location error"
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

                    if (response.isSuccessful) {

                        val weather = response.body()

                        if (weather != null) {

                            val temperature = weather.current.temp_c
                            val description = weather.current.condition.text
                            val humidity = weather.current.humidity
                            val wind = weather.current.wind_kph
                            val isDay = weather.current.is_day

                            txtTemperature.text = "${temperature.toInt()}°C"
                            txtWeatherDesc.text = description

                            txtDetails.text =
                                "Humidité: $humidity% • Vent: $wind km/h"

                            txtRecommendations.text =
                                getRecommendations(description)

                            updateWeatherIcon(description, isDay)
                        }

                    } else {
                        txtWeatherDesc.text = "API Error: ${response.code()}"
                    }
                }

                override fun onFailure(
                    call: Call<WeatherResponse>,
                    t: Throwable
                ) {
                    txtWeatherDesc.text = "Network error"

                    Toast.makeText(
                        this@HomeActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
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

            weather.contains("clear", true) -> """
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

    private fun updateWeatherIcon(condition: String, isDay: Int) {

        val night = isDay == 0

        if (night) {

            when {

                condition.contains("rain", true) ->
                    imgWeatherIcon.setImageResource(R.drawable.ic_rain_night)

                condition.contains("storm", true) ||
                        condition.contains("thunder", true) ->
                    imgWeatherIcon.setImageResource(R.drawable.ic_storm_night)

                condition.contains("cloud", true) ->
                    imgWeatherIcon.setImageResource(R.drawable.ic_cloud_night)

                else ->
                    imgWeatherIcon.setImageResource(R.drawable.ic_moon)
            }

        } else {

            when {

                condition.contains("rain", true) ->
                    imgWeatherIcon.setImageResource(R.drawable.ic_rain)

                condition.contains("storm", true) ||
                        condition.contains("thunder", true) ->
                    imgWeatherIcon.setImageResource(R.drawable.ic_storm)

                condition.contains("cloud", true) ->
                    imgWeatherIcon.setImageResource(R.drawable.ic_cloud)

                else ->
                    imgWeatherIcon.setImageResource(R.drawable.ic_sun)
            }
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
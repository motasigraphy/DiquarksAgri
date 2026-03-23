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
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

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

    private lateinit var tempText: TextView
    private lateinit var weatherText: TextView
    private lateinit var detailsText: TextView
    private lateinit var locationText: TextView
    private lateinit var weatherIcon: ImageView

    // 🔥 WEEKLY
    private lateinit var dayNames: Array<TextView>
    private lateinit var dayMins: Array<TextView>
    private lateinit var dayMaxs: Array<TextView>
    private lateinit var dayIcons: Array<ImageView>

    private val apiKey = "c115674ab27a4e9a916212605261203"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tempText = findViewById(R.id.tempText)
        weatherText = findViewById(R.id.weatherText)
        detailsText = findViewById(R.id.detailsText)
        locationText = findViewById(R.id.locationText)
        weatherIcon = findViewById(R.id.weatherIcon)

        // 🔥 WEEKLY INIT
        dayNames = arrayOf(
            findViewById(R.id.day1Name),
            findViewById(R.id.day2Name),
            findViewById(R.id.day3Name),
            findViewById(R.id.day4Name),
            findViewById(R.id.day5Name),
            findViewById(R.id.day6Name),
            findViewById(R.id.day7Name)
        )

        dayMins = arrayOf(
            findViewById(R.id.day1Min),
            findViewById(R.id.day2Min),
            findViewById(R.id.day3Min),
            findViewById(R.id.day4Min),
            findViewById(R.id.day5Min),
            findViewById(R.id.day6Min),
            findViewById(R.id.day7Min)
        )

        dayMaxs = arrayOf(
            findViewById(R.id.day1Max),
            findViewById(R.id.day2Max),
            findViewById(R.id.day3Max),
            findViewById(R.id.day4Max),
            findViewById(R.id.day5Max),
            findViewById(R.id.day6Max),
            findViewById(R.id.day7Max)
        )

        dayIcons = arrayOf(
            findViewById(R.id.day1Icon),
            findViewById(R.id.day2Icon),
            findViewById(R.id.day3Icon),
            findViewById(R.id.day4Icon),
            findViewById(R.id.day5Icon),
            findViewById(R.id.day6Icon),
            findViewById(R.id.day7Icon)
        )

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
                                locationText.text = cityName
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val locationQuery = "$lat,$lon"
                    getWeather(locationQuery)

                } else {
                    weatherText.text = "Location unavailable"
                }
            }
            .addOnFailureListener {
                weatherText.text = "Location error"
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

                            runOnUiThread {

                                tempText.text = "${temperature.toInt()}°C"
                                weatherText.text = description
                                detailsText.text =
                                    "Humidité: $humidity% • Vent: $wind km/h"

                                updateWeatherIcon(description, isDay)

                                // 🔥 WEEKLY
                                val forecastDays = weather.forecast.forecastday

                                for (i in forecastDays.indices) {

                                    val day = forecastDays[i]

                                    val date = day.date
                                    val min = day.day.mintemp_c.toInt()
                                    val max = day.day.maxtemp_c.toInt()
                                    val condition = day.day.condition.text

                                    val dayName = SimpleDateFormat("EEE", Locale.getDefault())
                                        .format(SimpleDateFormat("yyyy-MM-dd").parse(date)!!)

                                    dayNames[i].text = dayName
                                    dayMins[i].text = "$min°"
                                    dayMaxs[i].text = "$max°"

                                    when {
                                        condition.contains("rain", true) ->
                                            dayIcons[i].setImageResource(R.drawable.ic_rain)

                                        condition.contains("cloud", true) ->
                                            dayIcons[i].setImageResource(R.drawable.ic_cloud)

                                        else ->
                                            dayIcons[i].setImageResource(R.drawable.ic_sun)
                                    }
                                }
                            }
                        }

                    } else {
                        weatherText.text = "API Error: ${response.code()}"
                    }
                }

                override fun onFailure(
                    call: Call<WeatherResponse>,
                    t: Throwable
                ) {
                    weatherText.text = "Network error"

                    Toast.makeText(
                        this@HomeActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // ================= ICON =================

    private fun updateWeatherIcon(condition: String, isDay: Int) {

        val night = isDay == 0

        if (night) {

            when {
                condition.contains("rain", true) ->
                    weatherIcon.setImageResource(R.drawable.ic_rain_night)

                condition.contains("storm", true) ->
                    weatherIcon.setImageResource(R.drawable.ic_storm_night)

                condition.contains("cloud", true) ->
                    weatherIcon.setImageResource(R.drawable.ic_cloud_night)

                else ->
                    weatherIcon.setImageResource(R.drawable.ic_moon)
            }

        } else {

            when {
                condition.contains("rain", true) ->
                    weatherIcon.setImageResource(R.drawable.ic_rain)

                condition.contains("storm", true) ->
                    weatherIcon.setImageResource(R.drawable.ic_storm)

                condition.contains("cloud", true) ->
                    weatherIcon.setImageResource(R.drawable.ic_cloud)

                else ->
                    weatherIcon.setImageResource(R.drawable.ic_sun)
            }
        }
    }

    // ================= PERMISSION =================

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

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
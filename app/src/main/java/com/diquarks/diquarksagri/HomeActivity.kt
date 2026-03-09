package com.diquarks.diquarksagri

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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

    // 🔥 حط API KEY ديالك هنا
    private val apiKey = "faf0bdd2459e7b141c8587bc71028bf5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        txtTemperature = findViewById(R.id.txtTemperature)
        txtWeatherDesc = findViewById(R.id.txtWeatherDesc)

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

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (location != null) {
                getWeather(location.latitude, location.longitude)
            } else {
                txtWeatherDesc.text = "Location not available"
            }
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

                        txtTemperature.text = "${temperature.toInt()}°C"
                        txtWeatherDesc.text =
                            description.replaceFirstChar { it.uppercase() }

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
                    "Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
package com.diquarks.diquarksagri.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.diquarks.diquarksagri.HomeActivity
import com.diquarks.diquarksagri.R
import org.json.JSONObject
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {

        loadLocale()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val showPassword = findViewById<ImageView>(R.id.showPassword)
        val register = findViewById<TextView>(R.id.register)

        val btnAr = findViewById<TextView>(R.id.btnAr)
        val btnFr = findViewById<TextView>(R.id.btnFr)

        btnAr.setOnClickListener { changeLanguage("ar") }
        btnFr.setOnClickListener { changeLanguage("fr") }

        showPassword.setOnClickListener {
            passwordVisible = !passwordVisible

            passwordField.transformationMethod =
                if (passwordVisible)
                    HideReturnsTransformationMethod.getInstance()
                else
                    PasswordTransformationMethod.getInstance()

            passwordField.setSelection(passwordField.text.length)
        }

        loginButton.setOnClickListener {

            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_username), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "http://10.0.2.2/diquarks_api/login.php"

            val request = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val json = JSONObject(response)
                        val status = json.getString("status")
                        val message = json.getString("message")

                        if (status == "success") {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
                    }
                },
                {
                    Toast.makeText(this, "Connection error", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf(
                        "email" to email,
                        "password" to password
                    )
                }
            }

            Volley.newRequestQueue(this).add(request)
        }

        register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun changeLanguage(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)

        val prefs: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        prefs.edit().putString("lang", lang).apply()

        recreate()
    }

    private fun loadLocale() {
        val prefs: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val lang = prefs.getString("lang", "fr") ?: "fr"

        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
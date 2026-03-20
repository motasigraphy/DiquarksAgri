package com.diquarks.diquarksagri.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
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

        // 🔥 تحميل اللغة قبل الواجهة
        loadLocale()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val showPassword = findViewById<ImageView>(R.id.showPassword)
        val register = findViewById<TextView>(R.id.register)

        // 🔥 LANGUAGE BUTTONS
        val btnAr = findViewById<TextView>(R.id.btnAr)
        val btnFr = findViewById<TextView>(R.id.btnFr)

        btnAr.setOnClickListener { setLocale("ar") }
        btnFr.setOnClickListener { setLocale("fr") }

        // 👁 SHOW PASSWORD
        showPassword.setOnClickListener {
            if (passwordVisible) {
                passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordVisible = false
            } else {
                passwordField.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordVisible = true
            }
            passwordField.setSelection(passwordField.text.length)
        }

        // 🔥 LOGIN BUTTON
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

                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }

            Volley.newRequestQueue(this).add(request)
        }

        // 🟢 REGISTER BUTTON
        register.setOnClickListener {

            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_username), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "http://10.0.2.2/diquarks_api/register.php"

            val request = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val json = JSONObject(response)
                        val message = json.getString("message")

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }

            Volley.newRequestQueue(this).add(request)
        }
    }

    // 🔥 تغيير اللغة
    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)

        val prefs: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("lang", lang)
        editor.apply()

        recreate()
    }

    // 🔥 تحميل اللغة
    private fun loadLocale() {
        val prefs: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val lang = prefs.getString("lang", "fr")!!

        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
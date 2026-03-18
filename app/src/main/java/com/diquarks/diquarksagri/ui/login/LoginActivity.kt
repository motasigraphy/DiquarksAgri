package com.diquarks.diquarksagri.ui.login

import android.content.Intent
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

class LoginActivity : AppCompatActivity() {

    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val showPassword = findViewById<ImageView>(R.id.showPassword)

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
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
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

            val queue = Volley.newRequestQueue(this)
            queue.add(request)
        }
    }
}
package com.diquarks.diquarksagri.ui.login

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.diquarks.diquarksagri.R
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val goLogin = findViewById<TextView>(R.id.goLogin)

        registerBtn.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val confirmText = confirmPassword.text.toString().trim()

            if (emailText.isEmpty() || passwordText.isEmpty() || confirmText.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordText != confirmText) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "http://10.0.2.2/diquarks_api/register.php"

            val request = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val json = JSONObject(response)
                        val message = json.getString("message")
                        val status = json.getString("status")

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                        if (status == "success") {
                            finish()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "Erreur de traitement", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(this, "Erreur: ${error.message}", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = emailText
                    params["password"] = passwordText
                    return params
                }
            }

            Volley.newRequestQueue(this).add(request)
        }

        goLogin.setOnClickListener {
            finish()
        }
    }
}
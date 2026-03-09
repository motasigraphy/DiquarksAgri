package com.diquarks.diquarksagri.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.diquarks.diquarksagri.HomeActivity
import com.diquarks.diquarksagri.R

class LoginActivity : AppCompatActivity() {

    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val showPassword = findViewById<ImageView>(R.id.showPassword)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val register = findViewById<TextView>(R.id.register)

        // SHOW / HIDE PASSWORD
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

        // LOGIN BUTTON
        loginButton.setOnClickListener {

            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Veuillez remplir Email/Téléphone et Mot de passe",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Connexion réussie",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }

        // FORGOT PASSWORD
        forgotPassword.setOnClickListener {

            Toast.makeText(
                this,
                "Fonction Mot de passe oublié bientôt disponible",
                Toast.LENGTH_SHORT
            ).show()
        }

        // REGISTER
        register.setOnClickListener {

            Toast.makeText(
                this,
                "Page d'inscription bientôt disponible",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
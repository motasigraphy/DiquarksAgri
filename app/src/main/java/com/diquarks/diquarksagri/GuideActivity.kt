package com.diquarks.diquarksagri

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class GuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        val btn = findViewById<MaterialButton>(R.id.btnCommencer)
        btn.setOnClickListener {
            startActivity(Intent(this, PlantsActivity::class.java))
        }
    }
}
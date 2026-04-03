package com.diquarks.diquarksagri

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.diquarks.diquarksagri.data.PlantCalculation
import com.diquarks.diquarksagri.utils.PdfGenerator
import com.diquarks.diquarksagri.viewmodel.CalculatorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalculatorActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", "fr") ?: "fr"

        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = newBase.resources.configuration
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    private lateinit var viewModel: CalculatorViewModel
    private var baseDensite: Double = 0.0
    private var isDenseSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]

        val btnCalculatePdf = findViewById<LinearLayout>(R.id.btnCalculatePdf)
        val etBuyerName = findViewById<EditText>(R.id.etBuyerName)
        val etSurface = findViewById<EditText>(R.id.etSurface)
        val etUnitPrice = findViewById<EditText>(R.id.etUnitPrice)
        val etDensity = findViewById<EditText>(R.id.etDensity)
        val etDate = findViewById<EditText>(R.id.etDate)

        val btnNormal = findViewById<Button>(R.id.btnNormal)
        val btnDense = findViewById<Button>(R.id.btnDense)

        val ivPlant = findViewById<ImageView>(R.id.ivSelectedPlant)
        val tvPlantName = findViewById<TextView>(R.id.tvPlantName)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        etDate.setText(today)

        val plantName = intent.getStringExtra("PLANT_NAME") ?: ""
        val imageRes = intent.getIntExtra("PLANT_IMAGE", 0)

        tvPlantName.text = plantName

        if (imageRes != 0) {
            ivPlant.setImageResource(imageRes)
        }

        baseDensite = when {
            plantName.contains("pasteque", true) -> 2400.0
            plantName.contains("poivron", true) -> 16000.0
            plantName.contains("piment", true) -> 16000.0
            plantName.contains("tomate", true) -> 20000.0
            plantName.contains("souihla", true) -> 5600.0
            plantName.contains("melon", true) -> 4800.0
            plantName.contains("concombre", true) -> 9600.0
            plantName.contains("aubergine", true) -> 9600.0
            else -> 0.0
        }

        etDensity.setText(baseDensite.toInt().toString())

        fun updateCultureTypeUI() {
            if (isDenseSelected) {
                btnDense.setBackgroundResource(R.drawable.bg_type_selected)
                btnDense.setTextColor(getColor(android.R.color.white))

                btnNormal.setBackgroundResource(R.drawable.bg_type_unselected)
                btnNormal.setTextColor(getColor(R.color.calc_text_dark))

                etDensity.setText((baseDensite * 1.2).toInt().toString())
            } else {
                btnNormal.setBackgroundResource(R.drawable.bg_type_selected)
                btnNormal.setTextColor(getColor(android.R.color.white))

                btnDense.setBackgroundResource(R.drawable.bg_type_unselected)
                btnDense.setTextColor(getColor(R.color.calc_text_dark))

                etDensity.setText(baseDensite.toInt().toString())
            }
        }

        btnNormal.setOnClickListener {
            isDenseSelected = false
            updateCultureTypeUI()
        }

        btnDense.setOnClickListener {
            isDenseSelected = true
            updateCultureTypeUI()
        }

        updateCultureTypeUI()

        btnCalculatePdf.setOnClickListener {
            val buyer = etBuyerName.text.toString().trim()
            val surface = etSurface.text.toString().trim().toDoubleOrNull() ?: 0.0
            val prix = etUnitPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
            val densiteFinale = etDensity.text.toString().trim().toDoubleOrNull() ?: 0.0
            val selectedDate = etDate.text.toString().trim()

            if (buyer.isNotEmpty() && surface > 0 && densiteFinale > 0 && prix > 0) {
                val totalPlants = viewModel.calculateTotalPlants(
                    surface,
                    densiteFinale,
                    false
                )

                val totalPrice = viewModel.calculateTotalPrice(
                    totalPlants,
                    prix
                )

                val calculation = PlantCalculation(
                    plantName = plantName,
                    surface = surface,
                    density = densiteFinale,
                    isDense = isDenseSelected,
                    unitPrice = prix,
                    buyerName = buyer,
                    date = selectedDate
                )

                PdfGenerator.generate(
                    this,
                    calculation,
                    totalPlants.toInt(),
                    totalPrice
                )

            } else {
                Toast.makeText(
                    this,
                    getString(R.string.invalid_username),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
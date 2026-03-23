package com.diquarks.diquarksagri

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.diquarks.diquarksagri.data.PlantCalculation
import com.diquarks.diquarksagri.utils.PdfGenerator
import com.diquarks.diquarksagri.viewmodel.CalculatorViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalculatorActivity : AppCompatActivity() {

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

    private lateinit var viewModel: CalculatorViewModel
    private var baseDensite: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        viewModel = ViewModelProvider(this)[CalculatorViewModel::class.java]

        val btnCalculer = findViewById<Button>(R.id.btnCalculer)
        val etAcheteur = findViewById<EditText>(R.id.etAcheteur)
        val etSurface = findViewById<EditText>(R.id.etSurface)
        val etPrix = findViewById<EditText>(R.id.etPrix)
        val rbNormal = findViewById<RadioButton>(R.id.rbNormal)
        val rbDense = findViewById<RadioButton>(R.id.rbDense)
        val etDate = findViewById<EditText>(R.id.etDate)
        val ivPlant = findViewById<ImageView>(R.id.ivSelectedPlant)
        val tvPlantName = findViewById<TextView>(R.id.tvPlantName)
        val tvDensite = findViewById<TextView>(R.id.tvDensite)

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

        tvDensite.text = baseDensite.toInt().toString()

        rbNormal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvDensite.text = baseDensite.toInt().toString()
            }
        }

        rbDense.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val denseValue = baseDensite * 1.2
                tvDensite.text = denseValue.toInt().toString()
            }
        }

        btnCalculer.setOnClickListener {

            val buyer = etAcheteur.text.toString().trim()
            val surface = etSurface.text.toString().toDoubleOrNull() ?: 0.0
            val prix = etPrix.text.toString().toDoubleOrNull() ?: 0.0
            val densiteFinale = tvDensite.text.toString().toDoubleOrNull() ?: 0.0

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
                    isDense = rbDense.isChecked,
                    unitPrice = prix,
                    buyerName = buyer,
                    date = today
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
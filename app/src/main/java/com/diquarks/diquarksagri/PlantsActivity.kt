package com.diquarks.diquarksagri

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class PlantsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plants)

        val cardPasteque = findViewById<CardView>(R.id.cardPasteque)
        val cardPoivron = findViewById<CardView>(R.id.cardPoivron)
        val cardPiment = findViewById<CardView>(R.id.cardPiment)
        val cardTomate = findViewById<CardView>(R.id.cardTomate)
        val cardSouihla = findViewById<CardView>(R.id.cardSouihla)
        val cardMelon = findViewById<CardView>(R.id.cardMelon)
        val cardConcombre = findViewById<CardView>(R.id.cardConcombre)
        val cardAubergine = findViewById<CardView>(R.id.cardAubergine)

        cardPasteque.setOnClickListener {
            ouvrirCalculateur(getString(R.string.plant_pasteque), R.drawable.pasteque)
        }

        cardPoivron.setOnClickListener {
            ouvrirCalculateur(getString(R.string.plant_poivron), R.drawable.poivron)
        }

        cardPiment.setOnClickListener {
            ouvrirCalculateur(getString(R.string.plant_piment), R.drawable.piment)
        }

        cardTomate.setOnClickListener {
            ouvrirCalculateur(getString(R.string.plant_tomate), R.drawable.tomate)
        }

        cardSouihla.setOnClickListener {
            ouvrirCalculateur(getString(R.string.plant_souihla), R.drawable.souihla)
        }

        cardMelon.setOnClickListener {
            ouvrirCalculateur(getString(R.string.plant_melon), R.drawable.melon)
        }

        cardConcombre.setOnClickListener {
            ouvrirCalculateur(getString(R.string.plant_concombre), R.drawable.concombre)
        }

        cardAubergine.setOnClickListener {
            ouvrirCalculateur(getString(R.string.plant_aubergine), R.drawable.aubergine)
        }
    }

    private fun ouvrirCalculateur(nom: String, imageRes: Int) {
        val intent = Intent(this, CalculatorActivity::class.java)
        intent.putExtra("PLANT_NAME", nom)
        intent.putExtra("PLANT_IMAGE", imageRes)
        startActivity(intent)
    }
}
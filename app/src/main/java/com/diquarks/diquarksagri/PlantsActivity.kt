package com.diquarks.diquarksagri

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.Locale

class PlantsActivity : AppCompatActivity() {

    private lateinit var btnToutes: TextView
    private lateinit var btnLegumes: TextView
    private lateinit var btnFruits: TextView

    private lateinit var plantsGrid: GridLayout

    private lateinit var cardPasteque: CardView
    private lateinit var cardPoivron: CardView
    private lateinit var cardPiment: CardView
    private lateinit var cardTomate: CardView
    private lateinit var cardSouihla: CardView
    private lateinit var cardMelon: CardView
    private lateinit var cardConcombre: CardView
    private lateinit var cardAubergine: CardView

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", "fr")

        val locale = Locale(lang ?: "fr")
        Locale.setDefault(locale)

        val config = newBase.resources.configuration
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plants)

        initViews()
        setupPlantClicks()
        setupFilterButtons()

        showToutes()
    }

    private fun initViews() {
        btnToutes = findViewById(R.id.btnToutes)
        btnLegumes = findViewById(R.id.btnLegumes)
        btnFruits = findViewById(R.id.btnFruits)

        plantsGrid = findViewById(R.id.plantsGrid)

        cardPasteque = findViewById(R.id.cardPasteque)
        cardPoivron = findViewById(R.id.cardPoivron)
        cardPiment = findViewById(R.id.cardPiment)
        cardTomate = findViewById(R.id.cardTomate)
        cardSouihla = findViewById(R.id.cardSouihla)
        cardMelon = findViewById(R.id.cardMelon)
        cardConcombre = findViewById(R.id.cardConcombre)
        cardAubergine = findViewById(R.id.cardAubergine)
    }

    private fun setupPlantClicks() {
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

    private fun setupFilterButtons() {
        btnToutes.setOnClickListener { showToutes() }
        btnLegumes.setOnClickListener { showLegumes() }
        btnFruits.setOnClickListener { showFruits() }
    }

    private fun showToutes() {
        renderCards(
            listOf(
                cardPasteque,
                cardPoivron,
                cardPiment,
                cardTomate,
                cardSouihla,
                cardMelon,
                cardConcombre,
                cardAubergine
            )
        )

        setActiveButton(btnToutes)
        setInactiveButton(btnLegumes)
        setInactiveButton(btnFruits)
    }

    private fun showLegumes() {
        renderCards(
            listOf(
                cardPoivron,
                cardPiment,
                cardTomate,
                cardConcombre,
                cardAubergine
            )
        )

        setInactiveButton(btnToutes)
        setActiveButton(btnLegumes)
        setInactiveButton(btnFruits)
    }

    private fun showFruits() {
        renderCards(
            listOf(
                cardPasteque,
                cardSouihla,
                cardMelon
            )
        )

        setInactiveButton(btnToutes)
        setInactiveButton(btnLegumes)
        setActiveButton(btnFruits)
    }

    private fun renderCards(cardsToShow: List<CardView>) {
        val allCards = listOf(
            cardPasteque,
            cardPoivron,
            cardPiment,
            cardTomate,
            cardSouihla,
            cardMelon,
            cardConcombre,
            cardAubergine
        )

        allCards.forEach { card ->
            card.visibility = View.VISIBLE
            val parent = card.parent
            if (parent is GridLayout) {
                parent.removeView(card)
            }
        }

        cardsToShow.forEach { card ->
            plantsGrid.addView(card)
        }

        plantsGrid.requestLayout()
        plantsGrid.invalidate()
    }

    private fun setActiveButton(button: TextView) {
        button.setBackgroundResource(R.drawable.bg_filter_active)
    }

    private fun setInactiveButton(button: TextView) {
        button.setBackgroundResource(R.drawable.bg_filter_inactive)
    }

    private fun ouvrirCalculateur(nom: String, imageRes: Int) {
        val intent = Intent(this, CalculatorActivity::class.java)
        intent.putExtra("PLANT_NAME", nom)
        intent.putExtra("PLANT_IMAGE", imageRes)
        startActivity(intent)
    }
}
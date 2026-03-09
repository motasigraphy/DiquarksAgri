package com.diquarks.diquarksagri.viewmodel

import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    fun calculateTotalPlants(surface: Double, density: Double, isDense: Boolean): Double {
        val effectiveDensity = if (isDense) density * 1.2 else density
        return surface * effectiveDensity
    }

    fun calculateTotalPrice(totalPlants: Double, unitPrice: Double): Double {
        return totalPlants * unitPrice
    }
}

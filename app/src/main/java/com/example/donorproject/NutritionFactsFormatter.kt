package com.example.donorproject

object NutritionFactsFormatter {

    fun grams(value: Double): String {
        val formattedValue = if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            value.toString()
        }

        return "$formattedValue g"
    }
}
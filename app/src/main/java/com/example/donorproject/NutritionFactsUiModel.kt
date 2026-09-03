package com.example.donorproject

data class NutritionFactsUiModel(
    val foodName: String,
    val servingSize: String,
    val calories: Int,
    val proteinGrams: Double,
    val carbohydrateGrams: Double,
    val fatGrams: Double
)
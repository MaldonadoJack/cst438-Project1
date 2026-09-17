package com.example.donorproject

import kotlin.math.roundToInt

object NutritionFactsMapper {

    fun map(
        foodDetails: FoodDetails,
        serving: Serving
    ): NutritionFactsUiModel {
        return NutritionFactsUiModel(
            foodName = foodDetails.foodName,
            servingSize = serving.servingDescription ?: "Unknown serving",
            calories = serving.calories
                ?.toDoubleOrNull()
                ?.roundToInt() ?: 0,
            proteinGrams = serving.protein?.toDoubleOrNull() ?: 0.0,
            carbohydrateGrams = serving.carbohydrate?.toDoubleOrNull() ?: 0.0,
            fatGrams = serving.fat?.toDoubleOrNull() ?: 0.0
        )
    }
}
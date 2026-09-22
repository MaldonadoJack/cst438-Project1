package com.example.donorproject

import com.example.donorproject.data.local.FoodLogEntity

fun NutritionFactsUiModel.toFoodLogEntity (
    userId: Int,
    loggedAt: Long = System.currentTimeMillis()
) : FoodLogEntity {
    return FoodLogEntity(
        userId = userId,
        foodId = foodId,
        foodName = foodName,
        servingDescription = servingSize,
        calories = calories,
        proteinGrams = proteinGrams,
        carbohydrateGrams = carbohydrateGrams,
        fatGrams = fatGrams,
        loggedAtEpochMillis = loggedAt
    )
}
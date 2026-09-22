package com.example.donorproject

import org.junit.Assert.assertEquals
import org.junit.Test

class FoodLogMapperTest {

    @Test
    fun nutritionFactsAreConvertedToFoodLogEntity() {
        val nutritionalFacts = NutritionFactsUiModel(
            foodId = "123",
            foodName = "Apple",
            servingSize = "1 medium apple",
            calories = 95,
            proteinGrams = 0.5,
            carbohydrateGrams = 25.0,
            fatGrams = 0.3
        )

        val result = nutritionalFacts.toFoodLogEntity(
            userId = 7,
            loggedAt = 1_700_000_000_000L
        )

        assertEquals(7, result.userId)
        assertEquals("123", result.foodId)
        assertEquals("Apple", result.foodName)
        assertEquals("1 medium apple", result.servingDescription)
        assertEquals(95, result.calories)
        assertEquals(0.5, result.proteinGrams, 0.001)
        assertEquals(25.0, result.carbohydrateGrams, 0.001)
        assertEquals(0.3, result.fatGrams, 0.001)
        assertEquals(1_700_000_000_000L, result.loggedAtEpochMillis)
    }
}
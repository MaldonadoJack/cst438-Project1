package com.example.donorproject

import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionFactsMapperTest {

    @Test
    fun map_convertsApiServingIntoUiModel() {
        val foodDetails = FoodDetails(
            foodId = "123",
            foodName = "Apple",
            brandName = null,
            servings = Servings(emptyList())
        )

        val serving = Serving(
            servingDescription = "1 medium apple",
            calories = "95",
            carbohydrate = "25.1",
            protein = "0.5",
            fat = "0.3",
            fiber = "4.4",
            sugar = "19.0",
            sodium = "2"
        )

        val result = NutritionFactsMapper.map(foodDetails, serving)

        assertEquals("Apple", result.foodName)
        assertEquals("1 medium apple", result.servingSize)
        assertEquals(95, result.calories)
        assertEquals(0.5, result.proteinGrams, 0.0)
        assertEquals(25.1, result.carbohydrateGrams, 0.0)
        assertEquals(0.3, result.fatGrams, 0.0)
    }

    @Test
    fun map_usesZeroWhenOptionalNutritionValuesAreMissing() {
        val foodDetails = FoodDetails(
            foodId = "123",
            foodName = "Apple",
            brandName = null,
            servings = Servings(emptyList())
        )

        val serving = Serving(
            servingDescription = null,
            calories = null,
            carbohydrate = null,
            protein = null,
            fat = null,
            fiber = null,
            sugar = null,
            sodium = null
        )

        val result = NutritionFactsMapper.map(foodDetails, serving)

        assertEquals("Unknown serving", result.servingSize)
        assertEquals(0, result.calories)
        assertEquals(0.0, result.proteinGrams, 0.0)
        assertEquals(0.0, result.carbohydrateGrams, 0.0)
        assertEquals(0.0, result.fatGrams, 0.0)
    }
}
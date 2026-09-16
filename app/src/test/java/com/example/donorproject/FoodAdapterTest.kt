package com.example.donorproject

import org.junit.Assert.assertEquals
import org.junit.Test

class FoodAdapterTest {

    @Test
    fun updateFoods_updatesItemCount() {
        val adapter = FoodAdapter {}

        val foods = listOf(
            Food(
                foodId = "1" ,
                foodName = "Apple" ,
                brandName = null ,
                foodType = "Generic" ,
                foodDescription = "Per 100g - Caleries: 52kcal" ,
                foodUrl = null
            ),
            Food(
                foodId = "2" ,
                foodName = "banana" ,
                brandName = null ,
                foodType = "Generic" ,
                foodDescription = "per 100g - Calories: 89kcal" ,
                foodUrl = null
            )
        )

        adapter.updateFoods(foods)

        assertEquals(2 , adapter.itemCount)
    }

    @Test
    fun emptyFoodList_hasZeroItems() {
        val adapter = FoodAdapter{}

        adapter.updateFoods(emptyList())

        assertEquals(0 , adapter.itemCount)
    }
}
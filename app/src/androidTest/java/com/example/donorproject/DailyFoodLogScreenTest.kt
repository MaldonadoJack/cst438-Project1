package com.example.donorproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.donorproject.data.local.FoodLogEntity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyFoodLogScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysFoodEntriesAndNutritionTotals() {
        val entries = listOf(
            FoodLogEntity(
                id = 1,
                userId = 7,
                foodId = "123",
                foodName = "Apple",
                servingDescription = "1 medium apple",
                calories = 95,
                proteinGrams = 0.5,
                carbohydrateGrams = 25.0,
                fatGrams = 0.3,
                loggedAtEpochMillis = 1_700_000_000_000L
            ),
            FoodLogEntity(
                id = 2,
                userId = 7,
                foodId = "456",
                foodName = "Greek Yogurt",
                servingDescription = "170 g",
                calories = 120,
                proteinGrams = 12.0,
                carbohydrateGrams = 7.0,
                fatGrams = 0.0,
                loggedAtEpochMillis = 1_700_000_000_000L
            )
        )

        composeTestRule.setContent {
            DailyFoodLogScreen(
                entries = entries,
                onDeleteEntry = {}
            )
        }

        composeTestRule.onNodeWithText("Today's Food Log").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apple").assertIsDisplayed()
        composeTestRule.onNodeWithText("Greek Yogurt").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calories: 215 kcal").assertIsDisplayed()
    }

    @Test
    fun clickingDeleteCallsDeleteCallback() {
        val entries = listOf(
            FoodLogEntity(
                id = 10,
                userId = 7,
                foodId = "123",
                foodName = "Apple",
                servingDescription = "1 medium apple",
                calories = 95,
                proteinGrams = 0.5,
                carbohydrateGrams = 25.0,
                fatGrams = 0.3,
                loggedAtEpochMillis = 1_700_000_000_000L
            )
        )

        var deletedEntryId = -1

        composeTestRule.setContent {
            DailyFoodLogScreen(
                entries = entries,
                onDeleteEntry = { deletedEntryId = it }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Delete food").performScrollTo().performClick()

        assertEquals(10, deletedEntryId)
    }
}
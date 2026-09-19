package com.example.donorproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Covers the result row that replaced the adapter's view holder: the three lines it
 * shows, the fallback used when a food has no brand, and that the whole row is tappable.
 */
@RunWith(AndroidJUnit4::class)
class FoodItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var clicks = 0

    @Test
    fun showsTheFoodNameBrandAndDescription() {
        showFoodItem(brandName = BRAND_NAME, foodDescription = DESCRIPTION)

        composeTestRule.onNodeWithText(FOOD_NAME).assertIsDisplayed()
        composeTestRule.onNodeWithText(BRAND_NAME).assertIsDisplayed()
        composeTestRule.onNodeWithText(DESCRIPTION).assertIsDisplayed()
    }

    @Test
    fun aFoodWithoutABrandFallsBackToGenericFood() {
        showFoodItem(brandName = null, foodDescription = DESCRIPTION)

        composeTestRule.onNodeWithText("Generic food").assertIsDisplayed()
    }

    @Test
    fun aFoodWithoutADescriptionStillShowsTheNameAndBrand() {
        showFoodItem(brandName = BRAND_NAME, foodDescription = null)

        composeTestRule.onNodeWithText(FOOD_NAME).assertIsDisplayed()
        composeTestRule.onNodeWithText(BRAND_NAME).assertIsDisplayed()
    }

    @Test
    fun tappingTheRowReportsTheClickOnce() {
        showFoodItem(brandName = BRAND_NAME, foodDescription = DESCRIPTION)

        composeTestRule.onNodeWithText(FOOD_NAME).performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, clicks)
    }

    private fun showFoodItem(brandName: String?, foodDescription: String?) {
        clicks = 0

        composeTestRule.setContent {
            FoodItem(
                food = Food(
                    foodId = "1",
                    foodName = FOOD_NAME,
                    brandName = brandName,
                    foodType = "Brand",
                    foodDescription = foodDescription,
                    foodUrl = null
                ),
                onClick = { clicks++ }
            )
        }
    }

    private companion object {
        const val FOOD_NAME = "Greek Yogurt"
        const val BRAND_NAME = "Chobani"
        const val DESCRIPTION = "Per 170g - Calories: 120kcal"
    }
}

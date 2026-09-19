package com.example.donorproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Drives the migrated home page. The cases pin down the behaviour the activity used to
 * own: the query text listener's minimum length and debounce, the horizontal results
 * that replaced the RecyclerView, and what a tap on a result reports.
 */
@RunWith(AndroidJUnit4::class)
class HomePageScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val searchedQueries = mutableListOf<String>()
    private val clickedFoods = mutableListOf<Food>()

    @Test
    fun showsTheHeaderAndKeepsTheSearchFieldCollapsed() {
        showHomePage()

        composeTestRule.onNodeWithText("App Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("LOGOUT").assertIsDisplayed()
        composeTestRule.onNodeWithText("ACCOUNT").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()

        assertTrue(isSearchFieldCollapsed())
    }

    @Test
    fun tappingTheSearchIconExpandsTheFieldAndShowsTheHint() {
        showHomePage()

        expandSearchField()

        composeTestRule.onNodeWithText(SEARCH_HINT).assertIsDisplayed()
        assertFalse(isSearchFieldCollapsed())
    }

    @Test
    fun aSingleCharacterNeverStartsASearch() {
        showHomePage()
        expandSearchField()

        composeTestRule.mainClock.autoAdvance = false
        typeQuery("y")
        composeTestRule.mainClock.advanceTimeBy(PAST_DEBOUNCE_MILLIS)

        assertEquals(emptyList<String>(), searchedQueries)
    }

    @Test
    fun typingStartsOneSearchOnceTheDebounceElapses() {
        showHomePage()
        expandSearchField()

        composeTestRule.mainClock.autoAdvance = false
        typeQuery(QUERY)
        composeTestRule.mainClock.advanceTimeBy(PAST_DEBOUNCE_MILLIS)

        assertEquals(listOf(QUERY), searchedQueries)
    }

    @Test
    fun noSearchStartsBeforeTheDebounceElapses() {
        showHomePage()
        expandSearchField()

        composeTestRule.mainClock.autoAdvance = false
        typeQuery(QUERY)
        composeTestRule.mainClock.advanceTimeBy(UNDER_DEBOUNCE_MILLIS)

        assertEquals(emptyList<String>(), searchedQueries)

        composeTestRule.mainClock.advanceTimeBy(PAST_DEBOUNCE_MILLIS)

        assertEquals(listOf(QUERY), searchedQueries)
    }

    @Test
    fun eachEditRestartsTheDebounceSoOnlyTheFinalQueryIsSearched() {
        showHomePage()
        expandSearchField()

        composeTestRule.mainClock.autoAdvance = false
        typeQuery("yog")
        composeTestRule.mainClock.advanceTimeBy(UNDER_DEBOUNCE_MILLIS)
        typeQuery("urt")
        composeTestRule.mainClock.advanceTimeBy(PAST_DEBOUNCE_MILLIS)

        assertEquals(listOf(QUERY), searchedQueries)
    }

    @Test
    fun theQueryIsTrimmedBeforeItIsSearched() {
        showHomePage()
        expandSearchField()

        composeTestRule.mainClock.autoAdvance = false
        typeQuery("  $QUERY  ")
        composeTestRule.mainClock.advanceTimeBy(PAST_DEBOUNCE_MILLIS)

        assertEquals(listOf(QUERY), searchedQueries)
    }

    @Test
    fun submittingSendsTheRawQueryAndLeavesTheDebounceRunning() {
        showHomePage()
        expandSearchField()

        composeTestRule.mainClock.autoAdvance = false
        typeQuery("  $QUERY  ")
        composeTestRule.onNode(hasSetTextAction()).performImeAction()

        // The old listener searched the untrimmed submit value immediately.
        assertEquals(listOf("  $QUERY  "), searchedQueries)

        composeTestRule.mainClock.advanceTimeBy(PAST_DEBOUNCE_MILLIS)

        // And it did not cancel the pending typed search, so both fire.
        assertEquals(listOf("  $QUERY  ", QUERY), searchedQueries)
    }

    @Test
    fun submittingABlankQueryStillSearches() {
        showHomePage()
        expandSearchField()

        composeTestRule.onNode(hasSetTextAction()).performImeAction()
        composeTestRule.waitForIdle()

        assertEquals(listOf(""), searchedQueries)
    }

    @Test
    fun resultsAreListedAndTappingOneReportsThatFood() {
        showHomePage(foods = FOODS)

        composeTestRule.onNodeWithText("Greek Yogurt").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chobani").assertIsDisplayed()

        composeTestRule.onNodeWithText("Greek Yogurt").performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf(FOODS.first()), clickedFoods)
    }

    @Test
    fun laterResultsAreReachedByScrollingHorizontally() {
        showHomePage(foods = FOODS)

        composeTestRule.onNodeWithText("Banana")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Generic food").assertIsDisplayed()
    }

    @Test
    fun anEmptyResultListShowsNoResultsAndNoMessage() {
        showHomePage(foods = emptyList())

        assertTrue(
            composeTestRule.onAllNodesWithText("Greek Yogurt").fetchSemanticsNodes().isEmpty()
        )
        // The band the results sit in stays empty rather than explaining itself, which is
        // how the screen behaved before the migration.
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun theClearButtonEmptiesTheQueryAndThenCollapsesTheField() {
        showHomePage()
        expandSearchField()
        typeQuery(QUERY)

        composeTestRule.onNodeWithContentDescription(CLEAR_DESCRIPTION).performClick()
        composeTestRule.waitForIdle()

        // First tap clears the text but leaves the field open, as the SearchView did.
        composeTestRule.onNodeWithText(SEARCH_HINT).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription(CLEAR_DESCRIPTION).performClick()
        composeTestRule.waitForIdle()

        assertTrue(isSearchFieldCollapsed())
    }

    private fun showHomePage(foods: List<Food> = emptyList()) {
        composeTestRule.setContent {
            HomePageScreen(
                foods = foods,
                onSearchRequested = { query -> searchedQueries += query },
                onFoodClick = { food -> clickedFoods += food }
            )
        }
    }

    private fun expandSearchField() {
        composeTestRule.onNodeWithContentDescription(SEARCH_DESCRIPTION).performClick()
        composeTestRule.waitForIdle()
    }

    private fun typeQuery(text: String) {
        composeTestRule.onNode(hasSetTextAction()).performTextInput(text)
    }

    private fun isSearchFieldCollapsed(): Boolean =
        composeTestRule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().isEmpty()

    private companion object {
        const val QUERY = "yogurt"
        const val SEARCH_HINT = "Search..."
        const val SEARCH_DESCRIPTION = "Search"
        const val CLEAR_DESCRIPTION = "Clear query"

        // The screen waits 500ms after the last edit before searching.
        const val UNDER_DEBOUNCE_MILLIS = 400L
        const val PAST_DEBOUNCE_MILLIS = 600L

        val FOODS = listOf(
            Food(
                foodId = "1",
                foodName = "Greek Yogurt",
                brandName = "Chobani",
                foodType = "Brand",
                foodDescription = "Per 170g - Calories: 120kcal",
                foodUrl = null
            ),
            Food(
                foodId = "2",
                foodName = "Banana",
                brandName = null,
                foodType = "Generic",
                foodDescription = "Per 1 medium - Calories: 105kcal",
                foodUrl = null
            )
        )
    }
}

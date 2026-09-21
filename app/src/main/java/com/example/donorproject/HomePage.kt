package com.example.donorproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.example.donorproject.ui.theme.DOnorProjectTheme
import kotlinx.coroutines.launch

class HomePage : AppCompatActivity() {

    private val foods = mutableStateOf<List<Food>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = userIdFromIntent(intent)
        if (!isValidUserId(userId)) {
            // No authenticated owner: do not show the signed-in Home flow.
            startActivity(MainActivity.createIntentAfterLogout(this))
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            DOnorProjectTheme {
                HomePageScreen(
                    foods = foods.value,
                    onSearchRequested = ::searchApi,
                    onFoodClick = ::loadNutritionFacts,
                    onLogoutClick = ::logOut
                )
            }
        }
    }

    private fun searchApi(query: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.searchFoods(
                    accessToken = FatSecretConfig.ACCESS_TOKEN,
                    searchExpression = query
                )

                foods.value = response.foods.food
            } catch (exception: Exception) {
                Toast.makeText(
                    this@HomePage,
                    "Search failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadNutritionFacts(food: Food) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getFoodDetails(
                    accessToken = FatSecretConfig.ACCESS_TOKEN,
                    foodId = food.foodId
                )

                val serving = response.food.servings.serving.firstOrNull()

                if (serving == null) {
                    Toast.makeText(
                        this@HomePage,
                        "No serving information is available",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val nutritionFacts = NutritionFactsMapper.map(
                    foodDetails = response.food,
                    serving = serving
                )

                startActivity(
                    NutritionFactsActivity.createIntent(
                        context = this@HomePage,
                        nutritionFacts = nutritionFacts
                    )
                )
            } catch (exception: Exception) {
                Toast.makeText(
                    this@HomePage,
                    "Unable to load nutrition facts: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Returns directly to the login screen and drops this signed-in task so Back
     * cannot reopen HomePage. There is no persisted session token to delete;
     * finishing this task is the current sign-out.
     */
    private fun logOut() {
        startActivity(MainActivity.createIntentAfterLogout(this))
        finish()
    }

    companion object {
        private const val EXTRA_USER_ID = "com.example.donorproject.USER_ID"
        private const val INVALID_USER_ID = -1

        fun createIntent(context: Context, userId: Int): Intent {
            return Intent(context, HomePage::class.java).apply {
                putExtra(EXTRA_USER_ID, userId)
            }
        }

        internal fun userIdFromIntent(intent: Intent?): Int =
            intent?.getIntExtra(EXTRA_USER_ID, INVALID_USER_ID) ?: INVALID_USER_ID

        internal fun isValidUserId(userId: Int): Boolean = userId > 0
    }
}

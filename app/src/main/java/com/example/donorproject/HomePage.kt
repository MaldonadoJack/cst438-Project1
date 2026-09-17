package com.example.donorproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import android.widget.Toast
import android.widget.SearchView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomePage : ComponentActivity() {
    private var searchJob: Job? = null // stores coroutine for current search
    private lateinit var foodAdapter : FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        val searchView = findViewById<SearchView>(R.id.searchView)
        val recyclerView = findViewById<RecyclerView>(R.id.searchResultRecyclerView)

        foodAdapter = FoodAdapter { selectedFood ->
            loadNutritionFacts(selectedFood)
        }

        recyclerView.layoutManager = LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false)
        recyclerView.adapter = foodAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Submits users search
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchApi(it)
                }
                return true
            }

            // Checks when the user enters something into the search view (delay added so API is not swamped with responses)
            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()

                val query = newText?.trim().orEmpty()

                if (query.length >= 2) {
                    searchJob = lifecycleScope.launch {
                        delay(500.milliseconds)
                        searchApi(query)
                    }
                }
                return true
            }
        })
    }

    // Implements the search function using the API
    private fun searchApi(query: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.searchFoods (
                    accessToken = FatSecretConfig.ACCESS_TOKEN,
                    searchExpression = query
                )

                val foods = response.foods.food

                foodAdapter.updateFoods(foods)

            } catch (exception: Exception) {
                Toast.makeText(this@HomePage, "Search failed: ${exception.message}", Toast.LENGTH_SHORT).show()
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
}
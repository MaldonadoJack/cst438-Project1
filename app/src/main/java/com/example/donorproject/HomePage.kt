package com.example.donorproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class HomePage : AppCompatActivity() {
    private var searchJob: Job? = null // stores coroutine for current search
    private lateinit var foodAdapter : FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        val searchView = findViewById<SearchView>(R.id.searchView)
        val recyclerView = findViewById<RecyclerView>(R.id.searchResultRecyclerView)

        // foodAdapter = FoodAdapter() //TODO Add functionality to click on food to show UI Model

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
}
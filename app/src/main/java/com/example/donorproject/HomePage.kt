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

class HomePage : AppCompatActivity() {
    private var searchJob: Job? = null // stores coroutine for current search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        val searchView = findViewById<SearchView>(R.id.searchView)

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
                    accessToken = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6Ijk0Q0NGMUU4RTI4RUM3RkM4QTc2RTcyNTM4M0E4RjM0OUU3MUUzRjAiLCJ0eXAiOiJKV1QiLCJ4NXQiOiJsTXp4Nk9LT3hfeUtkdWNsT0RxUE5KNXg0X0EifQ.eyJuYmYiOjE3ODg5OTk3MzYsImV4cCI6MTc4OTA4NjEzNiwiaXNzIjoiaHR0cHM6Ly9vYXV0aC5mYXRzZWNyZXQuY29tIiwiYXVkIjoiYmFzaWMiLCJjbGllbnRfaWQiOiI0YmExMDQ3ZTU0NWE0ODcwYWI4ZjcyMjc4Y2NhNWQ0ZiIsImlhdCI6MTc4ODk5OTczNiwic2NvcGUiOlsiYmFzaWMiXX0.vFN-PS505PBLob-xg1grAmAsZ1HRlet-cHb9K0nKcay8Rnd2gpnY00ZMbguDR2cjww4Wvg0LoTMhBeTm1qrI-vqbPmX4289whXcen-nmxChH8rgO090duHiL6yXzF0pZ-FIz5NTHYQFMjyQ_lxXrp5v7zNJsOaEuPR2cQ2lBSYUgy_NgRzDK2EGvF_6cK0jq5n_JTIbHKN-kOrL-wujMtnIxjdv1K9-91_qoCWxH_SpzwtMEdJmQWgJ6EdjBqv0WCApXt9OExMPOxEb7RWGJDYCsbudndLbauF_zzKmZypn1JcGZwWJHmGiIhG8E66y-UZaoTA8dLCtsQYq7rphNtrdL5EbY7DsyUa8A2dr2Mldxs1Sb9_YJpJUrcPvOaKBlLHoEAimLl1-2uu9Sl_Q6KKiQusU9YH4udJvgJ0X-UCZsywqrhu9-k16hdhdB8E5YaxyxftZO8Gk6-LxDiSEVeYyugwtdcqMMPbbnnhA8LQqDj8IMqndtEMQUf6IF_kpY4GXNLGc7IKr4b3FuU4GmCn3lYsSy58Q9YdsQI6oBm_uruMC4MvmSKOAk-CCfpYH-jvzfmpYfo8MG6eEqIv5NzBxG3vphznqt35iWyxTWt-oFAenXhRaS7s7yBKmfEl0-GZ5caU6f5u78HUBlTt05t88ZL4fUY5uTqASaf6Go7kI",
                    searchExpression = query
                )

                val foods = response.foods.food

                // TODO: Update recycler view

            } catch (exception: Exception) {
                Toast.makeText(this@HomePage, "Search failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
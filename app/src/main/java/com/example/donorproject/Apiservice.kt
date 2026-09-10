package com.example.donorproject

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Class to hold the response from the API
data class APIResponse(
    val foods: Foods
)

// Class to hold a list of search results
data class Foods (
    val food: List<Food>
)

// Class to hold data on a single search result
data class Food(
    val food_id: String,
    val food_name: String,
    val brand_name: String?,
    val food_type: String,
    val food_description: String?,
    val food_url: String?
)

// Allows retrofit to communicate with the API
interface ApiService {
    @GET("rest/foods/search/v1")
    suspend fun searchFoods(
        @Header("Authorization") accessToken: String,
        @Query("search_expression") searchExpression: String,
        @Query("format") format: String = "json",
        @Query("max_results") maxResults: Int = 20
    ): APIResponse
}
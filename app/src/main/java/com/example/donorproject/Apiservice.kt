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
    val foodId: String,
    val foodName: String,
    val brandName: String?,
    val foodType: String,
    val foodDescription: String?,
    val foodUrl: String?
)

data class FoodDetailsResponse(
    val food: FoodDetails
)

data class FoodDetails(
    val foodId: String ,
    val foodName: String ,
    val brandName: String? ,
    val servings: Servings
)

data class Servings(
    val serving: List<Serving>
)

data class Serving(
    val servingDescription: String? ,
    val calories: String? ,
    val carbohydrate: String? ,
    val protein: String? ,
    val fat: String? ,
    val fiber: String? ,
    val sugar: String? ,
    val sodium: String?
)

// Allows retrofit to communicate with the API
interface ApiService {
    @GET("rest/foods/search/v1")
    suspend fun searchFoods(
        @Header("Authorization") accessToken: String ,
        @Query("search_expression") searchExpression: String ,
        @Query("format") format: String = "json" ,
        @Query("max_results") maxResults: Int = 20
    ) : APIResponse

    @GET("rest/foods/v1")
    suspend fun getFoodDetails(
        @Header("Authorization") accessToken: String,
        @Query("food_id") foodId: String,
        @Query("format") format: String = "json"
    ): FoodDetailsResponse
}
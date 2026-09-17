package com.example.donorproject

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Class to hold the response from the API
data class APIResponse(
    val foods: Foods
)

// Class to hold a list of search results
data class Foods(
    val food: List<Food>
)

// Class to hold data on a single search result
data class Food(
    @SerializedName("food_id")
    val foodId: String,

    @SerializedName("food_name")
    val foodName: String,

    @SerializedName("brand_name")
    val brandName: String?,

    @SerializedName("food_type")
    val foodType: String,

    @SerializedName("food_description")
    val foodDescription: String?,

    @SerializedName("food_url")
    val foodUrl: String?
)

data class FoodDetailsResponse(
    val food: FoodDetails
)

data class FoodDetails(
    @SerializedName("food_id")
    val foodId: String,

    @SerializedName("food_name")
    val foodName: String,

    @SerializedName("brand_name")
    val brandName: String?,

    val servings: Servings
)

data class Servings(
    val serving: List<Serving>
)

data class Serving(
    @SerializedName("serving_description")
    val servingDescription: String?,

    val calories: String?,
    val carbohydrate: String?,
    val protein: String?,
    val fat: String?,
    val fiber: String?,
    val sugar: String?,
    val sodium: String?
)

// Allows Retrofit to communicate with the API
interface ApiService {

    @GET("rest/foods/search/v1")
    suspend fun searchFoods(
        @Header("Authorization") accessToken: String,
        @Query("search_expression") searchExpression: String,
        @Query("format") format: String = "json",
        @Query("max_results") maxResults: Int = 20
    ): APIResponse

    @GET("rest/foods/v1")
    suspend fun getFoodDetails(
        @Header("Authorization") accessToken: String,
        @Query("food_id") foodId: String,
        @Query("format") format: String = "json"
    ): FoodDetailsResponse
}
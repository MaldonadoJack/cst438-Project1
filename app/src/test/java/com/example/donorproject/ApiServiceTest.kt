package com.example.donorproject

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var api: ApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        api = Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }

    @After
    fun tearDOwn() {
        server.shutdown()
    }

    @Test
    fun searchFoods_returnsFoodResults() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setHeader("Content-Type" , "application/json").setBody(
            """
            {
              "foods": {
                "food": [
                  {
                    "food_id": "123",
                    "food_name": "Apple",
                    "brand_name": null,
                    "food_type": "Generic",
                    "food_description": "Per 100g - Calories: 52kcal",
                    "food_url": "https://example.com/apple"
                  }
                ],
                "max_results": "20",
                "page_number": "0",
                "total_results": "1"
              }
            }
            """.trimIndent()
        ))

        val response = api.searchFoods(
            accessToken = "Bearer test-token",
            searchExpression = "apple"
        )

        val food = response.foods.food.first()

        assertEquals("123", food.food_id)
        assertEquals("Apple", food.food_name)
        assertEquals("Generic", food.food_type)
    }

    @Test
    fun searchFoods_sendsCorrectRequest() = runBlocking {
        server.enqueue(
            MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json").setBody(
                """
                {
                  "foods": {
                    "food": [],
                    "max_results": "20",
                    "page_number": "0",
                    "total_results": "0"
                  }
                }
                """.trimIndent()
            )
        )

        api.searchFoods(
            accessToken = "Bearer test-token",
            searchExpression = "apple"
        )

        val request: RecordedRequest = server.takeRequest()

        assertEquals("GET", request.method)
        assertEquals("Bearer test-token", request.getHeader("Authorization"))
        assertEquals("/rest/foods/search/v1", request.requestUrl?.encodedPath)
        assertEquals("apple", request.requestUrl?.queryParameter("search_expression"))
    }
}
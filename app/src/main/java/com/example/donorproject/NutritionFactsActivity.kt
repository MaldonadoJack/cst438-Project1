package com.example.donorproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.donorproject.ui.theme.DOnorProjectTheme

class NutritionFactsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nutritionFacts = NutritionFactsUiModel(
            foodName = intent.getStringExtra(EXTRA_FOOD_NAME) ?: "Unknown food",
            servingSize = intent.getStringExtra(EXTRA_SERVING_SIZE) ?: "Unknown serving",
            calories = intent.getIntExtra(EXTRA_CALORIES, 0),
            proteinGrams = intent.getDoubleExtra(EXTRA_PROTEIN, 0.0),
            carbohydrateGrams = intent.getDoubleExtra(EXTRA_CARBOHYDRATES, 0.0),
            fatGrams = intent.getDoubleExtra(EXTRA_FAT, 0.0)
        )

        enableEdgeToEdge()

        setContent {
            DOnorProjectTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NutritionFactsScreen(
                        nutritionFacts = nutritionFacts,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    companion object {
        private const val EXTRA_FOOD_NAME = "food_name"
        private const val EXTRA_SERVING_SIZE = "serving_size"
        private const val EXTRA_CALORIES = "calories"
        private const val EXTRA_PROTEIN = "protein"
        private const val EXTRA_CARBOHYDRATES = "carbohydrates"
        private const val EXTRA_FAT = "fat"

        fun createIntent(
            context: Context,
            nutritionFacts: NutritionFactsUiModel
        ): Intent {
            return Intent(context, NutritionFactsActivity::class.java).apply {
                putExtra(EXTRA_FOOD_NAME, nutritionFacts.foodName)
                putExtra(EXTRA_SERVING_SIZE, nutritionFacts.servingSize)
                putExtra(EXTRA_CALORIES, nutritionFacts.calories)
                putExtra(EXTRA_PROTEIN, nutritionFacts.proteinGrams)
                putExtra(EXTRA_CARBOHYDRATES, nutritionFacts.carbohydrateGrams)
                putExtra(EXTRA_FAT, nutritionFacts.fatGrams)
            }
        }
    }
}
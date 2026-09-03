package com.example.donorproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NutritionFactsScreen(
    nutritionFacts: NutritionFactsUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Nutrition Facts",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = nutritionFacts.foodName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Serving size: ${nutritionFacts.servingSize}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                NutritionFactRow(
                    label = "Calories",
                    value = nutritionFacts.calories.toString(),
                    emphasized = true
                )

                NutritionFactRow(
                    label = "Protein",
                    value = "${nutritionFacts.proteinGrams} g"
                )

                NutritionFactRow(
                    label = "Carbohydrates",
                    value = "${nutritionFacts.carbohydrateGrams} g"
                )

                NutritionFactRow(
                    label = "Fat",
                    value = "${nutritionFacts.fatGrams} g"
                )
            }
        }

        Text(
            text = "Nutrition values are based on the selected serving size.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NutritionFactRow(
    label: String,
    value: String,
    emphasized: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun NutritionFactsScreenPreview() {
    MaterialTheme {
        NutritionFactsScreen(
            nutritionFacts = NutritionFactsUiModel(
                foodName = "Grilled Chicken Breast",
                servingSize = "100 g",
                calories = 165,
                proteinGrams = 31.0,
                carbohydrateGrams = 0.0,
                fatGrams = 3.6
            )
        )
    }
}
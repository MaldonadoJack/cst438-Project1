package com.example.donorproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.example.donorproject.data.local.FoodLogEntity
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color

@Composable
fun DailyFoodLogScreen(
    entries: List<FoodLogEntity>,
    onDeleteEntry: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val calories = entries.sumOf { it.calories }
    val protein = entries.sumOf { it.proteinGrams }
    val carbohydrates = entries.sumOf { it.carbohydrateGrams }
    val fat = entries.sumOf { it.fatGrams }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1E8))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (onBackClick != null) {
            OutlinedButton(onClick = onBackClick) {
                Text(stringResource(R.string.back_to_home))
            }
        }

        Text(
            text = "Today's Food Log",
            color = Color(0xFF355E3B),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth()

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Nutrition totals",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Calories: $calories kcal")
                Text("Protein: ${NutritionFactsFormatter.grams(protein)}")
                Text("Carbohydrates: ${NutritionFactsFormatter.grams(carbohydrates)}")
                Text("Fat: ${NutritionFactsFormatter.grams(fat)}")
            }
        }

        if (entries.isEmpty()) {
            Text("No food logged today")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = entry.foodName,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(entry.servingDescription)
                                Text("${entry.calories} kcal")
                            }

                            IconButton(
                                onClick = {
                                    onDeleteEntry(entry.id)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete food"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyFoodLogScreenPreview() {
    MaterialTheme {
        DailyFoodLogScreen(
            entries = emptyList(),
            onDeleteEntry = {},
            onBackClick = {}
        )
    }
}
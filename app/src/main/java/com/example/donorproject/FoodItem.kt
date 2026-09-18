package com.example.donorproject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ItemWidth = 220.dp
private val ItemPadding = 12.dp

// A bare TextStyle is used instead of the MaterialTheme typography so that the
// letter spacing and line height stay at the framework defaults a TextView uses.
private val FoodNameStyle = TextStyle(
    color = Color(0xFF000000),
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold
)

private val SecondaryStyle = TextStyle(
    color = Color(0xFF555555),
    fontSize = 14.sp
)

private const val MissingBrandName = "Generic food"

/**
 * A single horizontal search result. Replaces the `item_food.xml` row that used
 * to be bound by a RecyclerView adapter.
 */
@Composable
fun FoodItem(
    food: Food,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(ItemWidth)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                // The replaced LinearLayout had no background, so it drew no ripple.
                indication = null,
                onClick = onClick
            )
            .padding(ItemPadding)
    ) {
        Text(
            text = food.foodName,
            modifier = Modifier.fillMaxWidth(),
            style = FoodNameStyle
        )

        Text(
            text = food.brandName ?: MissingBrandName,
            modifier = Modifier.fillMaxWidth(),
            style = SecondaryStyle
        )

        Text(
            text = food.foodDescription.orEmpty(),
            modifier = Modifier.fillMaxWidth(),
            style = SecondaryStyle
        )
    }
}

/** Sample results used by the previews on this screen. */
internal fun sampleFood(
    foodId: String,
    foodName: String,
    brandName: String?,
    foodDescription: String?
): Food = Food(
    foodId = foodId,
    foodName = foodName,
    brandName = brandName,
    foodType = "Brand",
    foodDescription = foodDescription,
    foodUrl = null
)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun FoodItemPreview() {
    FoodItem(
        food = sampleFood(
            foodId = "33691",
            foodName = "Greek Yogurt",
            brandName = "Chobani",
            foodDescription = "Per 170g - Calories: 120kcal | Fat: 0.00g | Carbs: 7.00g"
        ),
        onClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun FoodItemWithoutBrandPreview() {
    FoodItem(
        food = sampleFood(
            foodId = "1234",
            foodName = "Banana",
            brandName = null,
            foodDescription = null
        ),
        onClick = {}
    )
}

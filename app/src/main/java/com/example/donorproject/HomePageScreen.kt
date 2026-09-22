package com.example.donorproject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/** Shortest query that starts a search, matching the previous query text listener. */
private const val MinimumSearchLength = 2

/** Debounce applied to typing, matching the previous coroutine delay. */
private val SearchDebounce = 500.milliseconds

/** Query hint spelled exactly as the replaced SearchView spelled it. */
private const val SearchHint = "Search..."

// The SearchView supplied its own descriptions for these two controls.
private const val SearchContentDescription = "Search"
private const val CloseContentDescription = "Clear query"

private val ScreenBackground = Color(0xFF036F63)
private val OnScreenTextColor = Color(0xFFFFFFFF)
private val SurfaceBackground = Color(0xFFFFFFFF)

// Widget.AppCompat.Button.Borderless.Colored resolved against the app theme:
// 14sp sans-serif-medium all caps, tinted with the theme accent colour.
private val ButtonTextColor = Color(0xFF03DAC6)
private val ButtonMinWidth = 88.dp
private val ButtonMinHeight = 48.dp
private val ButtonMargin = 2.dp

// Distances taken from the ConstraintLayout the screen replaces.
private val SearchFieldHeight = 50.dp
private val SearchFieldBottomMargin = 580.dp
private val ResultsTopMargin = 8.dp
private val ResultsHeight = 150.dp

// The search field, the gap and the results band form one bottom anchored block,
// so the block's own margin is what is left of the field's 580dp bottom margin.
private val SearchBlockBottomMargin = SearchFieldBottomMargin - ResultsTopMargin - ResultsHeight

// 2dp start margin plus 5dp start padding on the search label.
private val SearchLabelStartInset = 7.dp

// Metrics of the AppCompat SearchView internals: a 48dp action button holding a
// 24dp icon, and query text styled with textAppearanceMedium.
private val SearchIconButtonWidth = 48.dp
private val SearchIconSize = 24.dp
private val SearchQueryTextColor = Color(0xDE000000)
private val SearchHintTextColor = Color(0x61000000)
private val SearchIconTint = Color(0x8A000000)

// Bare TextStyles keep the framework letter spacing and line height that the
// replaced TextViews used, instead of the MaterialTheme typography defaults.
private val AppNameStyle = TextStyle(
    color = OnScreenTextColor,
    fontSize = 22.sp,
    textAlign = TextAlign.Center
)

private val SearchLabelStyle = TextStyle(
    color = OnScreenTextColor,
    fontSize = 20.sp
)

private val ButtonTextStyle = TextStyle(
    color = ButtonTextColor,
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium,
    textAlign = TextAlign.Center
)

private val SearchQueryStyle = TextStyle(
    color = SearchQueryTextColor,
    fontSize = 18.sp
)

private val SearchHintStyle = SearchQueryStyle.copy(color = SearchHintTextColor)

// A ConstraintLayout bias of 0f..1f is the same placement as a Compose
// BiasAlignment of -1f..1f, so the title keeps adapting to the screen height.
private val AppNameAlignment = BiasAlignment(
    horizontalBias = 0f,
    verticalBias = 2f * 0.022f - 1f
)

/**
 * Home page search screen. Replaces `activity_home_page.xml`, owning the query
 * text and the debounce that the activity's query text listener used to drive.
 *
 * @param onSearchRequested called with the query to send to the search API.
 * @param onFoodClick called when a result is tapped.
 * @param onLogoutClick called when the existing Logout control is tapped.
 */
@Composable
fun HomePageScreen(
    foods: List<Food>,
    onSearchRequested: (String) -> Unit,
    onFoodClick: (Food) -> Unit,
    onFoodLogsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAccountClick: () -> Unit = {}
) {
    var query by rememberSaveable { mutableStateOf("") }
    var isSearchExpanded by rememberSaveable { mutableStateOf(false) }
    val currentOnSearchRequested by rememberUpdatedState(onSearchRequested)

    // Restarting on every edit cancels the pending search the same way the old
    // listener cancelled its job before scheduling a new one.
    LaunchedEffect(query) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.length >= MinimumSearchLength) {
            delay(SearchDebounce)
            currentOnSearchRequested(trimmedQuery)
        }
    }

    HomePageContent(
        foods = foods,
        query = query,
        isSearchExpanded = isSearchExpanded,
        onQueryChange = { query = it },
        onSearchExpand = { isSearchExpanded = true },
        // Submit still sends the raw, possibly blank, query and does not cancel
        // the typing debounce, matching the previous SearchView listener.
        onSearchSubmit = { onSearchRequested(query) },
        // Matches the SearchView close button: clear the query, then collapse.
        onSearchClose = { if (query.isEmpty()) isSearchExpanded = false else query = "" },
        onFoodClick = onFoodClick,
        onLogoutClick = onLogoutClick,
        onFoodLogsClick = onFoodLogsClick,
        onAccountClick = onAccountClick,
        modifier = modifier
    )
}

@Composable
private fun HomePageContent(
    foods: List<Food>,
    query: String,
    isSearchExpanded: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchExpand: () -> Unit,
    onSearchSubmit: () -> Unit,
    onSearchClose: () -> Unit,
    onFoodClick: (Food) -> Unit,
    onLogoutClick: () -> Unit,
    onFoodLogsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAccountClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            // Stands in for fitsSystemWindows on the replaced root layout.
            .windowInsetsPadding(WindowInsets.systemBars)
            .imePadding()
    ) {
        Text(
            text = stringResource(R.string.name),
            modifier = Modifier.align(AppNameAlignment),
            style = AppNameStyle
        )

        FlatButton(
            text = stringResource(R.string.logout),
            onClick = onLogoutClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = ButtonMargin, top = ButtonMargin)
        )

        FlatButton(
            text = stringResource(R.string.account),
            onClick = onAccountClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = ButtonMargin, end = ButtonMargin)
        )

        FlatButton(
            text = stringResource(R.string.food_logs),
            onClick = onFoodLogsClick,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 56.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(bottom = SearchBlockBottomMargin)
        ) {
            Text(
                text = stringResource(R.string.search),
                modifier = Modifier.padding(start = SearchLabelStartInset),
                style = SearchLabelStyle
            )

            SearchField(
                query = query,
                isExpanded = isSearchExpanded,
                onQueryChange = onQueryChange,
                onExpand = onSearchExpand,
                onSubmit = onSearchSubmit,
                onClose = onSearchClose
            )

            Spacer(modifier = Modifier.height(ResultsTopMargin))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ResultsHeight)
                    .background(SurfaceBackground)
            ) {
                items(foods) { food ->
                    FoodItem(
                        food = food,
                        onClick = { onFoodClick(food) }
                    )
                }
            }
        }
    }
}

/**
 * Flat button matching a Widget.AppCompat.Button.Borderless.Colored with its
 * background overridden to a solid colour, which left it without a ripple.
 */
@Composable
private fun FlatButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = ButtonMinWidth, minHeight = ButtonMinHeight)
            .background(SurfaceBackground)
            .then(
                if (onClick == null) {
                    Modifier
                } else {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        // The resting look stays a flat white rectangle; no ripple is added.
                        indication = null,
                        onClick = onClick
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = ButtonTextStyle
        )
    }
}

/**
 * Search input reproducing the two states of the AppCompat SearchView it
 * replaces: an icon at rest, and an editable field once that icon is tapped.
 */
@Composable
private fun SearchField(
    query: String,
    isExpanded: Boolean,
    onQueryChange: (String) -> Unit,
    onExpand: () -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            focusRequester.requestFocus()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SearchFieldHeight)
            .background(SurfaceBackground),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchIcon(
            imageVector = Icons.Default.Search,
            contentDescription = SearchContentDescription,
            onClick = if (isExpanded) null else onExpand
        )

        if (isExpanded) {
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = SearchHint,
                        style = SearchHintStyle
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = SearchQueryStyle,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                    cursorBrush = SolidColor(SearchQueryTextColor)
                )
            }

            SearchIcon(
                imageVector = Icons.Default.Close,
                contentDescription = CloseContentDescription,
                onClick = onClose
            )
        }
    }
}

@Composable
private fun SearchIcon(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(SearchIconButtonWidth)
            .fillMaxHeight()
            .then(
                if (onClick == null) {
                    Modifier
                } else {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(SearchIconSize),
            tint = SearchIconTint
        )
    }
}

private val PreviewFoods = listOf(
    sampleFood(
        foodId = "33691",
        foodName = "Greek Yogurt",
        brandName = "Chobani",
        foodDescription = "Per 170g - Calories: 120kcal | Fat: 0.00g | Carbs: 7.00g"
    ),
    sampleFood(
        foodId = "1234",
        foodName = "Banana",
        brandName = null,
        foodDescription = "Per 1 medium - Calories: 105kcal | Fat: 0.39g"
    ),
    sampleFood(
        foodId = "5678",
        foodName = "Grilled Chicken Breast",
        brandName = "Generic",
        foodDescription = "Per 100g - Calories: 165kcal | Fat: 3.57g"
    )
)

@Preview(showSystemUi = true)
@Composable
private fun HomePageCollapsedSearchPreview() {
    HomePageContent(
        foods = PreviewFoods,
        query = "",
        isSearchExpanded = false,
        onQueryChange = {},
        onSearchExpand = {},
        onSearchSubmit = {},
        onSearchClose = {},
        onFoodClick = {},
        onLogoutClick = {},
        onFoodLogsClick = {}
    )
}

@Preview(showSystemUi = true)
@Composable
private fun HomePageExpandedSearchPreview() {
    HomePageContent(
        foods = PreviewFoods,
        query = "yogurt",
        isSearchExpanded = true,
        onQueryChange = {},
        onSearchExpand = {},
        onSearchSubmit = {},
        onSearchClose = {},
        onFoodClick = {},
        onLogoutClick = {},
        onFoodLogsClick = {}
    )
}

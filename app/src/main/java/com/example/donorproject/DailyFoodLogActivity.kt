package com.example.donorproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.example.donorproject.data.local.AppDatabase
import com.example.donorproject.data.local.FoodLogEntity
import com.example.donorproject.ui.theme.DOnorProjectTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class DailyFoodLogActivity : ComponentActivity() {

    private val entries = mutableStateOf<List<FoodLogEntity>>(emptyList())

    private val userId by lazy {
        intent.getIntExtra(EXTRA_USER_ID, -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DOnorProjectTheme {
                DailyFoodLogScreen(
                    entries = entries.value,
                    onDeleteEntry = ::deleteEntry
                )
            }
        }

        loadToday()
    }

    private fun loadToday() {
        if (userId <= 0) {
            return
        }

        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)

        val start = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        lifecycleScope.launch {
            entries.value = AppDatabase.getInstance(this@DailyFoodLogActivity).foodLogDao().findByUserAndTimeRange(userId, start, end)
        }
    }

    private fun deleteEntry(entryId: Int) {
        lifecycleScope.launch {
            AppDatabase.getInstance(this@DailyFoodLogActivity).foodLogDao().deleteForUser(entryId, userId)

            loadToday()
        }
    }

    companion object {
        private const val EXTRA_USER_ID = "user_id"

        fun createIntent(
            context: Context,
            userId: Int
        ): Intent {
            return Intent(context, DailyFoodLogActivity::class.java).putExtra(EXTRA_USER_ID, userId)
        }
    }
}
package com.example.donorproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.donorproject.data.local.AppDatabase
import com.example.donorproject.ui.theme.DOnorProjectTheme

class AccountActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra(EXTRA_USER_ID, -1)

        if (userId <= 0) {
            startActivity(MainActivity.createIntentAfterLogout(this))
            finish()
            return
        }

        val accountDao = AppDatabase.getInstance(this).accountDao()

        enableEdgeToEdge()

        setContent {
            DOnorProjectTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AccountScreen(
                        userId = userId,
                        accountDao = accountDao,
                        onBackClick = { finish() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .imePadding()
                    )
                }
            }
        }
    }

    companion object {
        private const val EXTRA_USER_ID = "account_user_id"

        fun createIntent(context: Context, userId: Int): Intent {
            return Intent(context, AccountActivity::class.java).apply {
                putExtra(EXTRA_USER_ID, userId)
            }
        }
    }
}
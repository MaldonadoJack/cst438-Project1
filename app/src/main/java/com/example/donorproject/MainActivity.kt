package com.example.donorproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.donorproject.data.local.AppDatabase
import androidx.compose.ui.Modifier
import com.example.donorproject.ui.theme.DOnorProjectTheme

internal enum class Screen { LANDING, LOGIN, SIGN_UP }

internal fun startScreen(showLogin: Boolean): Screen =
    if (showLogin) Screen.LOGIN else Screen.LANDING

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val initialScreen = startScreen(intent.getBooleanExtra(EXTRA_SHOW_LOGIN, false))

        setContent {
            DOnorProjectTheme {
                val userDao = remember { AppDatabase.getInstance(this).userDao() }
                var currentScreen by rememberSaveable { mutableStateOf(initialScreen) }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            Screen.LANDING -> LandingPage(
                                onLoginClick = { currentScreen = Screen.LOGIN },
                                onSignUpClick = { currentScreen = Screen.SIGN_UP }
                            )

                            Screen.LOGIN -> LoginScreen(
                                userDao = userDao,
                                onLoginSuccess = {
                                    startActivity(Intent(this@MainActivity, HomePage::class.java))
                                    finish()
                                },
                                onBackClick = { currentScreen = Screen.LANDING }
                            )
                            Screen.SIGN_UP -> SignUpScreen(
                                userDao = userDao,
                                onGoToLoginClick = { currentScreen = Screen.LOGIN },
                                onBackClick = { currentScreen = Screen.LANDING }
                            )
                        }

                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_SHOW_LOGIN = "com.example.donorproject.SHOW_LOGIN"

        /**
         * Opens [MainActivity] on the login screen and replaces the current task.
         * The app has no stored session; clearing the signed-in [HomePage] task is
         * what signs the user out.
         */
        fun createIntentAfterLogout(context: Context): Intent {
            return Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(EXTRA_SHOW_LOGIN, true)
            }
        }
    }
}
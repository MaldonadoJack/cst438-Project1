package com.example.donorproject

import android.os.Bundle
import android.content.Intent
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

private enum class Screen { LANDING, LOGIN, SIGN_UP }
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DOnorProjectTheme {
                val userDao = remember { AppDatabase.getInstance(this).userDao() }
                var currentScreen by rememberSaveable { mutableStateOf(Screen.LANDING) }

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
}
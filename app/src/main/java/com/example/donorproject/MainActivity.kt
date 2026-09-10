package com.example.donorproject

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.donorproject.ui.theme.DOnorProjectTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DOnorProjectTheme {
                var showLoginScreen by rememberSaveable {
                    mutableStateOf(false)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (showLoginScreen) {
                            LoginScreen(
                                onLoginSubmit = { _, _ ->
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Authentication will be connected later",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onBackClick = {
                                    showLoginScreen = false
                                }
                            )
                        } else {
                            LandingPage(
                                onLoginClick = {
                                    showLoginScreen = true
                                },
                                onSignUpClick = {
                                    // Account creation is outside this issue.
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
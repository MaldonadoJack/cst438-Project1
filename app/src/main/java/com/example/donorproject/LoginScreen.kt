package com.example.donorproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.donorproject.data.local.UserDao
import com.example.donorproject.data.local.UserEntity
import com.example.donorproject.ui.theme.DOnorProjectTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    userDao: UserDao,                              // <-- new
    onLoginSuccess: (username: String) -> Unit,    // <-- replaces onLoginSubmit
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Log In",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = null
                passwordError = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") },
            singleLine = true,
            isError = usernameError != null,
            supportingText = {
                usernameError?.let { Text(it) }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null,
            supportingText = {
                passwordError?.let { Text(it) }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = !isSubmitting,
            onClick = {
                isSubmitting = true

                coroutineScope.launch {
                    val outcome = AccountAuthenticator.logIn(
                        username = username,
                        password = password,
                        userDao = userDao
                    )

                    when (outcome) {
                        is LoginOutcome.Success -> {
                            usernameError = null
                            passwordError = null
                            password = ""
                            onLoginSuccess(outcome.username)
                        }

                        is LoginOutcome.Rejected -> {
                            usernameError = outcome.errors.usernameError
                            passwordError = outcome.errors.passwordError
                        }
                    }

                    isSubmitting = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackClick,
            enabled = !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
private object PreviewUserDao : UserDao {
    override suspend fun insert(user: UserEntity): Long = 0L

    override suspend fun findByUsername(username: String): UserEntity? = null
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    DOnorProjectTheme {
        LoginScreen(
            userDao = PreviewUserDao,
            onLoginSuccess = {},
            onBackClick = {}
        )
    }
}
package com.example.donorproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.donorproject.data.local.AccountDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun AccountScreen(
    userId: Int,
    accountDao: AccountDao,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errors by remember {
        mutableStateOf(AccountUpdateValidationResult())
    }
    var message by remember { mutableStateOf<String?>(null) }
    var messageIsError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var accountLoaded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(userId, accountDao) {
        try {
            val user = accountDao.findById(userId)
            if (user == null) {
                message = "Account not found. Please log out and log in again."
                messageIsError = true
            } else {
                username = user.username
                accountLoaded = true
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            message = "Unable to load your account. Go back and try again."
            messageIsError = true
        } finally {
            isLoading = false
        }
    }

    val canEdit = accountLoaded && !isLoading && !isSubmitting

    Column(
        modifier = modifier
            .background(Color(0xFFF5F1E8))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Update Account",
            color = Color(0xFF355E3B),
            style = MaterialTheme.typography.headlineLarge
        )

        if (isLoading) {
            Text("Loading account…")
        }

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errors = errors.copy(usernameError = null)
                message = null
            },
            label = { Text("Username") },
            enabled = canEdit,
            singleLine = true,
            isError = errors.usernameError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7FAF8A),
                unfocusedBorderColor = Color(0xFF7FAF8A),
                focusedLabelColor = Color(0xFF355E3B),
                unfocusedLabelColor = Color(0xFF4F514B)),
            supportingText = {
                errors.usernameError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Leave both password fields empty to keep your current password.")

        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                errors = errors.copy(
                    passwordError = null,
                    confirmPasswordError = null
                )
                message = null
            },
            label = { Text("New password") },
            enabled = canEdit,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = errors.passwordError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7FAF8A),
                unfocusedBorderColor = Color(0xFF7FAF8A),
                focusedLabelColor = Color(0xFF355E3B),
                unfocusedLabelColor = Color(0xFF4F514B)
            ),
            supportingText = {
                errors.passwordError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errors = errors.copy(confirmPasswordError = null)
                message = null
            },
            label = { Text("Confirm new password") },
            enabled = canEdit,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = errors.confirmPasswordError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7FAF8A),
                unfocusedBorderColor = Color(0xFF7FAF8A),
                focusedLabelColor = Color(0xFF355E3B),
                unfocusedLabelColor = Color(0xFF4F514B)
            ),
            supportingText = {
                errors.confirmPasswordError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        message?.let {
            Text(
                text = it,
                color = if (messageIsError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }

        Button(
            enabled = canEdit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7FAF8A),
                contentColor = Color.White
            ),
            onClick = {
                isSubmitting = true
                message = null
                errors = AccountUpdateValidationResult()

                scope.launch {
                    try {
                        when (
                            val outcome = AccountUpdater.updateAccount(
                                userId = userId,
                                username = username,
                                newPassword = newPassword,
                                confirmPassword = confirmPassword,
                                accountDao = accountDao
                            )
                        ) {
                            is AccountUpdateOutcome.Updated -> {
                                username = outcome.username
                                newPassword = ""
                                confirmPassword = ""
                                message = "Account updated successfully."
                                messageIsError = false
                            }

                            is AccountUpdateOutcome.Rejected -> {
                                errors = outcome.errors
                            }

                            AccountUpdateOutcome.UserNotFound -> {
                                accountLoaded = false
                                message = "Account not found. Please log out and log in again."
                                messageIsError = true
                            }
                        }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (exception: Exception) {
                        message = "Unable to save changes. Please try again."
                        messageIsError = true
                    } finally {
                        isSubmitting = false
                    }
                }
            }
        ) {
            Text(if (isSubmitting) "Saving…" else "Save Changes")
        }

        OutlinedButton(
            onClick = onBackClick,
            enabled = !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
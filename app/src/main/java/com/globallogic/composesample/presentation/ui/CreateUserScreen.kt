package com.globallogic.composesample.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globallogic.composesample.presentation.state.CreateUserEvent
import com.globallogic.composesample.presentation.viewmodels.CreateUserViewModel

/**
 * Create User Screen demonstrating form handling and validation in Compose.
 *
 * This screen showcases:
 * 1. **Form State Management**: Using ViewModel for form state
 * 2. **Real-time Validation**: Validation as user types
 * 3. **rememberSaveable**: Surviving configuration changes
 * 4. **Side Effects**: Navigation after successful creation
 * 5. **Error Handling**: Field-level and form-level error display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    viewModel: CreateUserViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show success message and navigate back
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("User created successfully!")
            onNavigateBack()
        }
    }

    // Show error messages
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create User") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onEvent(CreateUserEvent.NavigateBack)
                        onNavigateBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    LoadingState()
                }
                else -> {
                    CreateUserForm(
                        state = state,
                        onEvent = viewModel::onEvent
                    )
                }
            }
        }
    }
}

/**
 * Form composable demonstrating declarative form handling.
 */
@Composable
private fun CreateUserForm(
    state: com.globallogic.composesample.presentation.state.CreateUserState,
    onEvent: (CreateUserEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Name field with real-time validation
        OutlinedTextField(
            value = state.name,
            onValueChange = { name ->
                onEvent(CreateUserEvent.UpdateName(name))
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.hasNameError,
            supportingText = {
                state.nameError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true
        )

        // Email field with real-time validation
        OutlinedTextField(
            value = state.email,
            onValueChange = { email ->
                onEvent(CreateUserEvent.UpdateEmail(email))
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.hasEmailError,
            supportingText = {
                state.emailError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        // Submit button - disabled when form is invalid or loading
        Button(
            onClick = { onEvent(CreateUserEvent.Submit) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.canSubmit
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create User")
            }
        }
    }
}

/**
 * Loading state for form submission.
 */
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Creating user...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

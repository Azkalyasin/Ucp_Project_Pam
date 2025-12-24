package com.example.ucp_project_pam.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.ucp_project_pam.viewmodel.ProfileViewModel
import com.example.ucp_project_pam.modeldata.ProfileUiState

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    when (uiState) {
        is ProfileUiState.Loading -> {
            CircularProgressIndicator()
        }

        is ProfileUiState.Success -> {
            val user = (uiState as ProfileUiState.Success).user

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Profil",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Nama : ${user.name}")
                Text("Email: ${user.email}")
                Text("Role : ${user.role}")

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = onLogout) {
                    Text("Logout")
                }
            }
        }

        is ProfileUiState.Error -> {
            Text(
                text = (uiState as ProfileUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }

        else -> {}
    }
}

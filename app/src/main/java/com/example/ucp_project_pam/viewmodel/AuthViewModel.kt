package com.example.ucp_project_pam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.*
import com.example.ucp_project_pam.repositori.auth.RepositoryAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repositoryAuth: RepositoryAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = repositoryAuth.login(
                LoginRequest(email, password)
            )

            _uiState.value = result.fold(
                onSuccess = { response ->
                    AuthUiState.Success(response.data.user)
                },
                onFailure = { AuthUiState.Error(it.message ?: "Login gagal") }
            )
        }
    }

    fun register(
        name: String,
        email: String,
        phone: String?,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = repositoryAuth.register(
                RegisterRequest(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password,
                    confirmPassword = confirmPassword
                )
            )

            _uiState.value = result.fold(
                onSuccess = { response ->
                    AuthUiState.Success(response.data.user)
                },
                onFailure = { AuthUiState.Error(it.message ?: "Registrasi gagal") }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

package com.example.ucp_project_pam.viewmodel.provider


import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ucp_project_pam.repositori.AplikasiUmkm
import com.example.ucp_project_pam.viewmodel.AuthViewModel
import com.example.ucp_project_pam.viewmodel.ProfileViewModel

// Ambil Application dari CreationExtras
fun CreationExtras.aplikasiUmkm(): AplikasiUmkm =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
            as AplikasiUmkm

object PenyediaViewModel {

    val Factory = viewModelFactory {

        // 🔐 AuthViewModel
        initializer {
            AuthViewModel(
                aplikasiUmkm().container.repositoryAuth
            )
        }

        // 👤 ProfileViewModel
        initializer {
            ProfileViewModel(
                aplikasiUmkm().container.repositoryAuth
            )
        }
    }
}

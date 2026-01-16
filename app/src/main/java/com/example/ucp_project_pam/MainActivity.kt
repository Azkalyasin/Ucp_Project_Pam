package com.example.ucp_project_pam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ucp_project_pam.repositori.AplikasiUmkm
import com.example.ucp_project_pam.uicontroller.PetaNavigasi
import com.example.ucp_project_pam.ui.theme.ucp_project_pamTheme
import com.example.ucp_project_pam.viewmodel.AuthViewModel
import com.example.ucp_project_pam.viewmodel.ProfileViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ucp_project_pamTheme {
                val navController = rememberNavController()

                val authViewModel: AuthViewModel = viewModel(
                    factory = PenyediaViewModel.Factory
                )

                val profileViewModel: ProfileViewModel = viewModel(
                    factory = PenyediaViewModel.Factory
                )

                PetaNavigasi(
                    navController = navController,
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel
                )
            }
        }
    }
}


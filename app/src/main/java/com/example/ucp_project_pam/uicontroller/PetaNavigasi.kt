package com.example.ucp_project_pam.uicontroller

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ucp_project_pam.view.customer.HomeScreenCustomer
import com.example.ucp_project_pam.view.admin.HomeScreenAdmin
import com.example.ucp_project_pam.view.LoginScreen
import com.example.ucp_project_pam.view.ProfileScreen
import com.example.ucp_project_pam.view.RegisterScreen
import com.example.ucp_project_pam.viewmodel.AuthViewModel
import com.example.ucp_project_pam.viewmodel.ProfileViewModel

@Composable
fun PetaNavigasi(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // 🔐 LOGIN
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { role ->
                    if (role == "ADMIN") {
                        navController.navigate("home_admin") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home_user") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // 📝 REGISTER
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 🏠 HOME ADMIN
        composable("home_admin") {
            HomeScreenAdmin(
                onProfileClick = {
                    navController.navigate("profile")
                },
                onLogout = {
                    authViewModel.resetState()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        // 🏠 HOME CUSTOMER
        composable("home_user") {
            HomeScreenCustomer(
                onProfileClick = {
                    navController.navigate("profile")
                },
                onLogout = {
                    authViewModel.resetState()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        // 👤 PROFILE
        composable("profile") {
            ProfileScreen(
                viewModel = profileViewModel,
                onLogout = {
                    authViewModel.resetState()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}

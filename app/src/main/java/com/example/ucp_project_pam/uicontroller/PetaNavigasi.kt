package com.example.ucp_project_pam.uicontroller

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ucp_project_pam.view.customer.HomeScreenCustomer
import com.example.ucp_project_pam.view.admin.HomeScreenAdmin
import com.example.ucp_project_pam.view.admin.category.CategoryListScreen
import com.example.ucp_project_pam.view.admin.category.CategoryFormScreen
import com.example.ucp_project_pam.view.admin.category.CategoryDetailScreen
import com.example.ucp_project_pam.view.admin.menu.MenuListScreen
import com.example.ucp_project_pam.view.admin.menu.MenuFormScreen
import com.example.ucp_project_pam.view.admin.menu.MenuDetailScreen
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
                onCategoryClick = {
                    navController.navigate("category_list")
                },
                onMenuClick = { // ✅ Tambahkan ini
                    navController.navigate("menu_list")
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

        // ==================== CATEGORY ROUTES ====================

        // 📂 CATEGORY LIST
        composable("category_list") {
            CategoryListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddCategory = {
                    navController.navigate("category_form")
                },
                onItemClick = { categoryId ->
                    navController.navigate("category_detail/$categoryId")
                },
                onEditCategory = { categoryId ->
                    navController.navigate("category_form/$categoryId")
                }
            )
        }

        // 👁️ CATEGORY DETAIL
        composable(
            route = "category_detail/{categoryId}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            CategoryDetailScreen(
                categoryId = categoryId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate("category_form/$categoryId")
                }
            )
        }

        // ➕ CATEGORY FORM (CREATE)
        composable("category_form") {
            CategoryFormScreen(
                categoryId = null,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // ✏️ CATEGORY FORM (EDIT)
        composable(
            route = "category_form/{categoryId}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId")
            CategoryFormScreen(
                categoryId = categoryId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // ==================== MENU ROUTES ✅ BARU ====================

        // 🍽️ MENU LIST
        composable("menu_list") {
            MenuListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddMenu = {
                    navController.navigate("menu_form")
                },
                onItemClick = { menuId ->
                    navController.navigate("menu_detail/$menuId")
                },
                onEditMenu = { menuId ->
                    navController.navigate("menu_form/$menuId")
                }
            )
        }

        // 👁️ MENU DETAIL
        composable(
            route = "menu_detail/{menuId}",
            arguments = listOf(
                navArgument("menuId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val menuId = backStackEntry.arguments?.getInt("menuId") ?: 0
            MenuDetailScreen(
                menuId = menuId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditClick = { id ->
                    navController.navigate("menu_form/$id")
                }
            )
        }

        // ➕ MENU FORM (CREATE)
        composable("menu_form") {
            MenuFormScreen(
                menuId = null,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // ✏️ MENU FORM (EDIT)
        composable(
            route = "menu_form/{menuId}",
            arguments = listOf(
                navArgument("menuId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val menuId = backStackEntry.arguments?.getInt("menuId")
            MenuFormScreen(
                menuId = menuId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}
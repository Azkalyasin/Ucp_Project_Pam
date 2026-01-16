package com.example.ucp_project_pam.uicontroller

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ucp_project_pam.view.customer.*
import com.example.ucp_project_pam.view.admin.*
import com.example.ucp_project_pam.view.admin.category.*
import com.example.ucp_project_pam.view.admin.menu.*
import com.example.ucp_project_pam.view.admin.order.*
import com.example.ucp_project_pam.view.*
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

        // ==================== AUTH ====================

        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { role ->
                    if (role == "ADMIN") {
                        navController.navigate("home_admin") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home_customer") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

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

        // ==================== ADMIN HOME ====================

        composable("home_admin") {
            HomeScreenAdmin(
                onProfileClick = {
                    navController.navigate("profile")
                },
                onCategoryClick = {
                    navController.navigate("category_list")
                },
                onMenuClick = {
                    navController.navigate("menu_list")
                },
                onOrderClick = { // ✅ TAMBAH
                    navController.navigate("admin_order_list")
                },
                onLogout = {
                    authViewModel.resetState()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        // ==================== CUSTOMER HOME ====================

        composable("home_customer") {
            HomeScreenCustomer(
                onProfileClick = {
                    navController.navigate("profile")
                },
                onMenuClick = {
                    navController.navigate("customer_menu_list")
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                onOrdersClick = { // ✅ TAMBAH
                    navController.navigate("customer_order_list")
                },
                onLogout = {
                    authViewModel.resetState()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        // ==================== PROFILE ====================

        composable("profile") {
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.resetState()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        // ==================== CATEGORY ROUTES (ADMIN) ====================

        composable("category_list") {
            CategoryListScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddCategory = { navController.navigate("category_form") },
                onItemClick = { categoryId ->
                    navController.navigate("category_detail/$categoryId")
                },
                onEditCategory = { categoryId ->
                    navController.navigate("category_form/$categoryId")
                }
            )
        }

        composable(
            route = "category_detail/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            CategoryDetailScreen(
                categoryId = categoryId,
                onNavigateBack = { navController.popBackStack() },
                onEditClick = { id -> navController.navigate("category_form/$id") }
            )
        }

        composable("category_form") {
            CategoryFormScreen(
                categoryId = null,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = "category_form/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId")
            CategoryFormScreen(
                categoryId = categoryId,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // ==================== MENU ROUTES (ADMIN) ====================

        composable("menu_list") {
            MenuListScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddMenu = { navController.navigate("menu_form") },
                onItemClick = { menuId -> navController.navigate("menu_detail/$menuId") },
                onEditMenu = { menuId -> navController.navigate("menu_form/$menuId") }
            )
        }

        composable(
            route = "menu_detail/{menuId}",
            arguments = listOf(navArgument("menuId") { type = NavType.IntType })
        ) { backStackEntry ->
            val menuId = backStackEntry.arguments?.getInt("menuId") ?: 0
            MenuDetailScreen(
                menuId = menuId,
                onNavigateBack = { navController.popBackStack() },
                onEditClick = { id -> navController.navigate("menu_form/$id") }
            )
        }

        composable("menu_form") {
            MenuFormScreen(
                menuId = null,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = "menu_form/{menuId}",
            arguments = listOf(navArgument("menuId") { type = NavType.IntType })
        ) { backStackEntry ->
            val menuId = backStackEntry.arguments?.getInt("menuId")
            MenuFormScreen(
                menuId = menuId,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // ==================== CUSTOMER MENU ROUTES ====================

        composable("customer_menu_list") {
            CustomerMenuListScreen(
                onNavigateBack = { navController.popBackStack() },
                onMenuClick = { menuId ->
                    navController.navigate("customer_menu_detail/$menuId")
                },
                onCartClick = { navController.navigate("cart") }
            )
        }

        composable(
            route = "customer_menu_detail/{menuId}",
            arguments = listOf(navArgument("menuId") { type = NavType.IntType })
        ) { backStackEntry ->
            val menuId = backStackEntry.arguments?.getInt("menuId") ?: 0
            CustomerMenuDetailScreen(
                menuId = menuId,
                onNavigateBack = { navController.popBackStack() },
                onCartClick = { navController.navigate("cart") }
            )
        }

        // ==================== CART ROUTES ====================

        composable("cart") {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onBrowseMenu = {
                    navController.navigate("customer_menu_list") {
                        popUpTo("home_customer")
                    }
                },
                onCheckout = { navController.navigate("checkout") } // ✅ UPDATE
            )
        }

        // ==================== ORDER ROUTES (CUSTOMER) ✅ BARU ====================

        // Checkout
        composable("checkout") {
            CheckoutScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderSuccess = { orderId ->
                    navController.navigate("customer_order_detail/$orderId") {
                        popUpTo("home_customer")
                    }
                }
            )
        }

        // Customer Order List
        composable("customer_order_list") {
            CustomerOrderListScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate("customer_order_detail/$orderId")
                }
            )
        }

        // Customer Order Detail
        composable(
            route = "customer_order_detail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            CustomerOrderDetailScreen(
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== ORDER ROUTES (ADMIN) ✅ BARU ====================

        // Admin Order List
        composable("admin_order_list") {
            AdminOrderListScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate("admin_order_detail/$orderId")
                }
            )
        }

        // Admin Order Detail
        composable(
            route = "admin_order_detail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            AdminOrderDetailScreen(
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
package com.louis.mycomposeproject.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.louis.mycomposeproject.Dependencies
import com.louis.mycomposeproject.data.local.PreferencesManager
import com.louis.mycomposeproject.presentation.ViewModelFactory
import com.louis.mycomposeproject.presentation.home.HomeScreen
import com.louis.mycomposeproject.presentation.login.LoginScreen
import com.louis.mycomposeproject.presentation.register.RegisterScreen
import com.louis.mycomposeproject.presentation.splash.SplashScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val startDestination = Routes.SPLASH
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onTimeout = {
                    val destination = if (Dependencies.isLoggedIn()) Routes.HOME else Routes.LOGIN
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.LOGIN) {
            val viewModel = viewModel<com.louis.mycomposeproject.presentation.login.LoginViewModel>(
                factory = ViewModelFactory { Dependencies.provideLoginViewModel() }
            )
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.REGISTER) {
            val viewModel = viewModel<com.louis.mycomposeproject.presentation.register.RegisterViewModel>(
                factory = ViewModelFactory { Dependencies.provideRegisterViewModel() }
            )
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.popBackStack() }
            )
        }
        
        composable(Routes.HOME) {
            HomeScreen(
                userEmail = PreferencesManager(context).getEmail(),
                onLogout = {
                    Dependencies.logoutUseCase()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

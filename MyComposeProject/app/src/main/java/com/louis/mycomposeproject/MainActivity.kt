package com.louis.mycomposeproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.louis.mycomposeproject.presentation.ViewModelFactory
import com.louis.mycomposeproject.presentation.home.HomeScreen
import com.louis.mycomposeproject.presentation.login.LoginScreen
import com.louis.mycomposeproject.presentation.register.RegisterScreen
import com.louis.mycomposeproject.ui.theme.MyComposeProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dependencies.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            MyComposeProjectTheme {
                val navController = rememberNavController()
                val startDestination = if (Dependencies.isLoggedIn()) "home" else "login"
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            val viewModel = viewModel<com.louis.mycomposeproject.presentation.login.LoginViewModel>(
                                factory = ViewModelFactory { Dependencies.provideLoginViewModel() }
                            )
                            LoginScreen(
                                viewModel = viewModel,
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = { 
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable("register") {
                            val viewModel = viewModel<com.louis.mycomposeproject.presentation.register.RegisterViewModel>(
                                factory = ViewModelFactory { Dependencies.provideRegisterViewModel() }
                            )
                            RegisterScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = { navController.popBackStack() },
                                onRegisterSuccess = { navController.popBackStack() }
                            )
                        }
                        
                        composable("home") {
                            HomeScreen(
                                userEmail = com.louis.mycomposeproject.data.local.PreferencesManager(applicationContext).getEmail(),
                                onLogout = {
                                    Dependencies.logoutUseCase()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
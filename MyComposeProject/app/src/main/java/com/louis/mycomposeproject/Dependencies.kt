package com.louis.mycomposeproject

import android.content.Context
import com.louis.mycomposeproject.data.local.PreferencesManager
import com.louis.mycomposeproject.data.remote.ApiClient
import com.louis.mycomposeproject.data.repository.AuthRepositoryImpl
import com.louis.mycomposeproject.domain.repository.AuthRepository
import com.louis.mycomposeproject.domain.usecase.LoginUseCase
import com.louis.mycomposeproject.domain.usecase.LogoutUseCase
import com.louis.mycomposeproject.domain.usecase.RegisterUseCase
import com.louis.mycomposeproject.presentation.login.LoginViewModel
import com.louis.mycomposeproject.presentation.register.RegisterViewModel

object Dependencies {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var authRepository: AuthRepository
    
    fun init(context: Context) {
        preferencesManager = PreferencesManager(context)
        authRepository = AuthRepositoryImpl(ApiClient.authApi, preferencesManager)
    }
    
    private val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository)
    }
    
    private val registerUseCase: RegisterUseCase by lazy {
        RegisterUseCase(authRepository)
    }
    
    val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCase(authRepository)
    }
    
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
    
    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel(loginUseCase)
    }
    
    fun provideRegisterViewModel(): RegisterViewModel {
        return RegisterViewModel(registerUseCase)
    }
}

package com.louis.mycomposeproject.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.louis.mycomposeproject.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()
    
    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _state.value = RegisterState(isLoading = true)
            registerUseCase(email, password, name)
                .onSuccess {
                    _state.value = RegisterState(isSuccess = true)
                }
                .onFailure {
                    _state.value = RegisterState(error = it.message)
                }
        }
    }
}

package com.louis.myarchitecture.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.louis.myarchitecture.data.repository.NewsRepository
import com.louisgeek.library.data.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _uiState: MutableLiveData<NewsUiState> = MutableLiveData()
    val uiState = _uiState
    private val _uiEvent: SingleLiveEvent<NewsUIEvent> = SingleLiveEvent()
    val uiEvent = _uiEvent

    private val _loading: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val loading = _loading.asStateFlow()

//    private val _uiState = MutableStateFlow(AddEditTaskUiState())
//    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    private val _uiStateFlow = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiStateFlow: StateFlow<NewsUiState> = _uiStateFlow

    //UIIntent 可以省略
    fun fetchNews() {
        _uiState.value = NewsUiState.Loading
        viewModelScope.launch {
            val hoemList = repository.getHomeList()
            _uiState.value = NewsUiState.Success(hoemList)
        }
    }

    fun clickTv() {
        _uiEvent.value = NewsUIEvent.ShowMessage("test click")
    }

//    fun getHomeList() {
//        viewModelScope.launch {
//            repository.getHomeList()
//                .onStart { emit(Result.Loading) }
//                .onCompletion { }
//        }
//    }
}
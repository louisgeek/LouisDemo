package com.louis.myarchitecture.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.louis.myarchitecture.R
import com.louisgeek.library.tool.ToastTool
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    companion object {
        fun newInstance() = NewsFragment()
    }

    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tv: TextView = view.findViewById(R.id.tv)
        tv.setOnClickListener {
            viewModel.clickTv()
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { uiEvent ->
            when (uiEvent) {
                is NewsUIEvent.ShowMessage -> {
                    Toast.makeText(requireContext(), uiEvent.message, Toast.LENGTH_SHORT).show()
                }

            }

        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is NewsUiState.Loading -> {

                }

                is NewsUiState.Success -> {
                    uiState.news
                }

                is NewsUiState.Error -> {
                    uiState.msg
                }
            }

        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateFlow //StateFlow
                    .map { it } // Flow
                    .distinctUntilChanged() //过滤重复元素
                    .collect { uiState ->
                        when (uiState) {
                            is NewsUiState.Loading -> {}
                            is NewsUiState.Success -> {

                            }

                            is NewsUiState.Error -> {

                            }
                        }
                    }
            }
        }

        lifecycleScope.launch {
            viewModel.uiStateFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED) //简化代码
                .collect { uiState ->
                    when (uiState) {
                        is NewsUiState.Loading -> {}
                        is NewsUiState.Success -> {

                        }

                        is NewsUiState.Error -> {

                        }
                    }
                }
        }

        viewModel.fetchNews()

    }
}
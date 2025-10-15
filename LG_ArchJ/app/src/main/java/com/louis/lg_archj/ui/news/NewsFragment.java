package com.louis.lg_archj.ui.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.louis.lg_archj.di.AppContainer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.louis.lg_archj.databinding.FragmentNewsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FragmentNewsBinding binding;
    private NewsViewModel viewModel;
    private NewsAdapter newsAdapter;

    public NewsFragment() {
    }

    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // 初始化 ViewModel 使用依赖注入
        AppContainer container = AppContainer.getInstance();
        NewsViewModelFactory factory = new NewsViewModelFactory(container.getLoadNewsUseCase());
        viewModel = new ViewModelProvider(this, factory).get(NewsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 RecyclerView
        initRecyclerView();

        // 设置下拉刷新监听器
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.processIntent(new NewsUiIntent.RefreshData());
        });

        // 观察 UI 状态变化
        viewModel.uiState.observe(getViewLifecycleOwner(), this::render);

        // 发送初始加载数据的意图
        viewModel.processIntent(new NewsUiIntent.LoadData());
    }

    private void initRecyclerView() {
        newsAdapter = new NewsAdapter();
        binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.newsRecyclerView.setAdapter(newsAdapter);
    }

    private void render(NewsUiState state) {
        if (state.isLoading()) {
            //显示加载状态
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.newsRecyclerView.setVisibility(View.GONE);
            binding.errorText.setVisibility(View.GONE);
        } else if (state.isRefreshing()) {
            //显示刷新状态
            binding.progressBar.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(true);
            binding.newsRecyclerView.setVisibility(View.VISIBLE);
            binding.errorText.setVisibility(View.GONE);
        } else if (state.error != null) {
            // 显示错误状态
            binding.progressBar.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.newsRecyclerView.setVisibility(View.GONE);
            binding.errorText.setVisibility(View.VISIBLE);
            binding.errorText.setText(state.error);
        } else if (state.data != null) {
            // 显示数据
            binding.progressBar.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.newsRecyclerView.setVisibility(View.VISIBLE);
            binding.errorText.setVisibility(View.GONE);

            newsAdapter.updateNews(state.data);
        }
    }
}
package com.louis.lg_archj.ui.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.louis.lg_archj.R;
import com.louis.lg_archj.domain.model.News;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> dataList = new ArrayList<>();

    public void updateNews(List<News> list) {
        if (list != null) {
            dataList.clear();
            dataList.addAll(list);
        }
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = dataList.get(position);
        holder.bind(news);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
        }

        public void bind(News news) {
            titleText.setText(news.getTitle());
        }
    }
}
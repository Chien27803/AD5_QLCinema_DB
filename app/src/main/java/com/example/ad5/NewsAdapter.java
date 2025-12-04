package com.example.ad5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<News> newsList;
    private OnNewsClickListener listener;

    public interface OnNewsClickListener {
        void onNewsClick(News news);
    }

    public NewsAdapter(Context context, List<News> newsList, OnNewsClickListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);

        holder.tvNewsTitle.setText(news.getTitle());
        holder.tvNewsSummary.setText(news.getSummary());
        holder.tvNewsDate.setText(formatDate(news.getCreated_at()));

        // Load ảnh bằng Glide
        Glide.with(context)
                .load(news.getImage())
                .placeholder(R.drawable.ic_movie_placeholder)
                .error(R.drawable.ic_movie_placeholder)
                .into(holder.imgNews);

        // TODO: Thêm sự kiện click để mở chi tiết tin tức nếu cần
        holder.cardNews.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNewsClick(news);
            }
        });
    }

    private String formatDate(String dateTime) {
        // Giả định created_at là định dạng TEXT mặc định (YYYY-MM-DD HH:MM:SS)
        if (dateTime == null || dateTime.length() < 10) return "N/A";
        return dateTime.substring(0, 10); // Lấy phần ngày
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardNews;
        ImageView imgNews;
        TextView tvNewsTitle, tvNewsSummary, tvNewsDate;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNews = itemView.findViewById(R.id.cardNews);
            imgNews = itemView.findViewById(R.id.imgNews);
            tvNewsTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvNewsSummary = itemView.findViewById(R.id.tvNewsSummary);
            tvNewsDate = itemView.findViewById(R.id.tvNewsDate);
        }
    }
}
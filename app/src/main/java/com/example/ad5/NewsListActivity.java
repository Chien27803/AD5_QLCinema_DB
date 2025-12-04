package com.example.ad5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NewsListActivity extends AppCompatActivity implements NewsAdapter.OnNewsClickListener{

    private RecyclerView rvNews;
    private ImageView btnBack;
    private LinearLayout layoutEmpty;
    private NewsAdapter newsAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        dbHelper = new DBHelper(this);

        initViews();
        setupRecyclerView();
        loadNews();
        setupClickListeners();
    }

    private void initViews() {
        rvNews = findViewById(R.id.rvNews);
        btnBack = findViewById(R.id.btnBack);
        layoutEmpty = findViewById(R.id.layoutEmpty);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvNews.setLayoutManager(layoutManager);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadNews() {
        List<News> newsList = dbHelper.getAllNews();

        if (newsList.isEmpty()) {
            rvNews.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvNews.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            // Cập nhật: Truyền 'this' (NewsListActivity) làm listener
            newsAdapter = new NewsAdapter(this, newsList, this);
            rvNews.setAdapter(newsAdapter);
        }
    }
    @Override
    public void onNewsClick(News news) {
        Intent intent = new Intent(NewsListActivity.this, NewsDetailActivity.class);
        intent.putExtra("news_item", news); // Truyền đối tượng News qua Intent
        startActivity(intent);
    }
}
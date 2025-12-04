package com.example.ad5;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView imgNewsDetail;
    private TextView tvNewsTitleDetail;
    private TextView tvNewsDateDetail;
    private TextView tvNewsContentDetail;
    private News newsItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Nhận đối tượng News từ Intent
        newsItem = (News) getIntent().getSerializableExtra("news_item");

        if (newsItem == null) {
            Toast.makeText(this, "Không tìm thấy thông tin tin tức", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        displayNewsDetail();
        setupClickListeners();
    }

    private void initViews() {
        // Lấy view trong Toolbar và Content
        btnBack = findViewById(R.id.btnBack);
        imgNewsDetail = findViewById(R.id.imgNewsDetail);
        tvNewsTitleDetail = findViewById(R.id.tvNewsTitleDetail);
        tvNewsDateDetail = findViewById(R.id.tvNewsDateDetail);
        tvNewsContentDetail = findViewById(R.id.tvNewsContentDetail);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void displayNewsDetail() {
        // Title
        tvNewsTitleDetail.setText(newsItem.getTitle());

        // Date: format lại từ YYYY-MM-DD HH:MM:SS sang YYYY-MM-DD
        String dateString = newsItem.getCreated_at();
        if (dateString != null && dateString.length() >= 10) {
            dateString = dateString.substring(0, 10);
        }
        tvNewsDateDetail.setText("📅 Ngày đăng: " + dateString);

        // Content
        tvNewsContentDetail.setText(newsItem.getContent());

        // Image (Load bằng Glide)
        Glide.with(this)
                .load(newsItem.getImage())
                .placeholder(R.drawable.ic_movie_placeholder)
                .error(R.drawable.ic_movie_placeholder)
                .into(imgNewsDetail);
    }
}

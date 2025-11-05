package com.example.ad5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView imgBackdrop, imgPoster, btnBack, btnFavorite, btnPlay;
    private TextView tvMovieTitle, tvMovieType, tvRating, tvLanguage, tvReleaseDate, tvStatus, tvDescription;
    private ExtendedFloatingActionButton btnBookTicket;

    private Movie movie;
    private User currentUser;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Nhận dữ liệu từ Intent
        movie = (Movie) getIntent().getSerializableExtra("movie");
        currentUser = (User) getIntent().getSerializableExtra("user");

        if (movie == null) {
            Toast.makeText(this, "Không tìm thấy thông tin phim", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        displayMovieInfo();
    }

    private void initViews() {
        imgBackdrop = findViewById(R.id.imgBackdrop);
        imgPoster = findViewById(R.id.imgPoster);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnPlay = findViewById(R.id.btnPlay);

        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvMovieType = findViewById(R.id.tvMovieType);
        tvRating = findViewById(R.id.tvRating);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        tvStatus = findViewById(R.id.tvStatus);
        tvDescription = findViewById(R.id.tvDescription);

        btnBookTicket = findViewById(R.id.btnBookTicket);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Favorite button
        btnFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            if (isFavorite) {
                btnFavorite.setImageResource(R.drawable.ic_favorite);
                Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                Toast.makeText(this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
            }
        });

        // Play trailer button
        btnPlay.setOnClickListener(v -> {
            Toast.makeText(this, "Phát trailer: " + movie.getMovie_name(), Toast.LENGTH_SHORT).show();
            // TODO: Mở video trailer
        });

        // Book ticket button
        btnBookTicket.setOnClickListener(v -> {
            if (currentUser != null && currentUser.getUser_id() > 0) {
                // TODO: Chuyển đến BookingActivity
                Toast.makeText(this, "Đặt vé cho: " + movie.getMovie_name(), Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(MovieDetailActivity.this, BookingActivity.class);
                // intent.putExtra("movie", movie);
                // intent.putExtra("user", currentUser);
                // startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để đặt vé", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MovieDetailActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void displayMovieInfo() {
        // Set movie info
        tvMovieTitle.setText(movie.getMovie_name());
        tvMovieType.setText(movie.getMovie_type());
        tvRating.setText(String.format("%.1f", movie.getPoint()));
        tvLanguage.setText(movie.getLanguage());

        // Format release date (chỉ lấy năm)
        String releaseDate = movie.getRelease_date();
        if (releaseDate != null && releaseDate.length() >= 4) {
            tvReleaseDate.setText(releaseDate.substring(0, 4));
        } else {
            tvReleaseDate.setText("N/A");
        }

        // Set status với icon và màu phù hợp
        String status = movie.getStatus();
        if ("Đang chiếu".equals(status)) {
            tvStatus.setText("🎬 " + status);
            tvStatus.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_green_dark));
        } else if ("Sắp chiếu".equals(status)) {
            tvStatus.setText("🔜 " + status);
            tvStatus.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_orange_dark));
        } else {
            tvStatus.setText("🚫 " + status);
            tvStatus.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        }

        tvDescription.setText(movie.getDescription());

        // TODO: Load images với Glide
        // Glide.with(this).load(movie.getImage()).into(imgPoster);
        // Glide.with(this).load(movie.getImage()).into(imgBackdrop);

        // Tạm thời dùng placeholder
        imgPoster.setImageResource(R.drawable.ic_movie_placeholder);
        imgBackdrop.setImageResource(R.drawable.ic_movie_placeholder);
    }
}

package com.example.ad5;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView rvNowShowing, rvComingSoon;
    private MovieAdapter nowShowingAdapter, comingSoonAdapter;
    private DBHelper dbHelper;
    private TextView tvUsername, tvSeeAllNowShowing, tvSeeAllComingSoon;
    private User currentUser;
    private BottomNavigationView bottomNav;

    // Quick access cards
    private MaterialCardView cardQuickBooking, cardPromotion, cardNews, cardMyTickets;
    private View searchBar;
    private ImageView btnNotification, imgBanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo database
        dbHelper = new DBHelper(this);

        // Nhận thông tin user từ Intent
        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null) {
            // Nếu chưa đăng nhập, tạo user demo
            currentUser = new User();
            currentUser.setUsername("Guest User");
            currentUser.setUser_id(0);
        }

        initViews();
        setupClickListeners();
        setupRecyclerViews();
        loadMovies();
        setupBottomNav();

        // Hiển thị tên người dùng
        tvUsername.setText(currentUser.getUsername());
    }

    private void initViews() {
        // Header views
        tvUsername = findViewById(R.id.tvUsername);
        btnNotification = findViewById(R.id.btnNotification);
        searchBar = findViewById(R.id.searchBar);
        imgBanner = findViewById(R.id.imgBanner);

        // Quick access cards
        cardQuickBooking = findViewById(R.id.cardQuickBooking);
        cardPromotion = findViewById(R.id.cardPromotion);
        cardNews = findViewById(R.id.cardNews);
        cardMyTickets = findViewById(R.id.cardMyTickets);

        // RecyclerViews
        rvNowShowing = findViewById(R.id.rvNowShowing);
        rvComingSoon = findViewById(R.id.rvComingSoon);

        // See all buttons
        tvSeeAllNowShowing = findViewById(R.id.tvSeeAllNowShowing);
        tvSeeAllComingSoon = findViewById(R.id.tvSeeAllComingSoon);

        // Bottom navigation
        bottomNav = findViewById(R.id.bottomNav);
    }

    private void setupClickListeners() {

        // Search bar
        searchBar.setOnClickListener(v -> {
            Toast.makeText(this, "Mở trang tìm kiếm", Toast.LENGTH_SHORT).show();
            // TODO: startActivity(new Intent(this, SearchActivity.class));
        });

        // Notification
        btnNotification.setOnClickListener(v -> {
            Toast.makeText(this, "Thông báo", Toast.LENGTH_SHORT).show();
        });

        // Quick booking
        cardQuickBooking.setOnClickListener(v -> {
            Toast.makeText(this, "Đặt vé nhanh", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, AllMoviesActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });

        // Promotions
        cardPromotion.setOnClickListener(v -> {
            Toast.makeText(this, "Khuyến mãi đang cập nhật", Toast.LENGTH_SHORT).show();
        });

        // News
        cardNews.setOnClickListener(v -> {
            Toast.makeText(this, "Tin tức điện ảnh", Toast.LENGTH_SHORT).show();
        });

        // My tickets
        cardMyTickets.setOnClickListener(v -> {
            if (currentUser.getUser_id() > 0) {
                // TODO: Intent intent = new Intent(this, MyTicketsActivity.class);
                // intent.putExtra("user", currentUser);
                // startActivity(intent);
                Toast.makeText(this, "Vé của bạn", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            }
        });

        // See all buttons
        tvSeeAllNowShowing.setOnClickListener(v -> {
            Toast.makeText(this, "Xem tất cả phim đang chiếu", Toast.LENGTH_SHORT).show();
        });

        tvSeeAllComingSoon.setOnClickListener(v -> {
            Toast.makeText(this, "Xem tất cả phim sắp chiếu", Toast.LENGTH_SHORT).show();
        });

        // Banner
        imgBanner.setOnClickListener(v -> {
            Toast.makeText(this, "Xem chi tiết khuyến mãi", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerViews() {
        // Now Showing - Horizontal scroll
        LinearLayoutManager layoutManagerNowShowing = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        rvNowShowing.setLayoutManager(layoutManagerNowShowing);

        // Coming Soon - Grid 2 columns
        GridLayoutManager layoutManagerComingSoon = new GridLayoutManager(this, 2);
        rvComingSoon.setLayoutManager(layoutManagerComingSoon);
    }

    private void loadMovies() {
        // Lấy danh sách phim đang chiếu
        List<Movie> nowShowingMovies = dbHelper.getMoviesByStatus("Đang chiếu");

        if (nowShowingMovies.isEmpty()) {
            Toast.makeText(this, "Không có phim nào đang chiếu", Toast.LENGTH_SHORT).show();
        }

        nowShowingAdapter = new MovieAdapter(this, nowShowingMovies, movie -> {
            // Click vào phim -> Xem chi tiết
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });
        rvNowShowing.setAdapter(nowShowingAdapter);

        // Lấy danh sách phim sắp chiếu
        List<Movie> comingSoonMovies = dbHelper.getMoviesByStatus("Sắp chiếu");
        comingSoonAdapter = new MovieAdapter(this, comingSoonMovies, movie -> {
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });
        rvComingSoon.setAdapter(comingSoonAdapter);
    }

    private void setupBottomNav() {
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Đang ở trang chủ
                return true;
            } else if (id == R.id.nav_tickets) {
                // Xem vé của tôi
                if (currentUser.getUser_id() > 0) {
                    Intent intent = new Intent(this, MyTicketsActivity.class);
                    intent.putExtra("user", currentUser);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (id == R.id.nav_profile) {
                // Trang cá nhân
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo bottom nav chọn đúng item
        bottomNav.setSelectedItemId(R.id.nav_home);
    }
}

package com.example.ad5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class ProfileActivity extends AppCompatActivity {

    private User currentUser;
    private BottomNavigationView bottomNav;

    // Views cho user đã đăng nhập
    private LinearLayout layoutLoggedIn;
    private TextView tvProfileName, tvProfileEmail, tvProfilePhone, tvProfileAddress, tvProfileRole;
    private ImageView imgAvatar;
    private Button btnLogout;

    // Views cho user chưa đăng nhập
    private LinearLayout layoutGuest;
    private Button btnLogin;

    // Menu items
    private MaterialCardView cardHistory, cardSettings, cardHelp, cardAbout;

    // callback cho nút back
    private OnBackPressedCallback backCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Nhận thông tin user từ Intent
        currentUser = (User) getIntent().getSerializableExtra("user");

        initViews();
        setupBottomNav();
        setupClickListeners();
        setupBackPressed();

        // Hiển thị layout phù hợp
        if (currentUser != null && currentUser.getUser_id() > 0) {
            showLoggedInLayout();
        } else {
            showGuestLayout();
        }
    }

    private void initViews() {
        // Bottom navigation
        bottomNav = findViewById(R.id.bottomNav);

        // Layout đã đăng nhập
        layoutLoggedIn = findViewById(R.id.layoutLoggedIn);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        tvProfileAddress = findViewById(R.id.tvProfileAddress);
        tvProfileRole = findViewById(R.id.tvProfileRole);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnLogout = findViewById(R.id.btnLogout);

        // Layout chưa đăng nhập
        layoutGuest = findViewById(R.id.layoutGuest);
        btnLogin = findViewById(R.id.btnLogin);

        // Menu items
        cardHistory = findViewById(R.id.cardHistory);
        cardSettings = findViewById(R.id.cardSettings);
        cardHelp = findViewById(R.id.cardHelp);
        cardAbout = findViewById(R.id.cardAbout);
    }

    private void showLoggedInLayout() {
        layoutLoggedIn.setVisibility(View.VISIBLE);
        layoutGuest.setVisibility(View.GONE);

        // Hiển thị thông tin user
        tvProfileName.setText(currentUser.getUsername());
        tvProfileEmail.setText(currentUser.getEmail());

        String phone = currentUser.getPhone();
        tvProfilePhone.setText(phone != null && !phone.isEmpty() ? phone : "Chưa cập nhật");

        String address = currentUser.getAddress();
        tvProfileAddress.setText(address != null && !address.isEmpty() ? address : "Chưa cập nhật");

        // Hiển thị role
        if (currentUser.isAdmin()) {
            tvProfileRole.setText("👑 Quản trị viên");
            tvProfileRole.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            tvProfileRole.setText("👤 Người dùng");
            tvProfileRole.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        // Set avatar (tạm thời dùng icon mặc định)
        imgAvatar.setImageResource(R.drawable.ic_avatar);
    }

    private void showGuestLayout() {
        layoutLoggedIn.setVisibility(View.GONE);
        layoutGuest.setVisibility(View.VISIBLE);
    }

    private void setupClickListeners() {
        // Nút đăng xuất
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        Toast.makeText(ProfileActivity.this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // Nút đăng nhập
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Lịch sử đặt vé
        cardHistory.setOnClickListener(v -> {
            if (currentUser != null && currentUser.getUser_id() > 0) {
                Toast.makeText(this, "Lịch sử đặt vé", Toast.LENGTH_SHORT).show();
                // TODO: chuyển sang BookingHistoryActivity
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử", Toast.LENGTH_SHORT).show();
            }
        });

        // Cài đặt
        cardSettings.setOnClickListener(v -> {
            if (currentUser != null && currentUser.getUser_id() > 0) {
                Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
                // TODO: SettingsActivity
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            }
        });

        // Trợ giúp
        cardHelp.setOnClickListener(v -> {
            Toast.makeText(this, "Trợ giúp & Hỗ trợ", Toast.LENGTH_SHORT).show();
            // TODO: HelpActivity
        });

        // Về chúng tôi
        cardAbout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Về chúng tôi")
                    .setMessage("Cinema App\nPhiên bản: 1.0.0\n\nỨng dụng đặt vé xem phim trực tuyến\n\n© 2024 Cinema App. All rights reserved.")
                    .setPositiveButton("Đóng", null)
                    .show();
        });
    }

    private void setupBottomNav() {
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                if (currentUser != null) {
                    intent.putExtra("user", currentUser);
                }
                startActivity(intent);
                finish();
                return true;

            } else if (id == R.id.nav_tickets) {
                if (currentUser != null && currentUser.getUser_id() > 0) {
                    Toast.makeText(this, "Vé của tôi", Toast.LENGTH_SHORT).show();
                    // TODO: MyTicketsActivity
                } else {
                    Toast.makeText(this, "Vui lòng đăng nhập để xem vé", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;

            } else if (id == R.id.nav_profile) {
                return true; // đang ở Profile
            }
            return false;
        });
    }

    private void setupBackPressed() {
        // xử lý nút Back hệ thống
        backCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backToMain();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_profile);
    }

    private void backToMain() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        if (currentUser != null) {
            intent.putExtra("user", currentUser);
        }
        startActivity(intent);
        finish();
    }
}

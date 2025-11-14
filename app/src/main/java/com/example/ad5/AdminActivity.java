package com.example.ad5;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    TextView tvAdminInfo;
    Button btnManageMovies, btnManageRooms, btnManageUsers, btnStatistics, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        // Ánh xạ Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bật nút back (mũi tên ←)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Xử lý khi nhấn nút back
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        // 🔹 Ánh xạ view
        tvAdminInfo = findViewById(R.id.tvAdminInfo);
        btnManageMovies = findViewById(R.id.btnManageMovies);
        btnManageRooms = findViewById(R.id.btnManageRooms);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnLogout = findViewById(R.id.btnLogout);

        // 🧩 Lấy thông tin admin (truyền từ LoginActivity)
        Intent intent = getIntent();
        String adminName = intent.getStringExtra("username");
        if (adminName == null || adminName.isEmpty()) adminName = "Admin";

        tvAdminInfo.setText("Xin chào " + adminName);

        // 👆 Khi bấm vào admin → mở Popup menu
        tvAdminInfo.setOnClickListener(this::showAdminMenu);

        // ⚙️ Các nút chức năng
        btnManageMovies.setOnClickListener(v -> {
            // Thay Toast bằng lệnh khởi động Activity
            Intent movieIntent = new Intent(AdminActivity.this, MovieManagementActivity.class);
            startActivity(movieIntent);
            // (Optional) Toast.makeText(this, "🎞️ Mở trang quản lý phim", Toast.LENGTH_SHORT).show();
        });
        // *************************************************



        btnManageRooms.setOnClickListener(v -> {
            Intent roomIntent = new Intent(AdminActivity.this, RoomManagementActivity.class); // Đổi tên biến
            startActivity(roomIntent);
        });

        // ✅ Khi bấm “Quản lý người dùng” → chuyển sang trang UserListActivity
        btnManageUsers.setOnClickListener(v -> {
            Intent userIntent = new Intent(AdminActivity.this, UserListActivity.class);
            startActivity(userIntent);
        });

        // ✅ Khi nhấn "Xem thống kê" → mở StatisticsActivity
        btnStatistics.setOnClickListener(v -> {
            Intent statisticsIntent = new Intent(AdminActivity.this, StatisticsActivity.class);
            startActivity(statisticsIntent);
        });
    }

    // 🔸 Hiển thị menu khi bấm vào tên admin
    private void showAdminMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.menu_admin, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onAdminMenuItemClick);
        popupMenu.show();
    }

    // 🔸 Xử lý khi chọn menu item
    private boolean onAdminMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
            Intent logoutIntent = new Intent(AdminActivity.this, LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            finish();
            return true;
        }
        return false;
    }
}

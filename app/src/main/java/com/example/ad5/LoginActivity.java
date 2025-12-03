package com.example.ad5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar; //

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnLogin, btnRegister;
    DBHelper dbHelper;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 🎯 1. Ánh xạ và Thiết lập Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            // Hiển thị nút quay lại
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Ẩn tiêu đề mặc định
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 🎯 2. XỬ LÝ SỰ KIỆN NHẤN NÚT QUAY LẠI
        toolbar.setNavigationOnClickListener(v -> {
            // Phương thức này đóng Activity hiện tại và quay lại Activity trước đó
            onBackPressed();
        });

        // Ánh xạ view
        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // 👉 Xử lý khi bấm nút "Đăng nhập"
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etUser.getText().toString().trim();
                String pass = etPass.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 🔍 Kiểm tra thông tin đăng nhập trong DB
                User user = dbHelper.authenticate(email, pass);

                if (user != null) {
                    Toast.makeText(LoginActivity.this,
                            "Đăng nhập thành công! Xin chào " + user.getUsername(),
                            Toast.LENGTH_SHORT).show();

                    // 🎯 SỬA LỖI #1: LƯU USER ID VÀO SESSION (SharedPreferences)
                    // Đây là bước CỰC KỲ QUAN TRỌNG để các Activity khác có thể lấy ID người dùng
                    SessionManager.saveLoggedInUserId(LoginActivity.this, user.getUser_id());

                    // 🎯 SỬA LỖI #2: CHUẨN BỊ INTENT (Truyền đối tượng User)
                    Intent intent;
                    if (user.getRole() != null && user.getRole().equalsIgnoreCase("admin")) {
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }

                    // Thay thế putExtra("username") bằng putExtra("user") để truyền đối tượng đầy đủ
                    // Điều này giúp MainActivity có thể đọc user.getUser_id()
                    intent.putExtra("user", user);

                    startActivity(intent);
                    finish(); // đóng LoginActivity
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 👉 Xử lý khi bấm "Đăng ký"
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
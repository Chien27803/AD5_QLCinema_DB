package com.example.ad5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnLogin, btnRegister;
    DBHelper dbHelper; // 🔹 thêm dòng này để dùng database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

                    // Chuyển sang MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
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

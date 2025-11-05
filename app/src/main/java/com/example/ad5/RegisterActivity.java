package com.example.ad5;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    DBHelper db;
    TextInputEditText etUsername, etEmail, etPhone, etAddress, etPass, etRePass;
    MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DBHelper(this);

        // Ánh xạ view
        etUsername = findViewById(R.id.etFullName); // bạn có thể đổi id này trong layout thành etUsername cho đúng
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPass = findViewById(R.id.etPass);
        etRePass = findViewById(R.id.etRePass);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            String rePass = etRePass.getText().toString().trim();

            // ✅ Kiểm tra nhập đủ thông tin
            if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || pass.isEmpty() || rePass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Kiểm tra mật khẩu khớp
            if (!pass.equals(rePass)) {
                Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Thêm người dùng mới
            boolean isSuccess = db.addUser(username, email, phone, address, pass);
            if (isSuccess) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Email đã tồn tại hoặc có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

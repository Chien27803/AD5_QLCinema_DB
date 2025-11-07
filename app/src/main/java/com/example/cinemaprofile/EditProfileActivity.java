package com.example.cinemaprofile;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    EditText etPhone, etName, etEmail, etBirthdate, etAddress;
    RadioGroup rgGender;
    Spinner spCity, spDistrict;
    Button btnUpdate;
    DatabaseHelper db;
    String gender = "Nam";
    Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = new DatabaseHelper(this);
        mapping();

        // City + District mẫu
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Hải Dương", "Hà Nội", "TP.HCM"});
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCity.setAdapter(cityAdapter);

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Huyện Kinh Môn", "Quận 1", "Quận 3"});
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistrict.setAdapter(districtAdapter);

        // Chọn ngày sinh bằng lịch
        etBirthdate.setOnClickListener(v -> showDatePicker());

        // Lưu giới tính
        rgGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMale) gender = "Nam";
            else if (checkedId == R.id.rbFemale) gender = "Nữ";
            else gender = "Khác";
        });

        // Tải dữ liệu đã lưu (nếu có)
        loadUserData();

        // Xử lý khi nhấn "Cập nhật"
        btnUpdate.setOnClickListener(v -> validateAndConfirm());
    }

    /** Hiển thị lịch chọn ngày */
    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            selectedDate.set(y, m, d);
            etBirthdate.setText(String.format("%02d/%02d/%d", d, m + 1, y));
        }, year, month, day);
        dialog.show();
    }

    /** Kiểm tra dữ liệu người dùng nhập */
    private void validateAndConfirm() {
        boolean isValid = true;

        // Reset lỗi
        etPhone.setError(null);
        etName.setError(null);
        etEmail.setError(null);
        etBirthdate.setError(null);
        etAddress.setError(null);

        String phone = etPhone.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String birthdate = etBirthdate.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        int selectedGenderId = rgGender.getCheckedRadioButtonId();

        // Kiểm tra rỗng và hợp lệ
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        } else if (!phone.matches("\\d{10}")) {
            etPhone.setError("Số điện thoại phải gồm đúng 10 chữ số");
            isValid = false;
        }

        if (TextUtils.isEmpty(name)) {
            etName.setError("Vui lòng nhập họ và tên");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            isValid = false;
        }

        if (TextUtils.isEmpty(birthdate)) {
            etBirthdate.setError("Vui lòng chọn ngày sinh");
            isValid = false;
        } else {
            // Kiểm tra ngày sinh không được lớn hơn ngày hiện tại
            Calendar today = Calendar.getInstance();
            if (selectedDate.after(today)) {
                etBirthdate.setError("Ngày sinh phải trước ngày hiện tại");
                isValid = false;
            }
        }

        if (selectedGenderId == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Vui lòng nhập địa chỉ");
            isValid = false;
        }

        if (!isValid) return;

        // Hiển thị hộp thoại xác nhận
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn cập nhật thông tin?")
                .setPositiveButton("Xác nhận", (dialog, which) -> saveToDatabase())
                .setNegativeButton("Hủy", null)
                .show();
    }

    /** Lưu dữ liệu xuống SQLite */
    private void saveToDatabase() {
        User user = new User(
                etPhone.getText().toString(),
                etName.getText().toString(),
                etEmail.getText().toString(),
                etBirthdate.getText().toString(),
                gender,
                spCity.getSelectedItem().toString(),
                spDistrict.getSelectedItem().toString(),
                etAddress.getText().toString()
        );

        db.insertOrUpdate(user);
        Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
    }

    /** Ánh xạ view */
    private void mapping() {
        etPhone = findViewById(R.id.etPhone);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etBirthdate = findViewById(R.id.etBirthdate);
        etAddress = findViewById(R.id.etAddress);
        rgGender = findViewById(R.id.rgGender);
        spCity = findViewById(R.id.spCity);
        spDistrict = findViewById(R.id.spDistrict);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    /** Tự động điền dữ liệu người dùng nếu đã lưu */
    private void loadUserData() {
        User user = db.getAnyUser();
        if (user != null) {
            etPhone.setText(user.phone);
            etName.setText(user.name);
            etEmail.setText(user.email);
            etBirthdate.setText(user.birthdate);
            etAddress.setText(user.address);

            if (user.gender != null) {
                if (user.gender.equals("Nam")) rgGender.check(R.id.rbMale);
                else if (user.gender.equals("Nữ")) rgGender.check(R.id.rbFemale);
                else rgGender.check(R.id.rbOther);
                gender = user.gender;
            }

            setSpinnerSelection(spCity, user.city);
            setSpinnerSelection(spDistrict, user.district);
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}

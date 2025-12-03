package com.example.ad5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class ShowtimeActivity extends AppCompatActivity {

    private static final String TAG = "ShowtimeActivity";
    private RecyclerView rvShowtimes;
    // Đã đổi tên để phản ánh mục đích chung
    private TextView tvActivityTitleGeneral;
    private DBHelper dbHelper;
    private ShowtimeAdapter showtimeAdapter;
    private FloatingActionButton fabAddShowtime;

    private List<Room> roomList; // Danh sách phòng
    private List<Movie> movieList; // 💡 MỚI: Danh sách phim
    private androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtime_list);
        dbHelper = new DBHelper(this);

        // 💡 KHÔNG CẦN NHẬN MOVIE_ID TỪ INTENT NỮA

        initViews();
        loadAllData(); // 💡 Tải cả Phim và Phòng
        loadAllShowtimes(); // 💡 Tải tất cả suất chiếu
        setupToolbar();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarshowtime);
        rvShowtimes = findViewById(R.id.rcvShowtime);
        // Ánh xạ TextView tiêu đề chung
        tvActivityTitleGeneral = findViewById(R.id.tv_activity_title_general);
        fabAddShowtime = findViewById(R.id.fab_add_showtime);

        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo Adapter với ArrayList rỗng
        showtimeAdapter = new ShowtimeAdapter(this, new ArrayList<>());
        rvShowtimes.setAdapter(showtimeAdapter);

        fabAddShowtime.setOnClickListener(v -> showAddShowtimeDialog());
    }
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút mũi tên
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Ẩn tiêu đề nếu cần
        }

        // 🎯 Xử lý sự kiện khi nhấn nút mũi tên quay lại trên Toolbar
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed(); // Quay về Activity trước đó
        });
    }

    // 💡 PHƯƠNG THỨC MỚI: Tải Tên Phim và Danh sách Phòng (Cần cho Dialog)
    private void loadAllData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Tải danh sách phim và phòng
            movieList = dbHelper.getAllMovies();
            roomList = dbHelper.getAllRooms();

            new Handler(Looper.getMainLooper()).post(() -> {
                if (movieList == null || movieList.isEmpty()) {
                    Toast.makeText(this, "Chưa có phim nào trong hệ thống.", Toast.LENGTH_SHORT).show();
                }
                if (roomList == null || roomList.isEmpty()) {
                    Toast.makeText(this, "Chưa có phòng chiếu nào trong hệ thống.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Tải TẤT CẢ suất chiếu
     */
    private void loadAllShowtimes() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 💡 SỬA LỖI: Gọi phương thức mới để lấy TẤT CẢ suất chiếu
            List<Showtime> showtimeList = dbHelper.getAllShowtimes();

            new Handler(Looper.getMainLooper()).post(() -> {
                if (showtimeList != null && !showtimeList.isEmpty()) {
                    showtimeAdapter.updateList(showtimeList);
                } else {
                    Toast.makeText(this, "Không tìm thấy suất chiếu nào.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    // 💡 PHƯƠNG THỨC MỚI: Hiển thị Dialog thêm suất chiếu
    private void showAddShowtimeDialog() {
        if (roomList == null || roomList.isEmpty() || movieList == null || movieList.isEmpty()) {
            Toast.makeText(this, "Chưa tải xong dữ liệu (Phim/Phòng). Vui lòng đợi hoặc thêm dữ liệu.", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_showtime, null);
        builder.setView(dialogView);
        builder.setTitle("Thêm Suất Chiếu Mới");

        // Ánh xạ View trong Dialog
        Spinner spMovie = dialogView.findViewById(R.id.sp_movie_dialog); // 💡 MỚI: Spinner chọn Phim
        Spinner spRoom = dialogView.findViewById(R.id.sp_room_dialog);
        TextView tvShowDate = dialogView.findViewById(R.id.tv_show_date_dialog);
        TextView tvStartTime = dialogView.findViewById(R.id.tv_start_time_dialog);
        TextView tvEndTime = dialogView.findViewById(R.id.tv_end_time_dialog);
        EditText edtPrice = dialogView.findViewById(R.id.edt_price_dialog);

        // Đổ dữ liệu
        populateMovieSpinner(spMovie, movieList); // 💡 Đổ dữ liệu phim
        populateRoomSpinner(spRoom, roomList);

        // Xử lý chọn Ngày/Giờ
        tvShowDate.setOnClickListener(v -> showDatePicker(tvShowDate));
        tvStartTime.setOnClickListener(v -> showTimePicker(tvStartTime));
        tvEndTime.setOnClickListener(v -> showTimePicker(tvEndTime));

        builder.setPositiveButton("Lưu", (dialog, id) -> {
            saveShowtime(spMovie, spRoom, tvShowDate, tvStartTime, tvEndTime, edtPrice);
        });
        builder.setNegativeButton("Hủy", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Phương thức helper đổ dữ liệu vào Spinner Phim
    private void populateMovieSpinner(Spinner spinner, List<Movie> movies) {
        List<String> movieNames = new ArrayList<>();
        for (Movie movie : movies) {
            movieNames.add(movie.getMovie_name());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, movieNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // Phương thức helper đổ dữ liệu vào Spinner Phòng
    private void populateRoomSpinner(Spinner spinner, List<Room> rooms) {
        List<String> roomNames = new ArrayList<>();
        for (Room room : rooms) {
            roomNames.add(room.getRoom_name() + " (" + room.getQuantity_seat() + " ghế)");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roomNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // Phương thức helper chọn Ngày
    private void showDatePicker(TextView textView) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    textView.setText(date);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    // Phương thức helper chọn Giờ
    private void showTimePicker(TextView textView) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    textView.setText(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    // 💡 PHƯƠNG THỨC MỚI: Xử lý Lưu suất chiếu
    private void saveShowtime(Spinner spMovie, Spinner spRoom, TextView tvDate, TextView tvStart, TextView tvEnd, EditText edtPrice) {
        String date = tvDate.getText().toString().trim();
        String startTime = tvStart.getText().toString().trim();
        String endTime = tvEnd.getText().toString().trim();
        String priceText = edtPrice.getText().toString().trim();

        if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy Movie ID
        int moviePosition = spMovie.getSelectedItemPosition();
        if (moviePosition < 0 || moviePosition >= movieList.size()) return;
        int selectedMovieId = movieList.get(moviePosition).getMovie_id();

        // Lấy Room ID
        int roomPosition = spRoom.getSelectedItemPosition();
        if (roomPosition < 0 || roomPosition >= roomList.size()) return;
        int selectedRoomId = roomList.get(roomPosition).getRoom_id();

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá vé không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi DB để thêm suất chiếu
        Executors.newSingleThreadExecutor().execute(() -> {
            long result = dbHelper.addShowtime(selectedMovieId, selectedRoomId, startTime, endTime, price, date);

            new Handler(Looper.getMainLooper()).post(() -> {
                if (result > 0) {
                    Toast.makeText(this, "✅ Thêm suất chiếu thành công!", Toast.LENGTH_LONG).show();
                    loadAllShowtimes(); // Tải lại TẤT CẢ danh sách
                } else {
                    Toast.makeText(this, "❌ Lỗi: Thêm suất chiếu thất bại. (Trùng lịch,...) ", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
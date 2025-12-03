package com.example.ad5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

// Đổi tên class để phù hợp với vai trò
public class AllShowtimesActivity extends AppCompatActivity implements UserShowtimeAdapter.OnItemClickListener {

    private RecyclerView rvShowtimes;
    private DBHelper dbHelper;
    private UserShowtimeAdapter userShowtimeAdapter;

    // TextView để hiển thị số lượng suất chiếu
    private TextView tvShowtimeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng layout list lớn đã sửa đổi (ví dụ: activity_all_showtimes)
        setContentView(R.layout.activity_all_showtime);

        dbHelper = new DBHelper(this);

        initViews();
        loadAllShowtimes(); // Tải tất cả suất chiếu khi khởi tạo
    }

    private void initViews() {
        rvShowtimes = findViewById(R.id.rvShowtimes); // ID từ layout list lớn
        tvShowtimeCount = findViewById(R.id.tvShowtimeCount); // ID từ layout list lớn

        // Thiết lập RecyclerView
        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter với listener là chính Activity này (this)
        userShowtimeAdapter = new UserShowtimeAdapter(this, new ArrayList<>(), this);
        rvShowtimes.setAdapter(userShowtimeAdapter);

        // TODO: Thiết lập click listener cho btnBack và btnSearch trên Toolbar
        // Ánh xạ các thành phần Toolbar
        ImageView btnBack = findViewById(R.id.btnBack_);


// Xử lý nút Back (đã thảo luận trước đó)
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void loadAllShowtimes() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Gọi hàm DB đã sửa lỗi
            List<Showtime> showtimeList = dbHelper.getAllShowtimes();

            new Handler(Looper.getMainLooper()).post(() -> {
                if (showtimeList != null && !showtimeList.isEmpty()) {
                    userShowtimeAdapter.updateList(showtimeList);
                    tvShowtimeCount.setText("Tìm thấy " + showtimeList.size() + " suất chiếu");
                } else {
                    userShowtimeAdapter.updateList(new ArrayList<>());
                    tvShowtimeCount.setText("Không tìm thấy suất chiếu nào");
                    Toast.makeText(this, "Không có suất chiếu nào đang hoạt động.", Toast.LENGTH_SHORT).show();
                    // TODO: Hiển thị layoutEmpty nếu cần
                }
            });
        });
    }

    // 💡 Xử lý sự kiện khi người dùng nhấn nút Đặt vé
    @Override
    public void onBookClick(Showtime showtime) {
        // Chuyển sang màn hình chọn ghế (SeatSelectionActivity)
        Toast.makeText(this, "Đang đặt vé cho phim: " + showtime.getMovie_name() + " (" + showtime.getStart_time() + ")", Toast.LENGTH_SHORT).show();

        Intent bookingIntent = new Intent(this, SeatSelectionActivity.class); // Thay thế bằng Activity chọn ghế thực tế
        bookingIntent.putExtra("SHOWTIME_ID", showtime.getShowtime_id());

        // Nếu cần, truyền thêm dữ liệu khác
        // bookingIntent.putExtra("MOVIE_NAME", showtime.getMovie_name());

        startActivity(bookingIntent);
    }
}
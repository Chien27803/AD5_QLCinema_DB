package com.example.ad5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

// Bạn cần đảm bảo import SessionManager ở đầu file
// import com.example.ad5.SessionManager;

public class SeatSelectionActivity extends AppCompatActivity implements SeatAdapter.OnSeatSelectedListener {

    private static final String TAG = "SeatSelectionActivity";
    private DBHelper dbHelper;
    private int showtimeId;
    private Showtime currentShowtime;
    private List<Seat> allSeatsInRoom; // Tất cả ghế trong phòng
    private List<Seat> selectedSeats;  // Ghế người dùng chọn

    private RecyclerView rvSeats;
    private TextView tvMovieTitle, tvShowtimeInfo, tvSeatInfo, tvTotalPrice;
    private Button btnContinueBooking;
    private SeatAdapter seatAdapter;

    private final DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection); // Cần tạo layout này

        dbHelper = new DBHelper(this);
        selectedSeats = new ArrayList<>();

        // 1. Nhận Showtime ID
        showtimeId = getIntent().getIntExtra("SHOWTIME_ID", -1);

        if (showtimeId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy suất chiếu.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadShowtimeAndSeats();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chọn Ghế");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        rvSeats = findViewById(R.id.rv_seats);
        tvMovieTitle = findViewById(R.id.tv_movie_title_seat);
        tvShowtimeInfo = findViewById(R.id.tv_showtime_info);
        tvSeatInfo = findViewById(R.id.tv_selected_seats);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnContinueBooking = findViewById(R.id.btn_continue_booking);

        btnContinueBooking.setOnClickListener(v -> confirmBookingAtCounter());
    }

    /**
     * Tải thông tin suất chiếu và sơ đồ ghế (cần chạy trên Background Thread)
     */
    private void loadShowtimeAndSeats() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 1. Tải chi tiết suất chiếu (Bạn cần hàm getShowtimeDetailsById trong DBHelper)
            currentShowtime = dbHelper.getShowtimeDetailsById(showtimeId);

            if (currentShowtime != null) {
                // 2. Tải tất cả ghế của phòng chiếu này và trạng thái của chúng
                // (Bạn cần hàm getSeatsForShowtime trong DBHelper)
                allSeatsInRoom = dbHelper.getSeatsForShowtime(currentShowtime.getRoom_id(), showtimeId);
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (currentShowtime != null && allSeatsInRoom != null) {
                    displayShowtimeInfo();
                    setupSeatsRecyclerView();
                } else {
                    Toast.makeText(this, "Không thể tải sơ đồ phòng chiếu.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        });
    }

    private void displayShowtimeInfo() {
        // Hiển thị thông tin suất chiếu
        tvMovieTitle.setText(currentShowtime.getMovie_name());
        tvShowtimeInfo.setText(currentShowtime.getRoom_name() +
                " | Ngày: " + currentShowtime.getShow_date() +
                " | Giờ: " + currentShowtime.getStart_time());
    }

    private void setupSeatsRecyclerView() {
        // Giả định phòng chiếu có 10 cột ghế (tùy thuộc vào thiết kế phòng)
        int numColumns = 10;

        rvSeats.setLayoutManager(new GridLayoutManager(this, numColumns));
        // Khởi tạo Adapter
        seatAdapter = new SeatAdapter(this, allSeatsInRoom, this);
        rvSeats.setAdapter(seatAdapter);
    }

    /**
     * Cập nhật UI khi có ghế được chọn/bỏ chọn
     */
    @Override
    public void onSeatSelected(Seat seat, boolean isSelected) {
        if (isSelected) {
            selectedSeats.add(seat);
        } else {
            selectedSeats.remove(seat);
        }
        updateBookingSummary();
    }

    private void updateBookingSummary() {
        if (selectedSeats.isEmpty()) {
            tvSeatInfo.setText("Chưa chọn ghế nào.");
            tvTotalPrice.setText("0 VNĐ");
            btnContinueBooking.setEnabled(false);
            return;
        }

        // 1. Lấy tên ghế và tính tổng tiền
        StringBuilder seatNames = new StringBuilder();
        double totalPrice = 0;

        for (Seat seat : selectedSeats) {
            seatNames.append(seat.getSeat_name()).append(", ");
            // Giả định tất cả ghế có giá bằng giá suất chiếu (currentShowtime.getPrice())
            totalPrice += currentShowtime.getPrice();
        }

        // 2. Cập nhật UI
        String finalSeatNames = seatNames.substring(0, seatNames.length() - 2); // Xóa dấu phẩy cuối cùng
        tvSeatInfo.setText("Ghế đã chọn (" + selectedSeats.size() + "): " + finalSeatNames);
        tvTotalPrice.setText(currencyFormat.format(totalPrice));
        btnContinueBooking.setEnabled(true);
    }

    private void confirmBookingAtCounter() {
        if (selectedSeats.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ghế.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Tính toán tổng tiền và lấy danh sách ID ghế
        final double totalPrice = selectedSeats.size() * currentShowtime.getPrice();
        final ArrayList<Integer> selectedSeatIds = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            selectedSeatIds.add(seat.getSeat_id());
        }

        // 2. Gọi DB để tạo giao dịch (Ticket) và ghi lại ghế (Ticket_Seat)
        Executors.newSingleThreadExecutor().execute(() -> {

            // 🎯 LẤY USER ID CHÍNH XÁC TỪ PHIÊN ĐĂNG NHẬP
            // SỬA LỖI: Gọi trực tiếp SessionManager để đồng bộ hóa
            final int userId = SessionManager.getLoggedInUserId(this);

            // Kiểm tra tính hợp lệ của ID
            if (userId <= 0) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(this, "❌ Lỗi: Vui lòng đăng nhập lại để đặt vé.", Toast.LENGTH_LONG).show()
                );
                return;
            }

            // 💡 Gọi hàm DB: Ghi lại vé với userId chính xác
            long ticketId = dbHelper.createTicketAndSeats(
                    userId, // TRUYỀN ID CỦA NGƯỜI DÙNG HIỆN TẠI
                    showtimeId,
                    totalPrice,
                    selectedSeatIds,
                    "pending"
            );

            // 3. Cập nhật UI trên Main Thread
            new Handler(Looper.getMainLooper()).post(() -> {
                if (ticketId > 0) {
                    // Xử lý thành công
                    Toast.makeText(this,
                            "✅ Đặt vé thành công! Tổng tiền: " + currencyFormat.format(totalPrice) + ". Vui lòng thanh toán tại quầy.",
                            Toast.LENGTH_LONG).show();

                    finish();
                } else {
                    Toast.makeText(this,
                            "❌ Lỗi: Không thể đặt vé. Ghế có thể đã được chọn hoặc lỗi hệ thống.",
                            Toast.LENGTH_LONG).show();
                    loadShowtimeAndSeats();
                }
            });
        });
    }

    // ĐÃ XÓA BỎ HÀM getLoggedInUserId() CỤC BỘ DƯ THỪA.
}
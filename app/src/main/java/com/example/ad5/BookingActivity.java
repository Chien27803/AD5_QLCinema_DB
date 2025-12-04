package com.example.ad5;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private Movie movie;
    private User currentUser;
    private DBHelper dbHelper;

    // Views
    private ImageView btnBack;
    private TextView tvMovieTitle;
    private RecyclerView rvDates;
    private GridLayout gridShowtimes;
    private TextView tvNoShowtime;
    private MaterialCardView cardSeatSelection, cardSummary, cardPaymentMethod;
    private LinearLayout layoutSeats;
    private RadioGroup rgPaymentMethod;
    private TextView tvSummaryDate, tvSummaryShowtime, tvSummarySeats, tvTotalPrice, tvPaymentMethod;
    private ExtendedFloatingActionButton btnConfirmBooking;

    // Data
    private List<DateItem> dateList = new ArrayList<>();
    private DateAdapter dateAdapter;
    private List<Showtime> availableShowtimes = new ArrayList<>(); // New: Store fetched showtimes
    private String selectedDate = "";
    private Showtime selectedShowtime = null; // Changed: Store Showtime object
    private List<Seat> allSeatsInRoom = new ArrayList<>(); // New: All seats for the selected room
    private List<String> bookedSeatNames = new ArrayList<>(); // New: Booked seat names for selected showtime
    private List<Seat> selectedSeats = new ArrayList<>(); // Changed: Store Seat objects
    private int selectedPaymentMethodId = 1; // Mặc định: Tiền mặt
    private String selectedPaymentMethodName = "Tiền mặt";

    // Seat configuration (now depends on DB, only rowLabels is kept for UI)
    private String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private int rows = rowLabels.length; // Max rows

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        dbHelper = new DBHelper(this);
        movie = (Movie) getIntent().getSerializableExtra("movie");
        currentUser = (User) getIntent().getSerializableExtra("user");

        if (movie == null) {
            Toast.makeText(this, "Không tìm thấy thông tin phim", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        loadDates();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        rvDates = findViewById(R.id.rvDates);
        gridShowtimes = findViewById(R.id.gridShowtimes);
        tvNoShowtime = findViewById(R.id.tvNoShowtime);
        cardSeatSelection = findViewById(R.id.cardSeatSelection);
        layoutSeats = findViewById(R.id.layoutSeats);
        cardPaymentMethod = findViewById(R.id.cardPaymentMethod);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        cardSummary = findViewById(R.id.cardSummary);
        tvSummaryDate = findViewById(R.id.tvSummaryDate);
        tvSummaryShowtime = findViewById(R.id.tvSummaryShowtime);
        tvSummarySeats = findViewById(R.id.tvSummarySeats);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        tvMovieTitle.setText(movie.getMovie_name());

        // Setup payment method radio buttons
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCash) {
                selectedPaymentMethodId = 1;
                selectedPaymentMethodName = "Tiền mặt";
            } else if (checkedId == R.id.rbEWallet) {
                selectedPaymentMethodId = 2;
                selectedPaymentMethodName = "Ví điện tử";
            } else if (checkedId == R.id.rbBankCard) {
                selectedPaymentMethodId = 3;
                selectedPaymentMethodName = "Thẻ ngân hàng";
            }
            updateSummary();
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void loadDates() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("vi"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("'Th'MM", new Locale("vi"));
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String dayName = dayFormat.format(calendar.getTime());
            String dayNumber = dateFormat.format(calendar.getTime());
            String month = monthFormat.format(calendar.getTime());
            String fullDate = fullDateFormat.format(calendar.getTime());

            dateList.add(new DateItem(dayName, dayNumber, month, fullDate));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        dateAdapter = new DateAdapter(this, dateList, (dateItem, position) -> {
            // Reset showtime and seats when date changes
            selectedDate = dateItem.getFullDate();
            selectedShowtime = null;
            selectedSeats.clear();
            cardSeatSelection.setVisibility(View.GONE);
            updateSummary();
            loadShowtimes();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvDates.setLayoutManager(layoutManager);
        rvDates.setAdapter(dateAdapter);

        // Select the first date by default to load showtimes
        if (!dateList.isEmpty()) {
            dateAdapter.selectFirstItem();
            selectedDate = dateList.get(0).getFullDate();
            // loadShowtimes() will be called by selectFirstItem's callback
        }
    }

    private void loadShowtimes() {
        gridShowtimes.removeAllViews();
        // Lấy suất chiếu từ DB
        availableShowtimes = dbHelper.getShowtimesByDateAndMovie(selectedDate, movie.getMovie_id());

        if (availableShowtimes.isEmpty()) {
            tvNoShowtime.setText("Không có suất chiếu cho phim này vào ngày đã chọn.");
            tvNoShowtime.setVisibility(View.VISIBLE);
            gridShowtimes.setVisibility(View.GONE);
            return;
        }

        tvNoShowtime.setVisibility(View.GONE);
        gridShowtimes.setVisibility(View.VISIBLE);

        for (Showtime showtime : availableShowtimes) {
            Button btnShowtime = new Button(this);
            btnShowtime.setText(showtime.getStart_time()); // Hiển thị giờ bắt đầu
            btnShowtime.setTextSize(14);
            btnShowtime.setTag(showtime); // Store the actual object in the tag

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            btnShowtime.setLayoutParams(params);

            // Highlight selected showtime if any
            if (selectedShowtime != null && selectedShowtime.getShowtime_id() == showtime.getShowtime_id()) {
                btnShowtime.setBackgroundResource(R.drawable.showtime_button_selected);
                btnShowtime.setTextColor(Color.WHITE);
                cardSeatSelection.setVisibility(View.VISIBLE);
            } else {
                btnShowtime.setBackgroundResource(R.drawable.showtime_button_background);
                btnShowtime.setTextColor(Color.parseColor("#333333"));
            }

            btnShowtime.setOnClickListener(v -> {
                Showtime newShowtime = (Showtime) v.getTag();

                // Clear previous seat selection if showtime changes
                if (selectedShowtime == null || selectedShowtime.getShowtime_id() != newShowtime.getShowtime_id()) {
                    selectedSeats.clear();
                }

                selectedShowtime = newShowtime;
                highlightSelectedShowtime((Button) v);
                createSeatLayout(); // Tải lại layout ghế
                updateSummary();
                cardSeatSelection.setVisibility(View.VISIBLE);
            });

            gridShowtimes.addView(btnShowtime);
        }
    }

    private void highlightSelectedShowtime(Button selectedButton) {
        for (int i = 0; i < gridShowtimes.getChildCount(); i++) {
            Button btn = (Button) gridShowtimes.getChildAt(i);
            btn.setBackgroundResource(R.drawable.showtime_button_background);
            btn.setTextColor(Color.parseColor("#333333"));
        }
        selectedButton.setBackgroundResource(R.drawable.showtime_button_selected);
        selectedButton.setTextColor(Color.WHITE);
    }

    private void createSeatLayout() {
        if (selectedShowtime == null) return;

        layoutSeats.removeAllViews();

        // 1. Fetch all seats for the room
        allSeatsInRoom = dbHelper.getSeatsByRoomId(selectedShowtime.getRoom_id());

        // 2. Fetch all booked seat names for the selected showtime
        bookedSeatNames = dbHelper.getBookedSeatNames(selectedShowtime.getShowtime_id());

        // Map để giữ LinearLayout cho từng hàng
        LinearLayout[] rowLayouts = new LinearLayout[rows];

        for (Seat seat : allSeatsInRoom) {
            String seatName = seat.getSeat_name();
            char rowLabel = seatName.charAt(0);
            int rowIndex = -1;

            // Tìm index của hàng (A=0, B=1, ...)
            for (int i = 0; i < rowLabels.length; i++) {
                if (rowLabels[i].charAt(0) == rowLabel) {
                    rowIndex = i;
                    break;
                }
            }

            if (rowIndex == -1) continue;

            // Khởi tạo LinearLayout cho hàng nếu chưa có
            if (rowLayouts[rowIndex] == null) {
                rowLayouts[rowIndex] = new LinearLayout(this);
                rowLayouts[rowIndex].setOrientation(LinearLayout.HORIZONTAL);
                rowLayouts[rowIndex].setGravity(Gravity.CENTER);

                // Thêm label hàng
                TextView tvRowLabel = new TextView(this);
                tvRowLabel.setText(String.valueOf(rowLabel));
                tvRowLabel.setTextSize(16);
                tvRowLabel.setTextColor(Color.parseColor("#666666"));
                tvRowLabel.setGravity(Gravity.CENTER);
                tvRowLabel.setWidth(70);
                rowLayouts[rowIndex].addView(tvRowLabel);
            }

            Button btnSeat = new Button(this);
            // Cắt lấy số ghế (ví dụ: 'A1' -> '1')
            String seatNumber = seatName.substring(1);
            btnSeat.setText(seatNumber);
            btnSeat.setTextSize(14);
            btnSeat.setTag(seat); // Lưu đối tượng Seat vào tag

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            params.setMargins(6, 6, 6, 6);
            btnSeat.setLayoutParams(params);

            // Kiểm tra trạng thái ghế
            boolean isBooked = bookedSeatNames.contains(seatName);
            boolean isSelected = false;
            for(Seat s : selectedSeats) {
                if (s.getSeat_id() == seat.getSeat_id()) {
                    isSelected = true;
                    break;
                }
            }

            if (isBooked) {
                btnSeat.setBackgroundResource(R.drawable.seat_booked);
                btnSeat.setEnabled(false);
                btnSeat.setTextColor(Color.WHITE);
            } else if (isSelected) {
                btnSeat.setBackgroundResource(R.drawable.seat_selected);
                btnSeat.setTextColor(Color.WHITE);
                btnSeat.setOnClickListener(v -> toggleSeatSelection(btnSeat, seat));
            } else {
                btnSeat.setBackgroundResource(R.drawable.seat_available);
                btnSeat.setTextColor(Color.parseColor("#333333"));
                btnSeat.setOnClickListener(v -> toggleSeatSelection(btnSeat, seat));
            }

            rowLayouts[rowIndex].addView(btnSeat);
        }

        // Thêm các hàng ghế vào layout chính
        for (LinearLayout rowLayout : rowLayouts) {
            if (rowLayout != null) {
                layoutSeats.addView(rowLayout);
            }
        }
    }

    private void toggleSeatSelection(Button btnSeat, Seat seat) {
        boolean removed = false;
        // Kiểm tra và xóa khỏi danh sách ghế đã chọn
        for (int i = 0; i < selectedSeats.size(); i++) {
            if (selectedSeats.get(i).getSeat_id() == seat.getSeat_id()) {
                selectedSeats.remove(i);
                removed = true;
                break;
            }
        }

        if (removed) {
            btnSeat.setBackgroundResource(R.drawable.seat_available);
            btnSeat.setTextColor(Color.parseColor("#333333"));
        } else {
            if (selectedSeats.size() >= 10) {
                Toast.makeText(this, "Bạn chỉ có thể chọn tối đa 10 ghế", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedSeats.add(seat);
            btnSeat.setBackgroundResource(R.drawable.seat_selected);
            btnSeat.setTextColor(Color.WHITE);
        }

        updateSummary();
    }

    private void updateSummary() {
        if (selectedSeats.isEmpty() || selectedShowtime == null) {
            cardPaymentMethod.setVisibility(View.GONE);
            cardSummary.setVisibility(View.GONE);
            btnConfirmBooking.setVisibility(View.GONE);
            return;
        }

        cardPaymentMethod.setVisibility(View.VISIBLE);
        cardSummary.setVisibility(View.VISIBLE);
        btnConfirmBooking.setVisibility(View.VISIBLE);

        tvSummaryDate.setText(selectedDate);
        tvSummaryShowtime.setText(selectedShowtime.getStart_time());

        // Thu thập tên ghế
        List<String> seatNames = new ArrayList<>();
        for(Seat seat : selectedSeats) {
            seatNames.add(seat.getSeat_name());
        }
        tvSummarySeats.setText(String.join(", ", seatNames));
        tvPaymentMethod.setText(selectedPaymentMethodName);

        // Tính tổng tiền dựa trên giá vé của suất chiếu
        int totalPrice = (int) (selectedSeats.size() * selectedShowtime.getPrice());
        tvTotalPrice.setText(String.format("%,dđ", totalPrice));
    }

    private void confirmBooking() {
        if (selectedShowtime == null || selectedSeats.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn suất chiếu và ghế ngồi.", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalPrice = (int) (selectedSeats.size() * selectedShowtime.getPrice());
        List<String> seatNames = new ArrayList<>();
        for(Seat seat : selectedSeats) {
            seatNames.add(seat.getSeat_name());
        }

        String message = "Phim: " + movie.getMovie_name() + "\n" +
                "Ngày: " + selectedDate + "\n" +
                "Suất chiếu: " + selectedShowtime.getStart_time() + "\n" +
                "Ghế: " + String.join(", ", seatNames) + "\n" +
                "Phương thức: " + selectedPaymentMethodName + "\n" +
                "Tổng tiền: " + String.format("%,dđ", totalPrice);

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đặt vé")
                .setMessage(message)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    boolean success = saveBookingToDatabase(totalPrice, selectedShowtime.getShowtime_id());
                    if (success) {
                        Toast.makeText(this, "Đặt vé thành công! Vui lòng kiểm tra mục Vé của tôi.", Toast.LENGTH_LONG).show();
                        // Quay về MainActivity hoặc MyTicketsActivity
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("user", currentUser);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Đặt vé thất bại. Vui lòng thử lại!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private boolean saveBookingToDatabase(int totalPrice, int showtimeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            // 1. Tạo Ticket
            ContentValues ticketValues = new ContentValues();
            ticketValues.put("user_id", currentUser.getUser_id());
            ticketValues.put("showtime_id", showtimeId);
            ticketValues.put("status", "booked");
            ticketValues.put("total_money", totalPrice);

            long ticketId = db.insert("Ticket", null, ticketValues);

            if (ticketId == -1) {
                return false;
            }

            // 2. Lưu thông tin ghế (Ticket_Seat) - Sử dụng seat_id thực tế
            for (Seat seat : selectedSeats) {
                ContentValues seatValues = new ContentValues();
                seatValues.put("ticket_id", ticketId);
                seatValues.put("seat_id", seat.getSeat_id()); // Sử dụng ID ghế thực tế
                db.insert("Ticket_Seat", null, seatValues);
            }

            // 3. Tạo Payment
            ContentValues paymentValues = new ContentValues();
            paymentValues.put("ticket_id", ticketId);
            paymentValues.put("user_id", currentUser.getUser_id());
            paymentValues.put("total_money", totalPrice);
            paymentValues.put("method_id", selectedPaymentMethodId);
            paymentValues.put("status", "completed");

            long paymentId = db.insert("Payment", null, paymentValues);

            if (paymentId == -1) {
                return false;
            }

            // 4. Lưu thông tin chi tiết để hiển thị sau (Booking_Details)
            List<String> seatNames = new ArrayList<>();
            for(Seat seat : selectedSeats) {
                seatNames.add(seat.getSeat_name());
            }

            ContentValues detailValues = new ContentValues();
            detailValues.put("ticket_id", ticketId);
            detailValues.put("movie_name", movie.getMovie_name());
            detailValues.put("movie_image", movie.getImage());
            detailValues.put("show_date", selectedDate);
            detailValues.put("show_time", selectedShowtime.getStart_time());
            detailValues.put("seats", String.join(", ", seatNames));
            detailValues.put("payment_method", selectedPaymentMethodName);

            // Đảm bảo bảng đã được tạo (dù đã có trong DBHelper.onCreate)
            db.execSQL("CREATE TABLE IF NOT EXISTS Booking_Details (" +
                    "detail_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ticket_id INTEGER, " +
                    "movie_name TEXT, " +
                    "movie_image TEXT, " +
                    "show_date TEXT, " +
                    "show_time TEXT, " +
                    "seats TEXT, " +
                    "payment_method TEXT)");

            db.insert("Booking_Details", null, detailValues);

            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }
}
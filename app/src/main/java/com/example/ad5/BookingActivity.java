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
    private String[] showtimes = {"10:00", "13:00", "16:00", "19:00", "21:30", "23:00"};
    private String selectedDate = "";
    private String selectedShowtime = "";
    private List<String> selectedSeats = new ArrayList<>();
    private int selectedPaymentMethodId = 1; // Mặc định: Tiền mặt
    private String selectedPaymentMethodName = "Tiền mặt";
    private int ticketPrice = 100000; // 100,000đ per ticket

    // Seat configuration
    private int rows = 8;
    private String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private int seatsPerRow = 10;
    private List<String> bookedSeats = new ArrayList<>();

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
        simulateBookedSeats();
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
            selectedDate = dateItem.getFullDate();
            loadShowtimes();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvDates.setLayoutManager(layoutManager);
        rvDates.setAdapter(dateAdapter);
    }

    private void loadShowtimes() {
        gridShowtimes.removeAllViews();
        tvNoShowtime.setVisibility(View.GONE);
        gridShowtimes.setVisibility(View.VISIBLE);

        for (String showtime : showtimes) {
            Button btnShowtime = new Button(this);
            btnShowtime.setText(showtime);
            btnShowtime.setTextSize(14);
            btnShowtime.setTextColor(Color.parseColor("#333333"));
            btnShowtime.setBackgroundResource(R.drawable.showtime_button_background);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            btnShowtime.setLayoutParams(params);

            btnShowtime.setOnClickListener(v -> {
                selectedShowtime = showtime;
                highlightSelectedShowtime((Button) v);
                createSeatLayout();
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

    private void simulateBookedSeats() {
        bookedSeats.add("C5");
        bookedSeats.add("C6");
        bookedSeats.add("D5");
        bookedSeats.add("D6");
        bookedSeats.add("E4");
        bookedSeats.add("E5");
        bookedSeats.add("E6");
        bookedSeats.add("E7");
    }

    private void createSeatLayout() {
        layoutSeats.removeAllViews();
        selectedSeats.clear();

        for (int row = 0; row < rows; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);

            TextView tvRowLabel = new TextView(this);
            tvRowLabel.setText(rowLabels[row]);
            tvRowLabel.setTextSize(16);
            tvRowLabel.setTextColor(Color.parseColor("#666666"));
            tvRowLabel.setGravity(Gravity.CENTER);
            tvRowLabel.setWidth(70);
            rowLayout.addView(tvRowLabel);

            for (int seat = 1; seat <= seatsPerRow; seat++) {
                String seatId = rowLabels[row] + seat;
                Button btnSeat = new Button(this);
                btnSeat.setText(String.valueOf(seat));
                btnSeat.setTextSize(14);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                params.setMargins(6, 6, 6, 6);
                btnSeat.setLayoutParams(params);

                if (bookedSeats.contains(seatId)) {
                    btnSeat.setBackgroundResource(R.drawable.seat_booked);
                    btnSeat.setEnabled(false);
                    btnSeat.setTextColor(Color.WHITE);
                } else {
                    btnSeat.setBackgroundResource(R.drawable.seat_available);
                    btnSeat.setTextColor(Color.parseColor("#333333"));
                    btnSeat.setOnClickListener(v -> toggleSeatSelection(btnSeat, seatId));
                }

                rowLayout.addView(btnSeat);
            }

            layoutSeats.addView(rowLayout);
        }
    }

    private void toggleSeatSelection(Button btnSeat, String seatId) {
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            btnSeat.setBackgroundResource(R.drawable.seat_available);
            btnSeat.setTextColor(Color.parseColor("#333333"));
        } else {
            if (selectedSeats.size() >= 10) {
                Toast.makeText(this, "Bạn chỉ có thể chọn tối đa 10 ghế", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedSeats.add(seatId);
            btnSeat.setBackgroundResource(R.drawable.seat_selected);
            btnSeat.setTextColor(Color.WHITE);
        }

        updateSummary();
    }

    private void updateSummary() {
        if (selectedSeats.isEmpty()) {
            cardPaymentMethod.setVisibility(View.GONE);
            cardSummary.setVisibility(View.GONE);
            btnConfirmBooking.setVisibility(View.GONE);
        } else {
            cardPaymentMethod.setVisibility(View.VISIBLE);
            cardSummary.setVisibility(View.VISIBLE);
            btnConfirmBooking.setVisibility(View.VISIBLE);

            tvSummaryDate.setText(selectedDate);
            tvSummaryShowtime.setText(selectedShowtime);
            tvSummarySeats.setText(String.join(", ", selectedSeats));
            tvPaymentMethod.setText(selectedPaymentMethodName);

            int totalPrice = selectedSeats.size() * ticketPrice;
            tvTotalPrice.setText(String.format("%,dđ", totalPrice));
        }
    }

    private void confirmBooking() {
        int totalPrice = selectedSeats.size() * ticketPrice;

        String message = "Phim: " + movie.getMovie_name() + "\n" +
                "Ngày: " + selectedDate + "\n" +
                "Suất chiếu: " + selectedShowtime + "\n" +
                "Ghế: " + String.join(", ", selectedSeats) + "\n" +
                "Phương thức: " + selectedPaymentMethodName + "\n" +
                "Tổng tiền: " + String.format("%,dđ", totalPrice);

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đặt vé")
                .setMessage(message)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    boolean success = saveBookingToDatabase(totalPrice);
                    if (success) {
                        Toast.makeText(this, "Đặt vé thành công!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Đặt vé thất bại. Vui lòng thử lại!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private boolean saveBookingToDatabase(int totalPrice) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            // 1. Tạo Ticket
            ContentValues ticketValues = new ContentValues();
            ticketValues.put("user_id", currentUser.getUser_id());
            ticketValues.put("showtime_id", 1); // Demo: showtime_id = 1
            ticketValues.put("status", "booked");
            ticketValues.put("total_money", totalPrice);

            long ticketId = db.insert("Ticket", null, ticketValues);

            if (ticketId == -1) {
                return false;
            }

            // 2. Lưu thông tin ghế (Ticket_Seat) - Demo
            for (String seatName : selectedSeats) {
                ContentValues seatValues = new ContentValues();
                seatValues.put("ticket_id", ticketId);
                seatValues.put("seat_id", 1); // Demo: seat_id
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

            // 4. Lưu thông tin chi tiết để hiển thị sau (custom table)
            ContentValues detailValues = new ContentValues();
            detailValues.put("ticket_id", ticketId);
            detailValues.put("movie_name", movie.getMovie_name());
            detailValues.put("movie_image", movie.getImage());
            detailValues.put("show_date", selectedDate);
            detailValues.put("show_time", selectedShowtime);
            detailValues.put("seats", String.join(", ", selectedSeats));
            detailValues.put("payment_method", selectedPaymentMethodName);

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
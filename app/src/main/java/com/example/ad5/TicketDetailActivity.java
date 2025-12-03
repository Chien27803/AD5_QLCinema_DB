package com.example.ad5;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {

    private ImageView btnBack, imgMoviePoster, imgQRCode;
    private TextView tvMovieName, tvStatus, tvTicketId, tvBookingTime;
    private TextView tvShowDate, tvShowTime, tvCinema;
    private TextView tvSeats, tvQuantity;
    private TextView tvPaymentMethod, tvTicketPrice, tvTotalPrice;
    private MaterialButton btnCancelTicket;
    private MaterialCardView cardQRCode;

    private DBHelper dbHelper;
    private int ticketId;
    private String ticketStatus;
    private String showDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        dbHelper = new DBHelper(this);
        ticketId = getIntent().getIntExtra("ticket_id", -1);

        if (ticketId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin vé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        loadTicketDetails();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgMoviePoster = findViewById(R.id.imgMoviePoster);
        imgQRCode = findViewById(R.id.imgQRCode);

        tvMovieName = findViewById(R.id.tvMovieName);
        tvStatus = findViewById(R.id.tvStatus);
        tvTicketId = findViewById(R.id.tvTicketId);
        tvBookingTime = findViewById(R.id.tvBookingTime);

        tvShowDate = findViewById(R.id.tvShowDate);
        tvShowTime = findViewById(R.id.tvShowTime);
        tvCinema = findViewById(R.id.tvCinema);

        tvSeats = findViewById(R.id.tvSeats);
        tvQuantity = findViewById(R.id.tvQuantity);

        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTicketPrice = findViewById(R.id.tvTicketPrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        btnCancelTicket = findViewById(R.id.btnCancelTicket);
        cardQRCode = findViewById(R.id.cardQRCode);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCancelTicket.setOnClickListener(v -> showCancelDialog());
    }

    private void loadTicketDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT t.ticket_id, t.total_money, t.booking_time, t.status, " +
                "bd.movie_name, bd.movie_image, bd.show_date, bd.show_time, bd.seats, bd.payment_method " +
                "FROM Ticket t " +
                "LEFT JOIN Booking_Details bd ON t.ticket_id = bd.ticket_id " +
                "WHERE t.ticket_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ticketId)});

        if (cursor != null && cursor.moveToFirst()) {
            // Basic info
            int totalMoney = cursor.getInt(1);
            String bookingTime = cursor.getString(2);
            ticketStatus = cursor.getString(3);

            // Movie info
            String movieName = cursor.getString(4);
            String movieImage = cursor.getString(5);
            showDate = cursor.getString(6);
            String showTime = cursor.getString(7);
            String seats = cursor.getString(8);
            String paymentMethod = cursor.getString(9);

            // Debug log
            android.util.Log.d("TicketDetail", "Ticket ID: " + ticketId);
            android.util.Log.d("TicketDetail", "Movie: " + movieName);
            android.util.Log.d("TicketDetail", "Date: " + showDate);
            android.util.Log.d("TicketDetail", "Time: " + showTime);
            android.util.Log.d("TicketDetail", "Seats: " + seats);

            // Display data
            tvMovieName.setText(movieName != null ? movieName : "Không có tên");
            tvTicketId.setText("Mã vé: #" + ticketId);
            tvBookingTime.setText("Đặt lúc: " + formatBookingTime(bookingTime));

            tvShowDate.setText(showDate != null ? showDate : "N/A");
            tvShowTime.setText(showTime != null ? showTime : "N/A");
            tvCinema.setText("AD5"); // Demo

            if (seats != null && !seats.isEmpty()) {
                tvSeats.setText(seats);
                String[] seatArray = seats.split(", ");
                tvQuantity.setText(seatArray.length + " vé");

                int pricePerTicket = 100000; // Demo price
                tvTicketPrice.setText(String.format("%,dđ x %d", pricePerTicket, seatArray.length));
            } else {
                tvSeats.setText("N/A");
                tvQuantity.setText("0 vé");
                tvTicketPrice.setText("N/A");
            }

            tvPaymentMethod.setText(paymentMethod != null ? paymentMethod : "N/A");
            tvTotalPrice.setText(String.format("%,dđ", totalMoney));

            // Load image
            if (movieImage != null && !movieImage.isEmpty()) {
                Glide.with(this)
                        .load(movieImage)
                        .placeholder(R.drawable.ic_movie_placeholder)
                        .error(R.drawable.ic_movie_placeholder)
                        .into(imgMoviePoster);
            } else {
                imgMoviePoster.setImageResource(R.drawable.ic_movie_placeholder);
            }

            // Update status
            updateStatusUI();

            cursor.close();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin vé", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateStatusUI() {
        if ("cancelled".equals(ticketStatus)) {
            tvStatus.setText("✗ Đã hủy");
            tvStatus.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_red_dark));
            btnCancelTicket.setEnabled(false);
            btnCancelTicket.setAlpha(0.5f);
            cardQRCode.setVisibility(View.GONE);
        } else if ("booked".equals(ticketStatus)) {
            tvStatus.setText("✓ Đã đặt");
            tvStatus.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_green_dark));

            // Check if can cancel (before 4 hours)
            if (!canCancelTicket()) {
                btnCancelTicket.setEnabled(false);
                btnCancelTicket.setAlpha(0.5f);
                btnCancelTicket.setText("QUÁ HẠN HỦY");
            }
        }
    }

    private boolean canCancelTicket() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateTimeStr = showDate + " " + tvShowTime.getText().toString();
            Date showDateTime = sdf.parse(dateTimeStr);
            Date now = new Date();

            long diffInMillis = showDateTime.getTime() - now.getTime();
            long diffInHours = diffInMillis / (1000 * 60 * 60);

            return diffInHours >= 4; // Can cancel if more than 4 hours before showtime
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showCancelDialog() {
        String message = "Bạn có chắc muốn hủy vé này?\n\n" +
                "⚠️ Lưu ý:\n" +
                "• Chỉ được hoàn lại 80% giá trị vé\n" +
                "• Tiền sẽ được hoàn về ví trong 3-5 ngày làm việc\n" +
                "• Sau khi hủy không thể hoàn tác";

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy vé")
                .setMessage(message)
                .setPositiveButton("Xác nhận hủy", (dialog, which) -> cancelTicket())
                .setNegativeButton("Giữ vé", null)
                .show();
    }

    private void cancelTicket() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", "cancelled");

        int rowsAffected = db.update("Ticket", values, "ticket_id = ?",
                new String[]{String.valueOf(ticketId)});

        if (rowsAffected > 0) {
            // Also update Payment status
            ContentValues paymentValues = new ContentValues();
            paymentValues.put("status", "refunded");
            db.update("Payment", paymentValues, "ticket_id = ?",
                    new String[]{String.valueOf(ticketId)});

            Toast.makeText(this, "Hủy vé thành công! Tiền sẽ được hoàn lại trong 3-5 ngày",
                    Toast.LENGTH_LONG).show();

            // Refresh UI
            ticketStatus = "cancelled";
            updateStatusUI();

            // Set result to refresh MyTicketsActivity
            setResult(RESULT_OK);
        } else {
            Toast.makeText(this, "Hủy vé thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatBookingTime(String bookingTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(bookingTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return bookingTime;
        }
    }
}

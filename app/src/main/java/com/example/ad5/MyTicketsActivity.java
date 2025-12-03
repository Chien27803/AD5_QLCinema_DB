package com.example.ad5;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private RecyclerView rvTickets;
    private LinearLayout layoutEmptyState;
    private ImageView btnBack;
    private TicketAdapter ticketAdapter;
    private List<TicketItem> ticketList = new ArrayList<>();
    private DBHelper dbHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        dbHelper = new DBHelper(this);
        currentUser = (User) getIntent().getSerializableExtra("user");

        if (currentUser == null) {
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        loadTickets();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Refresh list after cancel ticket
            loadTickets();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvTickets = findViewById(R.id.rvTickets);
        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadTickets() {
        ticketList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query với LEFT JOIN để lấy đầy đủ thông tin
        String query = "SELECT t.ticket_id, t.total_money, t.booking_time, t.status, " +
                "bd.movie_name, bd.movie_image, bd.show_date, bd.show_time, bd.seats, bd.payment_method " +
                "FROM Ticket t " +
                "LEFT JOIN Booking_Details bd ON t.ticket_id = bd.ticket_id " +
                "WHERE t.user_id = ? " +
                "ORDER BY t.ticket_id DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(currentUser.getUser_id())});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TicketItem ticket = new TicketItem();

                // Lấy dữ liệu từ cursor
                ticket.setTicketId(cursor.getInt(0));
                ticket.setTotalMoney(cursor.getInt(1));
                ticket.setBookingTime(cursor.getString(2));
                ticket.setStatus(cursor.getString(3));

                // Kiểm tra null trước khi set
                String movieName = cursor.getString(4);
                String movieImage = cursor.getString(5);
                String showDate = cursor.getString(6);
                String showTime = cursor.getString(7);
                String seats = cursor.getString(8);
                String paymentMethod = cursor.getString(9);

                // Debug log
                android.util.Log.d("MyTickets", "Ticket ID: " + ticket.getTicketId());
                android.util.Log.d("MyTickets", "Movie: " + movieName);
                android.util.Log.d("MyTickets", "Date: " + showDate);
                android.util.Log.d("MyTickets", "Time: " + showTime);
                android.util.Log.d("MyTickets", "Seats: " + seats);

                ticket.setMovieName(movieName != null ? movieName : "Không có tên");
                ticket.setMovieImage(movieImage != null ? movieImage : "");
                ticket.setShowDate(showDate != null ? showDate : "N/A");
                ticket.setShowTime(showTime != null ? showTime : "N/A");
                ticket.setSeats(seats != null ? seats : "N/A");
                ticket.setPaymentMethod(paymentMethod != null ? paymentMethod : "N/A");

                ticketList.add(ticket);
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (ticketList.isEmpty()) {
            rvTickets.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvTickets.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);

            ticketAdapter = new TicketAdapter(this, ticketList);
            rvTickets.setAdapter(ticketAdapter);
        }
    }
}

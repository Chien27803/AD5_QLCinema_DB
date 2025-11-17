package com.example.ad5;

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

        String query = "SELECT t.ticket_id, t.total_money, t.booking_time, t.status, " +
                "bd.movie_name, bd.movie_image, bd.show_date, bd.show_time, bd.seats, bd.payment_method " +
                "FROM Ticket t " +
                "LEFT JOIN Booking_Details bd ON t.ticket_id = bd.ticket_id " +
                "WHERE t.user_id = ? " +
                "ORDER BY t.booking_time DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(currentUser.getUser_id())});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TicketItem ticket = new TicketItem();
                ticket.setTicketId(cursor.getInt(0));
                ticket.setTotalMoney(cursor.getInt(1));
                ticket.setBookingTime(cursor.getString(2));
                ticket.setStatus(cursor.getString(3));
                ticket.setMovieName(cursor.getString(4));
                ticket.setMovieImage(cursor.getString(5));
                ticket.setShowDate(cursor.getString(6));
                ticket.setShowTime(cursor.getString(7));
                ticket.setSeats(cursor.getString(8));
                ticket.setPaymentMethod(cursor.getString(9));

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

package com.example.ad5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// Đảm bảo bạn đã có TicketAdapter
import com.example.ad5.TicketAdapter;
// Đảm bảo bạn đã có các models khác nếu cần (ví dụ: User)

public class TicketActivity extends AppCompatActivity {

    private RecyclerView rvTickets;
    private TicketAdapter ticketAdapter;
    private List<Ticket> allTickets = new ArrayList<>();
    private DBHelper dbHelper; // Khai báo DBHelper
    private androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sử dụng layout quản lý đơn hàng
        setContentView(R.layout.activity_manage_ticket);

        // 1. Khởi tạo DBHelper
        dbHelper = new DBHelper(this);
        initViews();
        setupToolbar();

        // 2. Khởi tạo RecyclerView
        // ID trong layout bạn cung cấp là 'rvOrders', tôi đổi thành 'rvTickets' cho nhất quán
        rvTickets = findViewById(R.id.rvOrders);
        rvTickets.setLayoutManager(new LinearLayoutManager(this));

        // 3. Khởi tạo Adapter
        ticketAdapter = new TicketAdapter(this, allTickets);
        rvTickets.setAdapter(ticketAdapter);

        // 4. Tải dữ liệu từ SQLite
        loadAllTickets();


    }
    private void initViews() {
        toolbar = findViewById(R.id.toolbarticket);

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

    /**
     * Tải tất cả các ticket từ cơ sở dữ liệu (Dùng cho Admin/Quản lý).
     */
    private void loadAllTickets() {
        try {
            // Gọi hàm getAllTickets() từ DBHelper
            List<Ticket> fetchedTickets = dbHelper.getAllTickets();

            allTickets.clear();
            allTickets.addAll(fetchedTickets);
            ticketAdapter.notifyDataSetChanged();

            if (allTickets.isEmpty()) {
                Toast.makeText(this, "Không có đơn đặt vé nào trong hệ thống.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tải dữ liệu vé: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
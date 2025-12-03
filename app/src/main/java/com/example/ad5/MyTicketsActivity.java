package com.example.ad5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

// MyTicketsActivity không cần triển khai listener nếu chỉ dùng để hiển thị
public class MyTicketsActivity extends AppCompatActivity {

    private RecyclerView rvTickets;
    private LinearLayout layoutEmptyState;
    private ImageView btnBack;
    // 💡 Đổi Adapter và List sang sử dụng Data Model chuẩn
    private TicketAdapter ticketAdapter;
    private List<Ticket> ticketList = new ArrayList<>();
    private DBHelper dbHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        dbHelper = new DBHelper(this);
        // Nhận đối tượng User qua Serializable
        currentUser = (User) getIntent().getSerializableExtra("user");

        if (currentUser == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        loadTickets(currentUser.getUser_id());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvTickets = findViewById(R.id.rvTickets);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        rvTickets.setLayoutManager(new LinearLayoutManager(this));

        // 💡 Khởi tạo Adapter với List rỗng và Constructor 2 tham số (User View)
        ticketAdapter = new TicketAdapter(this, new ArrayList<>());
        rvTickets.setAdapter(ticketAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * 💡 Tải vé trên Background Thread
     * @param userId ID người dùng hiện tại
     */
    private void loadTickets(int userId) {
        // Chạy truy vấn DB trên Background Thread
        Executors.newSingleThreadExecutor().execute(() -> {

            // 💡 Gọi hàm DB mới để lấy List<Ticket> (đã JOIN)
            List<Ticket> tickets = dbHelper.getTicketsByUserId(userId);

            // Cập nhật UI trên Main Thread
            new Handler(Looper.getMainLooper()).post(() -> {
                if (tickets != null && !tickets.isEmpty()) {
                    // Cập nhật Adapter (sử dụng hàm updateList đã được thêm vào Adapter)
                    ticketAdapter.updateList(tickets);
                    rvTickets.setVisibility(View.VISIBLE);
                    layoutEmptyState.setVisibility(View.GONE);
                } else {
                    // Xử lý trạng thái rỗng
                    rvTickets.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                }
            });
        });
    }
}
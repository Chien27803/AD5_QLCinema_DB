package com.example.ad5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomManagementActivity extends AppCompatActivity implements RoomAdapter.OnRoomActionListener {

    private RecyclerView rvRooms;
    private RoomAdapter adapter;
    private DBHelper dbHelper;
    private List<Room> roomList;
    private Button btnAddRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // DB
        dbHelper = new DBHelper(this);

        // Views
        rvRooms = findViewById(R.id.rvRooms);
        btnAddRoom = findViewById(R.id.btnAddRoom);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));

        loadRooms();

        btnAddRoom.setOnClickListener(v -> showRoomDialog(null));
    }

    private void loadRooms() {
        roomList = dbHelper.getAllRooms();
        adapter = new RoomAdapter(this, roomList, this);
        rvRooms.setAdapter(adapter);
    }

    private void showRoomDialog(Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RoomManagementActivity.this);
        builder.setTitle(room == null ? "Thêm phòng chiếu" : "Chỉnh sửa phòng");

        // Input fields
        EditText inputName = new EditText(RoomManagementActivity.this);
        inputName.setHint("Tên phòng");

        EditText inputSeats = new EditText(RoomManagementActivity.this);
        inputSeats.setHint("Số ghế");
        inputSeats.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        if (room != null) {
            inputName.setText(room.getRoom_name());
            inputSeats.setText(String.valueOf(room.getQuantity_seat()));
        }

        // Layout chứa input
        LinearLayout layout = new LinearLayout(RoomManagementActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 10);

        layout.addView(inputName);
        layout.addView(inputSeats);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", null); // Đặt null để tự xử lý

        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();

        // Xử lý sự kiện khi nhấn Lưu
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String name = inputName.getText().toString().trim();
                String seatsStr = inputSeats.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(RoomManagementActivity.this, "Vui lòng nhập tên phòng", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (seatsStr.isEmpty()) {
                    Toast.makeText(RoomManagementActivity.this, "Vui lòng nhập số ghế", Toast.LENGTH_SHORT).show();
                    return;
                }

                int seats = Integer.parseInt(seatsStr);
                if (seats <= 0) {
                    Toast.makeText(RoomManagementActivity.this, "Số ghế phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hiển thị dialog xác nhận
                showConfirmationDialog(room, name, seats, dialog);
            });
        });

        dialog.show();
    }

    private void showConfirmationDialog(Room room, String name, int seats, AlertDialog inputDialog) {
        String message = room == null ?
                "Bạn có chắc muốn thêm phòng \"" + name + "\" với " + seats + " ghế?" :
                "Bạn có chắc muốn sửa phòng thành \"" + name + "\" với " + seats + " ghế?";

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage(message)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // Thực hiện thêm/sửa
                    if (room == null) {
                        dbHelper.addRoom(name, seats);
                        Toast.makeText(this, "Thêm phòng thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.updateRoom(room.getRoom_id(), name, seats);
                        Toast.makeText(this, "Sửa phòng thành công!", Toast.LENGTH_SHORT).show();
                    }
                    inputDialog.dismiss(); // Đóng dialog nhập liệu
                    loadRooms();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onEditClick(Room room) {
        showRoomDialog(room);
    }

    @Override
    public void onDeleteClick(Room room) {
        new AlertDialog.Builder(RoomManagementActivity.this)
                .setTitle("Xóa phòng?")
                .setMessage("Bạn có chắc muốn xóa \"" + room.getRoom_name() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteRoom(room.getRoom_id());
                    Toast.makeText(this, "Xóa phòng thành công!", Toast.LENGTH_SHORT).show();
                    loadRooms();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
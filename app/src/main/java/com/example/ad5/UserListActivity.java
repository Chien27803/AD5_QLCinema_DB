package com.example.ad5;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerUserList;
    private DBHelper dbHelper;
    private List<User> userList;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bật nút back (mũi tên ←)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Xử lý khi nhấn nút back
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        // 🔙 Thêm nút quay lại góc trái
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Danh sách người dùng");
        }

        recyclerUserList = findViewById(R.id.rcvUsers);
        recyclerUserList.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
        loadUserList(); // 🔹 Tải danh sách user
    }

    private void loadUserList() {
        userList = dbHelper.getAllUsers();

        adapter = new UserAdapter(this, userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) {
                Toast.makeText(UserListActivity.this,
                        "🖋 Sửa người dùng: " + user.getUsername(),
                        Toast.LENGTH_SHORT).show();
                // TODO: Mở dialog sửa thông tin
            }

            @Override
            public void onDelete(User user) {
                new AlertDialog.Builder(UserListActivity.this)
                        .setTitle("Xóa người dùng")
                        .setMessage("Bạn có chắc chắn muốn xóa " + user.getUsername() + " không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            dbHelper.deleteUser(user.getUser_id());
                            Toast.makeText(UserListActivity.this,
                                    "Đã xóa " + user.getUsername(),
                                    Toast.LENGTH_SHORT).show();
                            loadUserList(); // 🔄 Cập nhật lại danh sách
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }

            @Override
            public void onRoleChange(User user) {
                showRoleDialog(user);
            }
        });

        recyclerUserList.setAdapter(adapter);
    }

    // 🔹 Hiển thị dialog chọn quyền
    private void showRoleDialog(User user) {
        String[] roles = {"User", "Admin"};
        int checkedIndex = user.getRole().equalsIgnoreCase("Admin") ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle("Phân quyền cho " + user.getUsername())
                .setSingleChoiceItems(roles, checkedIndex, (dialog, which) -> {
                    String selectedRole = roles[which];
                    dbHelper.updateUserRole(user.getUser_id(), selectedRole);
                    Toast.makeText(this,
                            "Đã đổi quyền của " + user.getUsername() + " thành " + selectedRole,
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadUserList(); // 🔄 Reload danh sách
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // 🔙 Xử lý khi bấm nút quay lại
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

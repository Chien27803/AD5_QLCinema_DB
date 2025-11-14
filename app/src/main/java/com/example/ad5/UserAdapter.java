package com.example.ad5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> list;
    private Context context;
    private OnUserActionListener listener;

    // Interface lắng nghe 3 hành động: sửa, xóa, phân quyền
    public interface OnUserActionListener {
        void onEdit(User user);
        void onDelete(User user);
        void onRoleChange(User user);
    }

    public UserAdapter(Context context, List<User> list, OnUserActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User u = list.get(position);
        holder.tvName.setText(u.getUsername());
        holder.tvEmail.setText(u.getEmail());
        holder.tvAddress.setText(u.getAddress());
        holder.tvRole.setText("Role: " + u.getRole());
        holder.tvStatus.setText(u.getStatus() == 1 ? "Đang hoạt động" : "Đã hủy");

        // Xử lý nút bấm
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(u));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(u));
        holder.btnRole.setOnClickListener(v -> listener.onRoleChange(u));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ViewHolder ánh xạ các thành phần trong layout item_user.xml
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole, tvStatus,tvAddress;
        Button btnEdit, btnDelete, btnRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnRole = itemView.findViewById(R.id.btnRole); // ⚡ Thêm dòng này
            tvAddress =  itemView.findViewById(R.id.tvAddress);
        }
    }
}

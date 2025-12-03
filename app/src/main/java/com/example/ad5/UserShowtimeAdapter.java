package com.example.ad5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Thêm import Button
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

// Đổi tên adapter để phân biệt với admin (hoặc chỉ sửa ShowtimeAdapter cũ)
public class UserShowtimeAdapter extends RecyclerView.Adapter<UserShowtimeAdapter.UserShowtimeViewHolder> {

    private final Context context;
    private List<Showtime> showtimeList;
    private final OnItemClickListener listener;
    // Format tiền tệ cho người dùng
    private final DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

    // Interface để xử lý sự kiện click Đặt vé
    public interface OnItemClickListener {
        void onBookClick(Showtime showtime);
    }

    public UserShowtimeAdapter(Context context, List<Showtime> showtimeList, OnItemClickListener listener) {
        this.context = context;
        this.showtimeList = showtimeList;
        this.listener = listener;
    }

    // Phương thức cập nhật danh sách
    public void updateList(List<Showtime> newList) {
        this.showtimeList = newList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public UserShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 💡 SỬ DỤNG LAYOUT USER MỚI
        View view = LayoutInflater.from(context).inflate(R.layout.item_showtime_user, parent, false);
        return new UserShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimeList.get(position);

        // 1. Gán dữ liệu cơ bản
        holder.tvStartTime.setText(showtime.getStart_time());
        holder.tvEndTime.setText("~ " + showtime.getEnd_time());
        holder.tvMovieTitle.setText(showtime.getMovie_name());
        holder.tvRoomName.setText(showtime.getRoom_name());
        holder.tvPrice.setText(currencyFormat.format(showtime.getPrice()));

        // 2. Tải ảnh movie bằng Glide
        if (showtime.getMovie_image() != null && !showtime.getMovie_image().isEmpty()) {
            Glide.with(context)
                    .load(showtime.getMovie_image())
                    // Thay thế bằng drawable placeholder mặc định của bạn
                    .error(R.drawable.ic_launcher_background)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivMovieImage);
        } else {
            holder.ivMovieImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // 3. Xử lý sự kiện click Đặt vé
        if (listener != null) {
            holder.btnBookTicket.setOnClickListener(v -> listener.onBookClick(showtime));

            // Xử lý click tổng thể (Tùy chọn: xem chi tiết suất chiếu)
            holder.itemView.setOnClickListener(v -> {
                // Có thể dùng onBookClick hoặc tạo listener riêng
            });
        }
    }

    @Override
    public int getItemCount() {
        return showtimeList != null ? showtimeList.size() : 0;
    }


    /**
     * ViewHolder: Khởi tạo và liên kết các View trong item_showtime_user.xml
     */
    public static class UserShowtimeViewHolder extends RecyclerView.ViewHolder {
        final TextView tvStartTime;
        final TextView tvEndTime;
        final TextView tvMovieTitle;
        final TextView tvRoomName;
        final TextView tvPrice;
        final ImageView ivMovieImage;
        final Button btnBookTicket; // 💡 Nút Đặt vé

        public UserShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvEndTime = itemView.findViewById(R.id.tv_end_time);
            tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivMovieImage = itemView.findViewById(R.id.iv_movie_image);
            btnBookTicket = itemView.findViewById(R.id.btn_book_ticket); // 💡 Ánh xạ nút Đặt vé
        }
    }
}
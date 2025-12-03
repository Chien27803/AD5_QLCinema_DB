package com.example.ad5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Cần thêm thư viện Glide

import java.text.DecimalFormat;
import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {

    private final Context context;
    private List<Showtime> showtimeList;
    private final OnItemClickListener listener;
    private final DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

    // Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Showtime showtime);
    }

    public ShowtimeAdapter(Context context, List<Showtime> showtimeList, OnItemClickListener listener) {
        this.context = context;
        this.showtimeList = showtimeList;
        this.listener = listener;
    }

    // Constructor đơn giản nếu bạn muốn xử lý click ở Activity
    public ShowtimeAdapter(Context context, List<Showtime> showtimeList) {
        this(context, showtimeList, null);
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimeList.get(position);

        // 1. Gán dữ liệu cơ bản
        holder.tvStartTime.setText(showtime.getStart_time());
        holder.tvEndTime.setText("~ " + showtime.getEnd_time());
        holder.tvMovieTitle.setText(showtime.getMovie_name());
        holder.tvRoomName.setText(showtime.getRoom_name());

        // 2. Định dạng và gán Giá vé
        holder.tvPrice.setText(currencyFormat.format(showtime.getPrice()));

        // 3. Tải ảnh movie bằng Glide
        if (showtime.getMovie_image() != null && !showtime.getMovie_image().isEmpty()) {
            Glide.with(context)
                    .load(showtime.getMovie_image())

                    .into(holder.ivMovieImage);
        } else {
            // Nếu không có ảnh, ẩn container hoặc đặt ảnh mặc định
            holder.ivMovieImage.setImageResource(R.drawable.inside2);
        }

        // 4. Lưu trữ ID và xử lý sự kiện click
        holder.itemView.setTag(showtime.getShowtime_id()); // Lưu ID vào View tag

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(showtime));
        }
    }

    @Override
    public int getItemCount() {
        return showtimeList != null ? showtimeList.size() : 0;
    }

    // Phương thức cập nhật danh sách
    public void updateList(List<Showtime> newList) {
        this.showtimeList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder: Khởi tạo và liên kết các View trong item_showtime.xml
     */
    public static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        final TextView tvStartTime;
        final TextView tvEndTime;
        final TextView tvMovieTitle;
        final TextView tvRoomName;
        final TextView tvPrice;
        final ImageView ivMovieImage;
        final TextView tvShowtimeIdHidden; // Giữ lại nếu bạn cần truy cập nó

        public ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvEndTime = itemView.findViewById(R.id.tv_end_time);
            tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivMovieImage = itemView.findViewById(R.id.iv_movie_image);
            tvShowtimeIdHidden = itemView.findViewById(R.id.tv_showtime_id_hidden);
        }
    }
}
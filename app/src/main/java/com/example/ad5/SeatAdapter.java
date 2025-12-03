package com.example.ad5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private final Context context;
    private final List<Seat> seatList;
    private final OnSeatSelectedListener listener;

    public interface OnSeatSelectedListener {
        void onSeatSelected(Seat seat, boolean isSelected);
    }

    public SeatAdapter(Context context, List<Seat> seatList, OnSeatSelectedListener listener) {
        this.context = context;
        this.seatList = seatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Cần tạo layout item_seat.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_seat, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seatList.get(position);

        // 1. Hiển thị tên ghế
        holder.tvSeatName.setText(seat.getSeat_name());

        // 2. Thiết lập trạng thái (Màu sắc)
        if (seat.getStatus() == 0) { // Đã đặt (Booked) 🛑
            holder.itemView.setEnabled(false);

            // Màu Đã đặt (Đỏ đậm/Xám)
            holder.tvSeatName.setBackgroundColor(Color.parseColor("#E57373")); // Ví dụ: Đỏ nhạt hơn
            // Đặt màu chữ là trắng (dùng hằng số an toàn)
            holder.tvSeatName.setTextColor(Color.WHITE);

        } else { // Còn trống (Available) ✅
            holder.itemView.setEnabled(true);

            if (seat.isSelected()) {
                // Đã Chọn (Selected) 🟢
                // Màu Xanh Lá (Mã Hex)
                holder.tvSeatName.setBackgroundColor(Color.parseColor("#66BB6A"));
                holder.tvSeatName.setTextColor(Color.BLACK);
            } else {
                // Còn Trống (Available) ⬜
                // Màu Xám Nhạt/Trắng (Mã Hex)
                holder.tvSeatName.setBackgroundColor(Color.parseColor("#EEEEEE"));
                holder.tvSeatName.setTextColor(Color.BLACK);
            }

            // 3. Xử lý click chọn/bỏ chọn (Giữ nguyên logic)
            holder.itemView.setOnClickListener(v -> {
                boolean newSelectedState = !seat.isSelected();
                seat.setSelected(newSelectedState);

                // Cập nhật giao diện của item hiện tại
                notifyItemChanged(position);

                // Báo cho Activity biết để cập nhật tổng tiền
                if (listener != null) {
                    listener.onSeatSelected(seat, newSelectedState);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public static class SeatViewHolder extends RecyclerView.ViewHolder {
        final TextView tvSeatName;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cần tạo TextView trong item_seat.xml có ID tv_seat_name
            tvSeatName = itemView.findViewById(R.id.tv_seat_name);
        }
    }
}
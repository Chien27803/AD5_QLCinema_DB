package com.example.ad5;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final Context context;
    private List<Ticket> ticketList;
    private final DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");
    private final OnTicketActionListener listener;

    public interface OnTicketActionListener {
        void onConfirmPayment(Ticket ticket);
        void onCancelTicket(Ticket ticket);
        String getSeatsByTicketId(int ticketId);
    }

    // Constructor Admin (3 tham số)
    public TicketAdapter(Context context, List<Ticket> ticketList, OnTicketActionListener listener) {
        this.context = context;
        this.ticketList = ticketList;
        this.listener = listener;
    }

    // Constructor User (2 tham số)
    public TicketAdapter(Context context, List<Ticket> ticketList) {
        this(context, ticketList, null);
    }

    public void updateList(List<Ticket> newList) {
        this.ticketList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Giả định R.layout.item_ticket là layout quản lý Admin (item_order.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        // 1. Ánh xạ các trường từ JOIN
        holder.tvTicketId.setText("#ID: " + ticket.getTicket_id());
        holder.tvUserName.setText("Người đặt: " + ticket.getUserName());

        // Giờ đặt vé (booking time)
        String bookingTime = ticket.getBooking_time();
        if (bookingTime != null && bookingTime.length() >= 16) {
            // Cắt chuỗi để hiển thị Giờ HH:MM và Ngày MM/DD
            holder.tvBookingTime.setText(bookingTime.substring(11, 16) + " " + bookingTime.substring(5, 10));
        } else {
            holder.tvBookingTime.setText("N/A");
        }

        // Phim & Phòng (JOIN data)
        holder.tvMovieAndRoom.setText(ticket.getMovie_name() + " - " + ticket.getRoom_name());

        // Chi tiết suất chiếu (Ngày & Giờ Bắt đầu)
        holder.tvShowtimeDetail.setText(ticket.getShowtimeDate() + " lúc " + ticket.getShowtimeStart());

        // Tổng tiền
        holder.tvTotalMoney.setText(currencyFormat.format(ticket.getTotal_money()));

        // 2. Lấy thông tin Ghế
        if (listener != null) {
            // LƯU Ý: Hàm này yêu cầu bạn tạo hàm getSeatsByTicketId(int) trong DBHelper
            String seatsString = listener.getSeatsByTicketId(ticket.getTicket_id());
            holder.tvSeats.setText("Ghế: " + seatsString);
        } else {
            holder.tvSeats.setText("Ghế: A1");
        }


        // 3. Xử lý Trạng thái và Nút Admin
        setTicketStatusAndActions(holder, ticket.getStatus(), ticket);
    }

    // Phương thức xử lý hiển thị trạng thái và hành động
    private void setTicketStatusAndActions(@NonNull TicketViewHolder holder, String status, Ticket ticket) {
        String statusDisplay;
        int statusColor;
        int textColor = Color.BLACK; // Mặc định là đen

        // Ẩn các nút mặc định
        holder.btnConfirmPayment.setVisibility(View.GONE);
        holder.btnCancelTicket.setVisibility(View.GONE);

        switch (status.toLowerCase()) {
            case "booked":
            case "confirmed":
                statusDisplay = "✓ ĐÃ THANH TOÁN";
                // Màu xanh lá đậm: #2E7D32 (Dark Green)
                statusColor = Color.parseColor("#2E7D32");
                textColor = Color.WHITE; // Chữ trắng dễ nhìn hơn trên nền đậm

                if (listener != null) holder.btnCancelTicket.setVisibility(View.VISIBLE);
                break;
            case "pending":
                statusDisplay = "⏳ CHỜ THANH TOÁN";
                // Màu cam/vàng đậm: #FFC107 (Amber/Orange)
                statusColor = Color.parseColor("#FFC107");
                textColor = Color.BLACK; // Chữ đen dễ nhìn hơn trên nền sáng

                if (listener != null) {
                    holder.btnConfirmPayment.setVisibility(View.VISIBLE);
                    holder.btnCancelTicket.setVisibility(View.VISIBLE);
                    holder.btnConfirmPayment.setOnClickListener(v -> listener.onConfirmPayment(ticket));
                }
                break;
            case "cancelled":
                statusDisplay = "✗ ĐÃ HỦY";
                // Màu đỏ đậm: #C62828 (Dark Red)
                statusColor = Color.parseColor("#C62828");
                textColor = Color.WHITE; // Chữ trắng
                break;
            default:
                statusDisplay = "TRẠNG THÁI KHÔNG RÕ";
                statusColor = Color.GRAY;
                textColor = Color.WHITE;
                break;
        }

        holder.tvStatus.setText(statusDisplay);
        // >> Đặt màu nền trực tiếp bằng mã Hex đã parse
        holder.tvStatus.setBackgroundColor(statusColor);
        holder.tvStatus.setTextColor(textColor);

        // Xử lý nút Hủy vé
        if (listener != null && holder.btnCancelTicket.getVisibility() == View.VISIBLE) {
            holder.btnCancelTicket.setOnClickListener(v -> listener.onCancelTicket(ticket));
        }
    }


    @Override
    public int getItemCount() {
        return ticketList != null ? ticketList.size() : 0;
    }

    // --- REVISED VIEWHOLDER ---
    static class TicketViewHolder extends RecyclerView.ViewHolder {

        TextView tvTicketId, tvStatus;
        TextView tvUserName, tvMovieAndRoom, tvShowtimeDetail;
        TextView tvSeats, tvTotalMoney, tvBookingTime;

        Button btnConfirmPayment;
        Button btnCancelTicket;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);

            // SỬA LỖI #1: Đã thống nhất ID
            tvTicketId = itemView.findViewById(R.id.tvTicketId);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus); // ID trong XML là tvOrderStatus

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
            tvMovieAndRoom = itemView.findViewById(R.id.tvMovieAndRoom);
            tvShowtimeDetail = itemView.findViewById(R.id.tvShowtimeDetail);
            tvSeats = itemView.findViewById(R.id.tvSeats_);
            tvTotalMoney = itemView.findViewById(R.id.tvTotalMoney);

            // Nút thao tác
            btnConfirmPayment = itemView.findViewById(R.id.btnConfirmPayment);
            btnCancelTicket = itemView.findViewById(R.id.btnCancelTicket);
        }
    }
}
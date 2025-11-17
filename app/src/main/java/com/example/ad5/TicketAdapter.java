package com.example.ad5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private List<TicketItem> ticketList;

    public TicketAdapter(Context context, List<TicketItem> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        TicketItem ticket = ticketList.get(position);

        holder.tvMovieName.setText(ticket.getMovieName());
        holder.tvShowDate.setText(ticket.getShowDate());
        holder.tvShowTime.setText(ticket.getShowTime());
        holder.tvSeats.setText(ticket.getSeats());
        holder.tvPaymentMethod.setText(ticket.getPaymentMethod());
        holder.tvTotalPrice.setText(String.format("%,dđ", ticket.getTotalMoney()));
        holder.tvTicketId.setText("Mã vé: #" + ticket.getTicketId());

        // Load image
        Glide.with(context)
                .load(ticket.getMovieImage())
                .placeholder(R.drawable.ic_movie_placeholder)
                .error(R.drawable.ic_movie_placeholder)
                .into(holder.imgMovie);

        // Status badge
        if ("booked".equals(ticket.getStatus())) {
            holder.tvStatus.setText("✓ Đã đặt");
            holder.tvStatus.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_green_dark));
        } else if ("cancelled".equals(ticket.getStatus())) {
            holder.tvStatus.setText("✗ Đã hủy");
            holder.tvStatus.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMovie;
        TextView tvMovieName, tvShowDate, tvShowTime, tvSeats, tvPaymentMethod, tvTotalPrice, tvTicketId, tvStatus;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvShowDate = itemView.findViewById(R.id.tvShowDate);
            tvShowTime = itemView.findViewById(R.id.tvShowTime);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvTicketId = itemView.findViewById(R.id.tvTicketId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
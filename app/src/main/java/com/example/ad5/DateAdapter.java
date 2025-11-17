package com.example.ad5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private Context context;
    private List<DateItem> dateList;
    private int selectedPosition = -1;
    private OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(DateItem dateItem, int position);
    }

    public DateAdapter(Context context, List<DateItem> dateList, OnDateClickListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateItem dateItem = dateList.get(position);

        holder.tvDayName.setText(dateItem.getDayName());
        holder.tvDayNumber.setText(dateItem.getDayNumber());
        holder.tvMonth.setText(dateItem.getMonth());

        // Highlight selected date
        if (selectedPosition == position) {
            holder.cardDate.setStrokeColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.cardDate.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
            holder.tvDayName.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.tvDayNumber.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.tvMonth.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.cardDate.setStrokeColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.cardDate.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.tvDayName.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.tvDayNumber.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.tvMonth.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        holder.cardDate.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            listener.onDateClick(dateItem, selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardDate;
        TextView tvDayName, tvDayNumber, tvMonth;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDate = itemView.findViewById(R.id.cardDate);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            tvMonth = itemView.findViewById(R.id.tvMonth);
        }
    }
}
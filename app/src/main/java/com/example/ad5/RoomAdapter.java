package com.example.ad5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private Context context;
    private List<Room> roomList;
    private OnRoomActionListener listener;

    public interface OnRoomActionListener {
        void onEditClick(Room room);
        void onDeleteClick(Room room);
    }

    public RoomAdapter(Context context, List<Room> roomList, OnRoomActionListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.tvRoomName.setText(room.getRoom_name());
        holder.tvSeats.setText("Số ghế: " + room.getQuantity_seat());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(room));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(room));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public void updateList(List<Room> newList) {
        this.roomList = newList;
        notifyDataSetChanged();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvSeats;
        ImageButton btnEdit, btnDelete;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

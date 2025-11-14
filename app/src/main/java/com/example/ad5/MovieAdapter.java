package com.example.ad5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ad5.Movie;

import com.bumptech.glide.Glide;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private OnMovieActionListener listener;
    private Context context;

    // Interface để xử lý sự kiện click nút Sửa và Xóa trong Activity/Fragment
    public interface OnMovieActionListener {
        void onEditClick(Movie movie);
        void onDeleteClick(Movie movie);
    }

    public MovieAdapter(Context context, List<Movie> movieList, OnMovieActionListener listener) {
        this.context = context;
        this.movieList = movieList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ layout CardView bạn đã tạo
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        final Movie currentMovie = movieList.get(position);

        // 1. Đổ dữ liệu từ Model (sử dụng các getter theo tên cột DB)
        holder.tvMovieTitle.setText(currentMovie.getMovie_name());
        holder.tvMovieType.setText("Thể loại: " + currentMovie.getMovie_type());

        // Hiển thị DURATION
        holder.tvMovieDuration.setText("Thời lượng: " + currentMovie.getDuration() + " phút");
        holder.tvDescription.setText("Mô tả: " + currentMovie.getDescription());

        holder.tvStatus.setText("Trạng thái: "+currentMovie.getStatus());

        // 2. Tải ảnh từ URL bằng Glide
        String imageUrl = currentMovie.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.inside2) // Ảnh hiển thị trong khi tải
                    .error(R.drawable.inside2) // Ảnh hiển thị nếu tải thất bại
                    .into(holder.imgMovie);

        } else {
            // Đặt ảnh mặc định khi không có URL ảnh
            holder.imgMovie.setImageResource(R.drawable.inside2);
        }

        // 3. Xử lý sự kiện click cho các nút Sửa/Xóa
        holder.btnEditMovie.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(currentMovie);
            }
        });

        holder.btnDeleteMovie.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(currentMovie);
            }
        });

        // Xử lý sự kiện click cho cả CardView
        holder.itemView.setOnClickListener(v -> {
            // Logic xem chi tiết phim
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // Phương thức tiện ích để cập nhật danh sách
    public void updateList(List<Movie> newList) {
        this.movieList = newList;
        notifyDataSetChanged();
    }

    // Phương thức tiện ích để xóa một item
    public void removeItem(Movie movie) {
        int position = movieList.indexOf(movie);
        if (position > -1) {
            movieList.remove(position);
            notifyItemRemoved(position);
        }
    }


    // Lớp ViewHolder (Ánh xạ các thành phần giao diện)
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvMovieType, tvMovieDuration,tvStatus,tvDescription;
        ImageView imgMovie, btnEditMovie, btnDeleteMovie;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ ID từ file XML item_movie.xml (Đảm bảo ID khớp)
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieType = itemView.findViewById(R.id.tvMovieType);
            tvMovieDuration = itemView.findViewById(R.id.tvMovieDuration);
            tvStatus= itemView.findViewById(R.id.tvMovieStatus);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            btnEditMovie = itemView.findViewById(R.id.btnEditMovie);
            btnDeleteMovie = itemView.findViewById(R.id.btnDeleteMovie);
            tvDescription = itemView.findViewById(R.id.tvDescription);

        }
    }
}
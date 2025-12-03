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

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;

    // Listener click mở chi tiết (1 method → dùng được lambda movie -> {})
    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    // Listener cho màn admin: sửa / xóa
    public interface OnMovieActionListener {
        void onEditClick(Movie movie);
        void onDeleteClick(Movie movie);
    }

    private OnMovieClickListener clickListener;
    private OnMovieActionListener actionListener;

    // Constructor dùng ở màn xem danh sách (click để xem chi tiết)
    public MovieAdapter(Context context, List<Movie> movieList, OnMovieClickListener clickListener) {
        this.context = context;
        this.movieList = movieList;
        this.clickListener = clickListener;
    }

    // Constructor dùng ở màn admin (Sửa/Xóa)
    public MovieAdapter(Context context, List<Movie> movieList, OnMovieActionListener actionListener) {
        this.context = context;
        this.movieList = movieList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        final Movie currentMovie = movieList.get(position);

        holder.tvMovieTitle.setText(currentMovie.getMovie_name());
        holder.tvMovieType.setText("Thể loại: " + currentMovie.getMovie_type());
        holder.tvMovieDuration.setText("Thời lượng: " + currentMovie.getDuration() + " phút");
        holder.tvDescription.setText("Mô tả: " + currentMovie.getDescription());
        holder.tvStatus.setText("Trạng thái: " + currentMovie.getStatus());

        String imageUrl = currentMovie.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.inside2)
                    .error(R.drawable.inside2)
                    .into(holder.imgMovie);
        } else {
            holder.imgMovie.setImageResource(R.drawable.inside2);
        }

        // Click Sửa/Xóa (dành cho màn admin)
        holder.btnEditMovie.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(currentMovie);
            }
        });

        holder.btnDeleteMovie.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClick(currentMovie);
            }
        });

        // Click cả item → mở chi tiết (dùng ở màn ALLMoviesActivity)
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onMovieClick(currentMovie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    public void updateList(List<Movie> newList) {
        this.movieList = newList;
        notifyDataSetChanged();
    }

    public void removeItem(Movie movie) {
        int position = movieList.indexOf(movie);
        if (position > -1) {
            movieList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvMovieType, tvMovieDuration, tvStatus, tvDescription;
        ImageView imgMovie, btnEditMovie, btnDeleteMovie;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieType = itemView.findViewById(R.id.tvMovieType);
            tvMovieDuration = itemView.findViewById(R.id.tvMovieDuration);
            tvStatus = itemView.findViewById(R.id.tvMovieStatus);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            btnEditMovie = itemView.findViewById(R.id.btnEditMovie);
            btnDeleteMovie = itemView.findViewById(R.id.btnDeleteMovie);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}

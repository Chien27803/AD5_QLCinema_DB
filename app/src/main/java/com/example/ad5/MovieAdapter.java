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
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(Context context, List<Movie> movieList, OnMovieClickListener listener) {
        this.context = context;
        this.movieList = movieList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.tvMovieName.setText(movie.getMovie_name());
        holder.tvMovieType.setText(movie.getMovie_type());
        holder.tvLanguage.setText(movie.getLanguage());
        holder.tvRating.setText(String.format("%.1f", movie.getPoint()));

        // Load ảnh (có thể dùng Glide/Picasso)
        // Để tạm thời dùng placeholder
        // TODO: Thêm thư viện Glide và uncomment dòng dưới
        // Glide.with(context).load(movie.getImage()).placeholder(R.drawable.ic_movie_placeholder).into(holder.imgMovie);
        
        holder.imgMovie.setImageResource(R.drawable.ic_movie_placeholder);

        holder.cardMovie.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        });

        Glide.with(context)
                .load(movie.getImage()) // Lấy URL từ đối tượng Movie
//                .placeholder(R.drawable.placeholder_image) // Tùy chọn: Ảnh hiển thị trong lúc đang tải
//                .error(R.drawable.error_image) // Tùy chọn: Ảnh hiển thị nếu tải thất bại
                .into(holder.imgMovie); // Đặt ảnh vào ImageView
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardMovie;
        ImageView imgMovie;
        TextView tvMovieName, tvMovieType, tvLanguage, tvRating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMovie = itemView.findViewById(R.id.cardMovie);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvMovieType = itemView.findViewById(R.id.tvMovieType);
            tvLanguage = itemView.findViewById(R.id.tvLanguage);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}

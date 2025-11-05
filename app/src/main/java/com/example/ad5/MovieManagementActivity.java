package com.example.ad5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;




import java.util.ArrayList;
import java.util.List;

// Activity này triển khai interface OnMovieActionListener để xử lý sự kiện Sửa/Xóa
public class MovieManagementActivity extends AppCompatActivity implements MovieAdapter.OnMovieActionListener {

    private RecyclerView recyclerView;
    private Button btnAddMovie;
    private MovieAdapter movieAdapter;
    private DBHelper dbHelper;
    private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ánh xạ layout XML bạn vừa cung cấp
        setContentView(R.layout.activity_manage_movies);

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Ánh xạ các View từ layout
        recyclerView = findViewById(R.id.rvMovies);
        btnAddMovie = findViewById(R.id.btnAddMovie);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load dữ liệu và thiết lập Adapter
        loadMovies();

        // Xử lý sự kiện khi nhấn nút "Thêm phim"
        btnAddMovie.setOnClickListener(v -> {
            onAddMovieClick();
        });
    }

    // Phương thức tải phim từ DB và cập nhật Adapter
    private void loadMovies() {
        // Lấy danh sách phim từ SQLite
        movieList = dbHelper.getAllMovies();

        if (movieList == null) {
            movieList = new ArrayList<>();
        }

        // Khởi tạo MovieAdapter. 'this' là listener cho các nút Sửa/Xóa
        movieAdapter = new MovieAdapter(this, movieList, this);
        recyclerView.setAdapter(movieAdapter);
    }

    // Phương thức xử lý khi nhấn nút Thêm Phim
    private void onAddMovieClick() {
        Toast.makeText(this, "Mở màn hình Thêm phim...", Toast.LENGTH_SHORT).show();
        // TODO: Chuyển sang Activity/Dialog để thêm phim mới
        // Intent intent = new Intent(this, AddEditMovieActivity.class);
        // startActivity(intent);
    }

    // =======================================================
    // TRIỂN KHAI PHƯƠNG THỨC XỬ LÝ SỰ KIỆN TỪ ADAPTER
    // =======================================================

    @Override
    public void onEditClick(Movie movie) {
        // Xử lý khi người dùng nhấn nút Sửa
        Toast.makeText(this, "Chuyển sang màn hình Sửa phim: " + movie.getMovie_name(), Toast.LENGTH_SHORT).show();
        // TODO: Viết logic để mở màn hình chỉnh sửa phim và truyền ID phim
    }

    @Override
    public void onDeleteClick(Movie movie) {
        // Xử lý khi người dùng nhấn nút Xóa
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy phim")
                .setMessage("Bạn có chắc chắn muốn chuyển trạng thái phim '" + movie.getMovie_name() + "' sang 'Đã hủy'?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {

                    // ********** SỬA ĐỔI QUAN TRỌNG TẠI ĐÂY **********
                    int affectedRows = dbHelper.markMovieAsCanceled(movie.getMovie_id());

                    if (affectedRows > 0) {
                        // Cập nhật lại danh sách để thấy trạng thái mới
                        movieAdapter.updateList(dbHelper.getAllMovies());
                        Toast.makeText(this, "Đã hủy phim: " + movie.getMovie_name(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Lỗi khi hủy phim.", Toast.LENGTH_SHORT).show();
                    }
                    // *************************************************

                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Quan trọng: Cập nhật danh sách mỗi khi quay lại Activity (sau khi Thêm/Sửa/Xóa)
        if (movieAdapter != null) {
            movieAdapter.updateList(dbHelper.getAllMovies());
        }
    }
}
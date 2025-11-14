package com.example.ad5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    private static final int REQUEST_PICK_IMAGE = 1001;

    private Uri selectedImageUri = null;
    private ImageView imgPreviewAddMovie;
    private android.app.Dialog addMovieDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ánh xạ layout XML bạn vừa cung cấp
        setContentView(R.layout.activity_manage_movies);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bật nút back (mũi tên ←)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Xử lý khi nhấn nút back
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
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
        // Tạo dialog
        addMovieDialog = new android.app.Dialog(this);
        addMovieDialog.setContentView(R.layout.dialog_add_movie);

        if (addMovieDialog.getWindow() != null) {
            addMovieDialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
            );
        }

        // Ánh xạ view
        android.widget.EditText edtTitle       = addMovieDialog.findViewById(R.id.edtAddMovieTitle);
        android.widget.EditText edtType        = addMovieDialog.findViewById(R.id.edtAddMovieType);
        android.widget.EditText edtDuration    = addMovieDialog.findViewById(R.id.edtAddMovieDuration);
        android.widget.EditText edtDescription = addMovieDialog.findViewById(R.id.edtAddMovieDescription);
        android.widget.EditText edtStatus      = addMovieDialog.findViewById(R.id.edtAddMovieStatus);
        android.widget.Button   btnSave        = addMovieDialog.findViewById(R.id.btnSaveAdd);

        imgPreviewAddMovie = addMovieDialog.findViewById(R.id.imgAddMoviePreview);
        android.widget.Button btnChooseImage   = addMovieDialog.findViewById(R.id.btnChooseImage);

        selectedImageUri = null; // reset mỗi lần mở dialog

        // ===== Chọn ảnh từ thư viện =====
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh poster"), REQUEST_PICK_IMAGE);
        });

        // ===== Nút Thêm =====
        btnSave.setOnClickListener(v -> {
            String title    = edtTitle.getText().toString().trim();
            String type     = edtType.getText().toString().trim();
            String durStr   = edtDuration.getText().toString().trim();
            String desc     = edtDescription.getText().toString().trim();
            String status   = edtStatus.getText().toString().trim();

            if (title.isEmpty() || type.isEmpty() || durStr.isEmpty() || status.isEmpty()) {
                Toast.makeText(MovieManagementActivity.this,
                        "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            int duration;
            try {
                duration = Integer.parseInt(durStr);
            } catch (NumberFormatException e) {
                Toast.makeText(MovieManagementActivity.this,
                        "Thời lượng phải là số", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            // Upload + lưu DB trong thread riêng
            new Thread(() -> {
                String imageUrl = CloudinaryUploader.uploadImage(MovieManagementActivity.this, selectedImageUri);

                runOnUiThread(() -> {
                    if (imageUrl == null) {
                        Toast.makeText(MovieManagementActivity.this,
                                "Upload ảnh thất bại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long newId = dbHelper.addMovie(title, type, duration, desc, status, imageUrl);

                    if (newId != -1) {
                        movieAdapter.updateList(dbHelper.getAllMovies());
                        Toast.makeText(MovieManagementActivity.this,
                                "Đã thêm phim mới", Toast.LENGTH_SHORT).show();
                        addMovieDialog.dismiss();
                    } else {
                        Toast.makeText(MovieManagementActivity.this,
                                "Lỗi khi thêm phim", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        addMovieDialog.show();
        if (addMovieDialog.getWindow() != null) {
            android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int width = (int) (metrics.widthPixels * 0.9); // 90% chiều ngang

            addMovieDialog.getWindow().setLayout(
                    width,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }


    // =======================================================
    // TRIỂN KHAI PHƯƠNG THỨC XỬ LÝ SỰ KIỆN TỪ ADAPTER
    // =======================================================

    @Override
    public void onEditClick(Movie movie) {
        // Tạo Dialog
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_movie);

        // Nếu muốn nền bo góc đẹp (bg_dialog_round) hiển thị đúng:
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
            );
        }

        // Ánh xạ view trong dialog

        android.widget.EditText edtTitle    = dialog.findViewById(R.id.edtMovieTitle);
        android.widget.EditText edtType     = dialog.findViewById(R.id.edtMovieType);
        android.widget.EditText edtDuration = dialog.findViewById(R.id.edtMovieDuration);
        android.widget.EditText edtDesciption = dialog.findViewById(R.id.edtDescription);
        android.widget.EditText edtStatus   = dialog.findViewById(R.id.edtMovieStatus);
        android.widget.Button btnSave       = dialog.findViewById(R.id.btnEdit);


        // Đổ sẵn dữ liệu từ movie vào dialog
        edtTitle.setText(movie.getMovie_name());      // đổi theo getter của bạn
        edtType.setText(movie.getMovie_type());       // ví dụ
        edtDuration.setText(String.valueOf(movie.getDuration())); // ví dụ
        edtDesciption.setText(movie.getDescription());
        edtStatus.setText(movie.getStatus());         // ví dụ


        // Nút Lưu
        btnSave.setOnClickListener(v -> {
            String newTitle    = edtTitle.getText().toString().trim();
            String newType     = edtType.getText().toString().trim();
            String newDuration = edtDuration.getText().toString().trim();
            String newDesciption = edtDesciption.getText().toString().trim();
            String newStatus   = edtStatus.getText().toString().trim();

            if (newTitle.isEmpty() || newType.isEmpty() || newDuration.isEmpty() || newStatus.isEmpty()) {
                Toast.makeText(MovieManagementActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int durationInt;
            try {
                durationInt = Integer.parseInt(newDuration);
            } catch (NumberFormatException e) {
                Toast.makeText(MovieManagementActivity.this, "Thời lượng phải là số", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật object movie
            movie.setMovie_name(newTitle);
            movie.setMovie_type(newType);
            movie.setDuration(durationInt);
            movie.setDescription(newDesciption);
            movie.setStatus(newStatus);

            // Gọi DBHelper để update (điều chỉnh theo hàm bạn đang có)
            // Ví dụ:
            int rows = dbHelper.updateMovie(movie);

            if (rows > 0) {
                // Cập nhật lại list trên màn hình
                movieAdapter.updateList(dbHelper.getAllMovies());
                Toast.makeText(MovieManagementActivity.this, "Đã cập nhật phim", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(MovieManagementActivity.this, "Lỗi khi cập nhật phim", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int width = (int) (metrics.widthPixels * 0.9); // 90% chiều ngang

            dialog.getWindow().setLayout(
                    width,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedImageUri = uri;
                if (imgPreviewAddMovie != null) {
                    imgPreviewAddMovie.setImageURI(uri);
                }
            }
        }
    }
//ảnh phim mưa đỏ
    //Sắp chiếu
}
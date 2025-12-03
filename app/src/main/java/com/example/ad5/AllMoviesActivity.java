//package com.example.ad5;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.google.android.material.card.MaterialCardView;
//import com.google.android.material.chip.Chip;
//import com.google.android.material.chip.ChipGroup;
//import java.util.ArrayList;
//import java.util.List;
//
//public class AllMoviesActivity extends AppCompatActivity {
//
//    private RecyclerView rvMovies;
//    private MovieAdapter movieAdapter;
//    private DBHelper dbHelper;
//    private User currentUser;
//
//    // Views
//    private ImageView btnBack, btnSearch, btnClearSearch;
//    private MaterialCardView searchCard, cardPagination;
//    private EditText etSearch;
//    private ChipGroup chipGroup;
//    private Chip chipAll, chipNowShowing, chipComingSoon, chipStopped;
//    private TextView tvMovieCount, tvCurrentPage, tvTotalPages, tvItemsInfo;
//    private ImageView btnFirstPage, btnPrevPage, btnNextPage, btnLastPage;
//    private View layoutEmpty;
//
//    // Pagination
//    private List<Movie> allMovies = new ArrayList<>();
//    private List<Movie> filteredMovies = new ArrayList<>();
//    private List<Movie> currentPageMovies = new ArrayList<>();
//    private int currentPage = 1;
//    private int itemsPerPage = 6;
//    private int totalPages = 1;
//
//    // Filter
//    private String currentFilter = "Tất cả";
//    private String searchQuery = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_all_showtime);
//
//        dbHelper = new DBHelper(this);
//        currentUser = (User) getIntent().getSerializableExtra("user");
//
//        initViews();
//        setupRecyclerView();
//        setupClickListeners();
//        loadAllMovies();
//    }
//
//    private void initViews() {
//        btnBack = findViewById(R.id.btnBack);
//        btnSearch = findViewById(R.id.btnSearch);
//        btnClearSearch = findViewById(R.id.btnClearSearch);
//        searchCard = findViewById(R.id.searchCard);
//        etSearch = findViewById(R.id.etSearch);
//
//        chipGroup = findViewById(R.id.chipGroup);
//        chipAll = findViewById(R.id.chipAll);
//        chipNowShowing = findViewById(R.id.chipNowShowing);
//        chipComingSoon = findViewById(R.id.chipComingSoon);
//        chipStopped = findViewById(R.id.chipStopped);
//
//        rvMovies = findViewById(R.id.rvMovies);
//        layoutEmpty = findViewById(R.id.layoutEmpty);
//        tvMovieCount = findViewById(R.id.tvMovieCount);
//
//        cardPagination = findViewById(R.id.cardPagination);
//        tvCurrentPage = findViewById(R.id.tvCurrentPage);
//        tvTotalPages = findViewById(R.id.tvTotalPages);
//        tvItemsInfo = findViewById(R.id.tvItemsInfo);
//        btnFirstPage = findViewById(R.id.btnFirstPage);
//        btnPrevPage = findViewById(R.id.btnPrevPage);
//        btnNextPage = findViewById(R.id.btnNextPage);
//        btnLastPage = findViewById(R.id.btnLastPage);
//    }
//
//    private void setupRecyclerView() {
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
//        rvMovies.setLayoutManager(layoutManager);
//
//        movieAdapter = new MovieAdapter(this, currentPageMovies, movie -> {
//            Intent intent = new Intent(AllMoviesActivity.this, MovieDetailActivity.class);
//            intent.putExtra("movie", movie);
//            intent.putExtra("user", currentUser);
//            startActivity(intent);
//        });
//        rvMovies.setAdapter(movieAdapter);
//    }
//
//    private void setupClickListeners() {
//        // Back button
//        btnBack.setOnClickListener(v -> finish());
//
//        // Search button
//        btnSearch.setOnClickListener(v -> {
//            if (searchCard.getVisibility() == View.GONE) {
//                searchCard.setVisibility(View.VISIBLE);
//                etSearch.requestFocus();
//            } else {
//                searchCard.setVisibility(View.GONE);
//                etSearch.setText("");
//            }
//        });
//
//        // Clear search
//        btnClearSearch.setOnClickListener(v -> etSearch.setText(""));
//
//        // Search text watcher
//        etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                searchQuery = s.toString().trim();
//                btnClearSearch.setVisibility(searchQuery.isEmpty() ? View.GONE : View.VISIBLE);
//                applyFilterAndSearch();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//
//        // Filter chips
//        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
//            if (checkedIds.contains(R.id.chipAll)) {
//                currentFilter = "Tất cả";
//            } else if (checkedIds.contains(R.id.chipNowShowing)) {
//                currentFilter = "Đang chiếu";
//            } else if (checkedIds.contains(R.id.chipComingSoon)) {
//                currentFilter = "Sắp chiếu";
//            } else if (checkedIds.contains(R.id.chipStopped)) {
//                currentFilter = "Ngừng chiếu";
//            }
//            applyFilterAndSearch();
//        });
//
//        // Pagination buttons
//        btnFirstPage.setOnClickListener(v -> goToPage(1));
//        btnPrevPage.setOnClickListener(v -> goToPage(currentPage - 1));
//        btnNextPage.setOnClickListener(v -> goToPage(currentPage + 1));
//        btnLastPage.setOnClickListener(v -> goToPage(totalPages));
//    }
//
//    private void loadAllMovies() {
//        allMovies = dbHelper.getAllMovies();
//        applyFilterAndSearch();
//    }
//
//    private void applyFilterAndSearch() {
//        filteredMovies.clear();
//
//        for (Movie movie : allMovies) {
//            boolean matchFilter = currentFilter.equals("Tất cả") ||
//                    movie.getStatus().equals(currentFilter);
//            boolean matchSearch = searchQuery.isEmpty() ||
//                    movie.getMovie_name().toLowerCase().contains(searchQuery.toLowerCase());
//
//            if (matchFilter && matchSearch) {
//                filteredMovies.add(movie);
//            }
//        }
//
//        calculatePagination();
//        goToPage(1);
//    }
//
//    private void calculatePagination() {
//        int totalItems = filteredMovies.size();
//        totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
//        if (totalPages == 0) totalPages = 1;
//    }
//
//    private void goToPage(int page) {
//        if (page < 1 || page > totalPages) return;
//
//        currentPage = page;
//        updatePageData();
//        updateUI();
//
//        // Scroll to top
//        rvMovies.smoothScrollToPosition(0);
//    }
//
//    private void updatePageData() {
//        currentPageMovies.clear();
//
//        int startIndex = (currentPage - 1) * itemsPerPage;
//        int endIndex = Math.min(startIndex + itemsPerPage, filteredMovies.size());
//
//        for (int i = startIndex; i < endIndex; i++) {
//            currentPageMovies.add(filteredMovies.get(i));
//        }
//    }
//
//    private void updateUI() {
//        // Update adapter
//        movieAdapter.notifyDataSetChanged();
//
//        // Show/hide empty state
//        if (filteredMovies.isEmpty()) {
//            rvMovies.setVisibility(View.GONE);
//            cardPagination.setVisibility(View.GONE);
//            layoutEmpty.setVisibility(View.VISIBLE);
//        } else {
//            rvMovies.setVisibility(View.VISIBLE);
//            cardPagination.setVisibility(View.VISIBLE);
//            layoutEmpty.setVisibility(View.GONE);
//        }
//
//        // Update movie count
//        String countText = "Tìm thấy " + filteredMovies.size() + " phim";
//        tvMovieCount.setText(countText);
//
//        // Update pagination info
//        tvCurrentPage.setText(String.valueOf(currentPage));
//        tvTotalPages.setText(String.valueOf(totalPages));
//
//        // Update items info
//        int startItem = (currentPage - 1) * itemsPerPage + 1;
//        int endItem = Math.min(currentPage * itemsPerPage, filteredMovies.size());
//        String itemsText = "Hiển thị " + startItem + "-" + endItem +
//                " trong tổng số " + filteredMovies.size() + " phim";
//        tvItemsInfo.setText(itemsText);
//
//        // Enable/disable pagination buttons
//        btnFirstPage.setEnabled(currentPage > 1);
//        btnPrevPage.setEnabled(currentPage > 1);
//        btnNextPage.setEnabled(currentPage < totalPages);
//        btnLastPage.setEnabled(currentPage < totalPages);
//
//        btnFirstPage.setAlpha(currentPage > 1 ? 1.0f : 0.3f);
//        btnPrevPage.setAlpha(currentPage > 1 ? 1.0f : 0.3f);
//        btnNextPage.setAlpha(currentPage < totalPages ? 1.0f : 0.3f);
//        btnLastPage.setAlpha(currentPage < totalPages ? 1.0f : 0.3f);
//    }
//}

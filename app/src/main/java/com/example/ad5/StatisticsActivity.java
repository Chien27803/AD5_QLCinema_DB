package com.example.ad5;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    private BarChart barChart;
    private PieChart pieChart;
    private TextView tvTotalRevenue, tvTotalTickets, tvTotalUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Ánh xạ - SỬA LẠI THEO LAYOUT MỚI
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        // Thêm các TextView mới từ layout
        TextView tvTicketsSold = findViewById(R.id.tvTicketsSold);
        TextView tvShowtimes = findViewById(R.id.tvShowtimes);
        TextView tvTopMovieTitle = findViewById(R.id.tvTopMovieTitle);
        TextView tvTopMovieRevenue = findViewById(R.id.tvTopMovieRevenue);
        TextView tvTopMovieTickets = findViewById(R.id.tvTopMovieTickets);

        barChart = findViewById(R.id.barChartRevenue);
        // pieChart = findViewById(R.id.pieChartGenre); // Bỏ vì layout mới không có PieChart

        // Load dữ liệu mẫu
        loadStatistics();
    }

    private void loadStatistics() {
        // 🧩 Dữ liệu mẫu cho layout mới
        tvTotalRevenue.setText("120.000.000 VNĐ");

        // Cập nhật dữ liệu cho các view mới
        TextView tvTicketsSold = findViewById(R.id.tvTicketsSold);
        TextView tvShowtimes = findViewById(R.id.tvShowtimes);
        TextView tvTopMovieTitle = findViewById(R.id.tvTopMovieTitle);
        TextView tvTopMovieRevenue = findViewById(R.id.tvTopMovieRevenue);
        TextView tvTopMovieTickets = findViewById(R.id.tvTopMovieTickets);

        tvTicketsSold.setText("1.250");
        tvShowtimes.setText("45");
        tvTopMovieTitle.setText("Avengers: Endgame");
        tvTopMovieRevenue.setText("50.000.000 VNĐ");
        tvTopMovieTickets.setText("1.250 vé");

        // Biểu đồ doanh thu (BarChart)
        if (barChart != null) {
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            barEntries.add(new BarEntry(1, 4.5f));
            barEntries.add(new BarEntry(2, 6.2f));
            barEntries.add(new BarEntry(3, 7.1f));
            barEntries.add(new BarEntry(4, 3.9f));
            barEntries.add(new BarEntry(5, 8.7f));
            barEntries.add(new BarEntry(6, 5.4f));
            barEntries.add(new BarEntry(7, 9.3f));

            BarDataSet barDataSet = new BarDataSet(barEntries, "Doanh thu (triệu VNĐ)");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextSize(12f);

            BarData barData = new BarData(barDataSet);
            barChart.setData(barData);
            barChart.getDescription().setEnabled(false);
            barChart.getAxisRight().setEnabled(false);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            barChart.animateY(1000);
            barChart.invalidate();
        }

        // Biểu đồ tròn (PieChart) - BỎ VÌ LAYOUT MỚI KHÔNG CÓ
        /*
        if (pieChart != null) {
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            pieEntries.add(new PieEntry(40f, "Hành động"));
            pieEntries.add(new PieEntry(25f, "Tình cảm"));
            pieEntries.add(new PieEntry(20f, "Hoạt hình"));
            pieEntries.add(new PieEntry(15f, "Kinh dị"));

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextSize(12f);
            pieDataSet.setSliceSpace(3f);

            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("Thể loại phim");
            pieChart.setCenterTextSize(14f);
            pieChart.animateY(1000);
        }
        */
    }
}
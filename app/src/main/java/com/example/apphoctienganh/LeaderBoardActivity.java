package com.example.apphoctienganh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {
    private ListView listView;
    private List<UserPoint> list;
    private LeaderBoardAdapter adapter;
    private DataBasePointUser dataBasePointUser;
    private Spinner spinnerExamType;
    private Button btnViewLeaderboard;
    private TextView txtCurrentLeaderboard;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Mảng chứa các loại bài thi
    private String[] examTypes = {"Trắc nghiệm", "Ngữ pháp", "Luyện nghe"};
    private String currentExamType = "Trắc nghiệm"; // Mặc định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        dataBasePointUser = new DataBasePointUser(LeaderBoardActivity.this);
        mapping();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupSpinner();
        setupButton();
        setupSwipeRefresh();

        // Mặc định hiển thị bảng xếp hạng đầu tiên
        updateLeaderboard();
    }

    public void mapping() {
        list = new ArrayList<>();
        listView = findViewById(R.id.listLeaderBoard);
        spinnerExamType = findViewById(R.id.spinnerExamType);
        btnViewLeaderboard = findViewById(R.id.btnViewLeaderboard);
        txtCurrentLeaderboard = findViewById(R.id.txtCurrentLeaderboard);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    private void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, examTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExamType.setAdapter(spinnerAdapter);

        spinnerExamType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentExamType = examTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });
    }

    private void setupButton() {
        btnViewLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLeaderboard();
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLeaderboard();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void updateLeaderboard() {
        txtCurrentLeaderboard.setText("Bảng xếp hạng: " + currentExamType);

        try {
            // Lấy dữ liệu từ database theo loại bài thi
            list = getLeaderboardByExamType(currentExamType);

            if (list.isEmpty()) {
                Toast.makeText(this, "Chưa có dữ liệu cho phần thi này", Toast.LENGTH_SHORT).show();
            }

            // Sắp xếp danh sách theo điểm giảm dần
            Collections.sort(list, new Comparator<UserPoint>() {
                @Override
                public int compare(UserPoint p1, UserPoint p2) {
                    return Integer.compare(p2.getPoints(), p1.getPoints());
                }
            });

            adapter = new LeaderBoardAdapter(LeaderBoardActivity.this, list);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức mẫu - cần thay đổi tùy theo cấu trúc database thực tế của bạn
    private List<UserPoint> getLeaderboardByExamType(String examType) {
        switch(examType) {
            case "Trắc nghiệm":
                return dataBasePointUser.getUserPointsByExamType(DataBasePointUser.EXAM_TYPE_MULTIPLE_CHOICE);
            case "Ngữ pháp":
                return dataBasePointUser.getUserPointsByExamType(DataBasePointUser.EXAM_TYPE_GRAMMAR);
            case "Luyện nghe":
                return dataBasePointUser.getUserPointsByExamType(DataBasePointUser.EXAM_TYPE_LISTENING);
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LeaderBoardActivity.this, LayoutActivity.class);
        startActivity(intent);
        finish();
    }
}
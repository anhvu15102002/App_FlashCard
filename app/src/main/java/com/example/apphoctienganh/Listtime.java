package com.example.apphoctienganh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Listtime extends AppCompatActivity {

    private Button btnManageReminders;
    private Button btnStartLearning;
    private TextView textViewNextReminder;
    private TextView textViewStreakCount;
    private TextView textViewTotalLessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listtime);

        // Khởi tạo các view
        btnManageReminders = findViewById(R.id.btnManageReminders);
        btnStartLearning = findViewById(R.id.btnStartLearning);
        textViewNextReminder = findViewById(R.id.textViewNextReminder);
        textViewStreakCount = findViewById(R.id.textViewStreakCount);
        textViewTotalLessons = findViewById(R.id.textViewTotalLessons);

        // Mặc định hiển thị dữ liệu mẫu hoặc đọc từ SharedPreferences
        textViewStreakCount.setText("0");  // Sẽ đọc từ dữ liệu thực tế trong ứng dụng thực
        textViewTotalLessons.setText("0"); // Sẽ đọc từ dữ liệu thực tế trong ứng dụng thực

        // Thiết lập sự kiện cho các nút
        btnManageReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở activity quản lý nhắc nhở
                Intent intent = new Intent(Listtime.this, StudyReminderActivity.class);
                startActivity(intent);
            }
        });

        btnStartLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trong ứng dụng thực, sẽ chuyển đến màn hình bài học
                // Ví dụ mẫu này chỉ hiển thị thông báo đã bắt đầu học
                updateLearningStats();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật hiển thị thông tin nhắc nhở tiếp theo
        updateNextReminderInfo();
    }

    private void updateNextReminderInfo() {
        // Lấy thông tin về nhắc nhở sắp tới từ database
        ReminderDatabaseHelper dbHelper = new ReminderDatabaseHelper(this);
        ArrayList<ReminderItem> reminders = dbHelper.getAllReminders();

        if (reminders.isEmpty()) {
            textViewNextReminder.setText("Không có nhắc nhở nào");
            return;
        }

        // Lấy thời gian hiện tại
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        // Tìm nhắc nhở gần nhất
        ReminderItem nextReminder = null;
        int smallestTimeDiff = 24 * 60; // Khởi tạo là một ngày (tính theo phút)

        for (ReminderItem reminder : reminders) {
            if (!reminder.isActive()) continue;

            // Phân tích thời gian nhắc nhở
            String[] timeParts = reminder.getTime().split(":");
            if (timeParts.length != 2) continue;

            int reminderHour = Integer.parseInt(timeParts[0]);
            int reminderMinute = Integer.parseInt(timeParts[1]);

            // Tính khoảng cách thời gian (theo phút)
            int timeDiff;
            if (reminderHour > currentHour || (reminderHour == currentHour && reminderMinute > currentMinute)) {
                // Nhắc nhở trong ngày hôm nay
                timeDiff = (reminderHour - currentHour) * 60 + (reminderMinute - currentMinute);
            } else {
                // Nhắc nhở vào ngày mai
                timeDiff = ((24 + reminderHour) - currentHour) * 60 + (reminderMinute - currentMinute);
            }

            if (timeDiff < smallestTimeDiff) {
                smallestTimeDiff = timeDiff;
                nextReminder = reminder;
            }
        }

        if (nextReminder != null) {
            // Tạo định dạng thời gian thân thiện
            int hoursUntil = smallestTimeDiff / 60;
            int minutesUntil = smallestTimeDiff % 60;

            String timeMessage;
            if (hoursUntil == 0 && minutesUntil == 0) {
                timeMessage = "Sắp tới giờ học rồi!";
            } else if (hoursUntil == 0) {
                timeMessage = "Còn " + minutesUntil + " phút nữa đến giờ học";
            } else if (minutesUntil == 0) {
                timeMessage = "Còn " + hoursUntil + " giờ nữa đến giờ học";
            } else {
                timeMessage = "Còn " + hoursUntil + " giờ " + minutesUntil + " phút nữa đến giờ học";
            }

            textViewNextReminder.setText(timeMessage);
        } else {
            textViewNextReminder.setText("Không có nhắc nhở nào hoạt động");
        }
    }

    private void updateLearningStats() {
        // Trong ứng dụng thực, sẽ cập nhật từ database/SharedPreferences
        // Ví dụ này chỉ tăng giá trị mẫu lên
        int currentStreak = Integer.parseInt(textViewStreakCount.getText().toString());
        int currentLessons = Integer.parseInt(textViewTotalLessons.getText().toString());

        textViewStreakCount.setText(String.valueOf(currentStreak + 1));
        textViewTotalLessons.setText(String.valueOf(currentLessons + 1));
    }
}
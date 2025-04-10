package com.example.apphoctienganh;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class StudyReminderActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button btnSetReminder;
    private Button btnSaveSchedule;
    private RecyclerView recyclerViewSchedules;
    private ReminderAdapter reminderAdapter;
    private ArrayList<ReminderItem> reminderList;
    private ReminderDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_reminder);

        dbHelper = new ReminderDatabaseHelper(this);

        timePicker = findViewById(R.id.timePicker);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        btnSaveSchedule = findViewById(R.id.btnSaveSchedule);
        recyclerViewSchedules = findViewById(R.id.recyclerViewSchedules);

        reminderList = dbHelper.getAllReminders();
        reminderAdapter = new ReminderAdapter(reminderList, this);
        recyclerViewSchedules.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSchedules.setAdapter(reminderAdapter);

        btnSetReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReminder();
            }
        });

        btnSaveSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSchedule();
            }
        });
    }

    private void setReminder() {
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(this, "Ứng dụng không có quyền đặt báo thức chính xác", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("message", "Đã đến giờ học rồi! Hãy mở ứng dụng và học ngay!");

        int requestCode = (int) System.currentTimeMillis() / 1000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        String message = "Đã đặt nhắc nhở lúc " + hour + ":" + (minute < 10 ? "0" + minute : minute);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveSchedule() {
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        String timeString = hour + ":" + (minute < 10 ? "0" + minute : minute);

        ReminderItem newReminder = new ReminderItem(0, timeString, true);
        long newId = dbHelper.addReminder(newReminder);
        newReminder.setId((int) newId);

        reminderList.add(newReminder);
        reminderAdapter.notifyDataSetChanged();

        setReminderForSchedule(hour, minute, (int) newId);

        Toast.makeText(this, "Đã lưu lịch học thường xuyên lúc " + timeString, Toast.LENGTH_SHORT).show();
    }

    private void setReminderForSchedule(int hour, int minute, int reminderId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(this,
                    "Ứng dụng không có quyền đặt báo thức  ",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("message", "Đã đến giờ học theo lịch trình của bạn!");
        intent.putExtra("reminder_id", reminderId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    public void deleteReminder(int reminderId) {
        dbHelper.deleteReminder(reminderId);

        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        for (int i = 0; i < reminderList.size(); i++) {
            if (reminderList.get(i).getId() == reminderId) {
                reminderList.remove(i);
                reminderAdapter.notifyItemRemoved(i);
                break;
            }
        }

        Toast.makeText(this, "Đã xóa nhắc nhở", Toast.LENGTH_SHORT).show();
    }
}

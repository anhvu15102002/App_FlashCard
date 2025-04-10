// BootReceiver.java
package com.example.apphoctienganh;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Khôi phục lại tất cả các báo thức sau khi thiết bị khởi động lại
            ReminderDatabaseHelper dbHelper = new ReminderDatabaseHelper(context);
            ArrayList<ReminderItem> reminderList = dbHelper.getAllReminders();

            for (ReminderItem reminder : reminderList) {
                if (reminder.isActive()) {
                    // Phân tích chuỗi thời gian để lấy giờ và phút
                    String[] timeParts = reminder.getTime().split(":");
                    if (timeParts.length == 2) {
                        try {
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            // Thiết lập lại báo thức
                            setReminderAlarm(context, hour, minute, reminder.getId());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void setReminderAlarm(Context context, int hour, int minute, int reminderId) {
        // Thiết lập thời gian cho nhắc nhở
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Nếu thời gian đã qua trong ngày, đặt cho ngày hôm sau
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Tạo Intent cho BroadcastReceiver
        Intent reminderIntent = new Intent(context, ReminderReceiver.class);
        reminderIntent.putExtra("message", "Đã đến giờ học theo lịch trình của bạn!");
        reminderIntent.putExtra("reminder_id", reminderId);

        // PendingIntent để trigger BroadcastReceiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId, // Sử dụng ID để phân biệt các PendingIntent
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Thiết lập AlarmManager để gửi thông báo lặp lại hàng ngày
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, // Lặp lại mỗi ngày
                    pendingIntent
            );
        }
    }
}
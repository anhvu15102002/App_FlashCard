package com.example.apphoctienganh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ReminderDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_REMINDERS = "reminders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_ACTIVE = "active";

    public ReminderDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_REMINDERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_ACTIVE + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        onCreate(db);
    }

    // Thêm nhắc nhở mới
    public long addReminder(ReminderItem reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, reminder.getTime());
        values.put(COLUMN_ACTIVE, reminder.isActive() ? 1 : 0);

        // Chèn hàng mới và trả về ID chính
        long id = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        return id;
    }

    // Lấy tất cả các nhắc nhở
    public ArrayList<ReminderItem> getAllReminders() {
        ArrayList<ReminderItem> reminderList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_REMINDERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                boolean active = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACTIVE)) == 1;

                ReminderItem reminder = new ReminderItem(id, time, active);
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reminderList;
    }

    // Cập nhật trạng thái của nhắc nhở
    public int updateReminderStatus(int id, boolean active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACTIVE, active ? 1 : 0);

        return db.update(TABLE_REMINDERS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Xóa nhắc nhở
    public void deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
}
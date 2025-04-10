package com.example.apphoctienganh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBasePointUser extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "QuestionEnd2";
    private static final String TABLE_NAME = "PointsOfUser";
    private static final String ID = "Id";
    private static final String USER = "User";
    private static final String POINTS = "Points";
    private static final String TIME = "Time";
    private static final String EXAM_TYPE = "ExamType"; // Thêm cột mới
    private static final int DATABASE_VERSION = 2; // Tăng version để trigger onUpgrade

    // Các loại bài thi
    public static final String EXAM_TYPE_MULTIPLE_CHOICE = "Trắc nghiệm";
    public static final String EXAM_TYPE_GRAMMAR = "Ngữ pháp";
    public static final String EXAM_TYPE_LISTENING = "Luyện nghe";

    public DataBasePointUser(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER + " TEXT, " +
                POINTS + " INTEGER, " +
                TIME + " TEXT, " +
                EXAM_TYPE + " TEXT)"; // Thêm cột EXAM_TYPE
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Thêm cột EXAM_TYPE vào bảng hiện có nếu chưa có
            try {
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + EXAM_TYPE + " TEXT DEFAULT '" +
                        EXAM_TYPE_MULTIPLE_CHOICE + "'");
            } catch (SQLException e) {
                // Cột có thể đã tồn tại
                e.printStackTrace();
            }
        }
    }

    public void addPoints(String user, int points, String time) {
        // Method mặc định - giữ cho khả năng tương thích ngược
        addPoints(user, points, time, EXAM_TYPE_MULTIPLE_CHOICE);
    }

    public void addPoints(String user, int points, String time, String examType) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(USER, user);
            values.put(POINTS, points);
            values.put(TIME, time);
            values.put(EXAM_TYPE, examType);

            int currentPoints = getPoints(user, examType);
            if (currentPoints == 0) {
                // User does not exist in the database, insert a new record
                db.insert(TABLE_NAME, null, values);
            } else if (points > currentPoints) {
                // User exists and the new points are greater, update the record
                db.update(TABLE_NAME, values, USER + "=? AND " + EXAM_TYPE + "=?",
                        new String[]{user, examType});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public List<UserPoint> getAllUserPoints() {
        // Method mặc định - giữ cho khả năng tương thích ngược
        return getUserPointsByExamType(EXAM_TYPE_MULTIPLE_CHOICE);
    }

    public List<UserPoint> getGrammarPoints() {
        return getUserPointsByExamType(EXAM_TYPE_GRAMMAR);
    }

    public List<UserPoint> getListeningPoints() {
        return getUserPointsByExamType(EXAM_TYPE_LISTENING);
    }

    public List<UserPoint> getUserPointsByExamType(String examType) {
        List<UserPoint> userPointsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, EXAM_TYPE + "=?",
                new String[]{examType}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String user = cursor.getString(cursor.getColumnIndexOrThrow(USER));
                    int points = cursor.getInt(cursor.getColumnIndexOrThrow(POINTS));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(TIME));
                    UserPoint userPoint = new UserPoint(user, points, time);
                    userPointsList.add(userPoint);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return userPointsList;
    }

    public void updatePoints(String user, int points, String time, String examType) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(POINTS, points);
            values.put(TIME, time);
            db.update(TABLE_NAME, values, USER + "=? AND " + EXAM_TYPE + "=?",
                    new String[]{user, examType});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deletePoints(String user, String examType) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_NAME, USER + "=? AND " + EXAM_TYPE + "=?",
                    new String[]{user, examType});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public int getPoints(String user) {
        // Method mặc định - giữ cho khả năng tương thích ngược
        return getPoints(user, EXAM_TYPE_MULTIPLE_CHOICE);
    }

    public int getPoints(String user, String examType) {
        int points = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, new String[]{POINTS},
                    USER + "=? AND " + EXAM_TYPE + "=?",
                    new String[]{user, examType},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                points = cursor.getInt(cursor.getColumnIndexOrThrow(POINTS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return points;
    }
}
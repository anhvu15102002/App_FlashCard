package com.example.apphoctienganh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.apphoctienganh.VocabularyItem;

import java.util.ArrayList;
import java.util.List;

public class VocabularyDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "VocabularyDbHelper";

    private static final String DATABASE_NAME = "vocabulary.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và các cột
    public static final String TABLE_VOCABULARY = "vocabulary";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ENGLISH = "english_word";
    public static final String COLUMN_VIETNAMESE = "vietnamese_meaning";
    public static final String COLUMN_IMAGE_URL = "image_url";

    // Câu lệnh SQL để tạo bảng
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_VOCABULARY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ENGLISH + " TEXT NOT NULL, " +
                    COLUMN_VIETNAMESE + " TEXT NOT NULL, " +
                    COLUMN_IMAGE_URL + " TEXT NOT NULL)";

    public VocabularyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_TABLE);
            Log.d(TAG, "Database created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database: " + e.getMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCABULARY);
        // Tạo lại bảng mới
        onCreate(db);
    }

    // Thêm từ vựng mới
    public long addVocabulary(VocabularyItem item) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_ENGLISH, item.getEnglishWord());
            values.put(COLUMN_VIETNAMESE, item.getVietnameseMeaning());
            values.put(COLUMN_IMAGE_URL, item.getImageUrl());

            // Chèn dữ liệu mới và lấy ID
            long id = db.insert(TABLE_VOCABULARY, null, values);
            db.close();

            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error adding vocabulary: " + e.getMessage(), e);
            return -1;
        }
    }

    // Lấy danh sách tất cả từ vựng
    public List<VocabularyItem> getAllVocabulary() {
        List<VocabularyItem> vocabularyList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_VOCABULARY;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int englishIndex = cursor.getColumnIndex(COLUMN_ENGLISH);
                    int vietnameseIndex = cursor.getColumnIndex(COLUMN_VIETNAMESE);
                    int imageUrlIndex = cursor.getColumnIndex(COLUMN_IMAGE_URL);

                    // Kiểm tra chỉ mục có tồn tại không
                    if (englishIndex != -1 && vietnameseIndex != -1 && imageUrlIndex != -1) {
                        VocabularyItem item = new VocabularyItem(
                                cursor.getString(englishIndex),
                                cursor.getString(vietnameseIndex),
                                cursor.getString(imageUrlIndex)
                        );
                        vocabularyList.add(item);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting vocabulary: " + e.getMessage(), e);
        }

        return vocabularyList;
    }

    // Xóa từ vựng
    public void deleteVocabulary(String englishWord) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_VOCABULARY, COLUMN_ENGLISH + " = ?", new String[]{englishWord});
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting vocabulary: " + e.getMessage(), e);
        }
    }

    // Kiểm tra xem từ vựng đã tồn tại chưa
    public boolean isVocabularyExists(String englishWord) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_VOCABULARY,
                    new String[]{COLUMN_ID},
                    COLUMN_ENGLISH + " = ?",
                    new String[]{englishWord},
                    null, null, null);

            boolean exists = cursor.getCount() > 0;
            cursor.close();
            db.close();

            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking vocabulary existence: " + e.getMessage(), e);
            return false;
        }
    }
}
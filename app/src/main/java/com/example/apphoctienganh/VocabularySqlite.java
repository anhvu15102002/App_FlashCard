package com.example.apphoctienganh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class VocabularySqlite extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DB_VB";
    private static final String TABLE_NAME = "Vocabulary";
    private static final String ID = "Id";
    private static final String WORDS = "Words";
    private static final String IMAGE = "Image";
    private static final String ANSWER = "Answer";
    private static final String TOPIC = "Topic";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            WORDS + " TEXT," +
            IMAGE + " TEXT," +
            TOPIC + " TEXT," +
            ANSWER + " TEXT)";

    public VocabularySqlite(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public boolean addVocabulary(Vocabulary vocabulary) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (checkDuplicateVocabulary(vocabulary.getWords())) {
            return false; // Trả về false nếu từ vựng đã tồn tại
        } else {
            ContentValues values = new ContentValues();
            values.put(WORDS, vocabulary.getWords());
            values.put(IMAGE, vocabulary.getImage());
            values.put(ANSWER, vocabulary.getAnswer());
            values.put(TOPIC, vocabulary.getTopic());
            db.insert(TABLE_NAME, null, values);
            return true; // Trả về true nếu thêm từ vựng thành công
        }
    }


    // Kiểm tra từ vựng trùng lặp
    private boolean checkDuplicateVocabulary(String words) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + WORDS + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{words});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<Vocabulary> getVocabulariesByTopic(String topic) {
        List<Vocabulary> vocabularyList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + TOPIC + " = ?";
        Cursor cursor = null;
        try {
            if (topic != null) {
                cursor = db.rawQuery(selectQuery, new String[]{topic});
                if (cursor.moveToFirst()) {
                    do {
                        Vocabulary vocabulary = new Vocabulary();
                        vocabulary.setId(cursor.getInt(0));
                        vocabulary.setWords(cursor.getString(1));
                        vocabulary.setImage(cursor.getString(2));
                        vocabulary.setAnswer(cursor.getString(4));
                        vocabulary.setTopic(cursor.getString(3));
                        vocabularyList.add(vocabulary);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return vocabularyList;
    }




    // Xóa từ vựng
    public void deleteVocabulary(Vocabulary vocabulary) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(vocabulary.getId())});
        db.close();
    }
}

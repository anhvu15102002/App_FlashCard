package com.example.apphoctienganh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataTenseSqlite extends SQLiteOpenHelper {
    private static final String DB_NAME = "dbQuestionFixed";
    private static final String TABLE_NAME = "QuestionVoca";
    private static final String ID = "Id";
    private static final String QUESTION = "Question";
    private static final String ANSWER = "Answer";
    private static final String ALLCHOICE = "Choice";

    public DataTenseSqlite(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                QUESTION + " TEXT," +
                ANSWER + " TEXT," +
                ALLCHOICE + " TEXT)";
        sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the question already exists in the database
        if (!isQuestionExists(question.getQuestion())) {
            ContentValues values = new ContentValues();
            values.put(QUESTION, question.getQuestion());
            values.put(ANSWER, question.getAnswer());
            values.put(ALLCHOICE, question.getAllchoice());

            db.insert(TABLE_NAME, null, values);
        }

        db.close();
    }

    private boolean isQuestionExists(String question) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID}, QUESTION + "=?",
                new String[]{question}, null, null, null, null);

        boolean exists = (cursor.getCount() > 0);

        cursor.close();
        return exists;
    }

    public Question getQuestion(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID, QUESTION, ANSWER, ALLCHOICE},
                ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Question question = new Question(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getString(3));
        cursor.close();
        return question;
    }

    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getInt(0));
                question.setQuestion(cursor.getString(1));
                question.setAnswer(cursor.getString(2));
                question.setAllchoice(cursor.getString(3));

                questionList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }

    public int updateQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QUESTION, question.getQuestion());
        values.put(ANSWER, question.getAnswer());
        values.put(ALLCHOICE, question.getAllchoice());

        return db.update(TABLE_NAME, values, ID + " = ?",
                new String[]{String.valueOf(question.getId())});
    }

    public void deleteQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + " = ?",
                new String[]{String.valueOf(question.getId())});
        db.close();
    }
}

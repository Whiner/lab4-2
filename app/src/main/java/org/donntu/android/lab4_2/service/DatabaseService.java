package org.donntu.android.lab4_2.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import org.donntu.android.lab4_2.dto.Word;

import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private SQLiteOpenHelperImpl sqLiteOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    private String tableName = "words";

    public DatabaseService(Context context) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(context, tableName);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
    }

    public boolean insert(Word word) {
        return sqLiteDatabase.insert(tableName, null, word.toContentValues()) != -1;
    }

    public void delete(int id) {
        sqLiteDatabase.delete(tableName, "id = " + id, null);
    }

    public List<Word> findAll() {
        Cursor query = sqLiteDatabase.query(
                tableName,
                new String[]{"id", "russian", "english", "isInArchive"},
                null,
                null,
                null,
                null,
                null
        );
        List<Word> words = new ArrayList<>();
        if (query.moveToFirst()) {
            do {
                Log.d("customdatabase", "selected id = " + query.getInt(0));
                Word word = new Word();
                word.setId(query.getInt(0));
                word.setRussianTranslate(query.getString(1));
                word.setEnglishTranslate(query.getString(2));
                word.setInArchive(query.getInt(3) != 0);
                words.add(word);
            } while (query.moveToNext());
        }

        query.close();
        return words;
    }

    public void close() {
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
            Log.d("customdatabase", "db closed");
        }
    }


    public int getAvailableWordsCount() {
        Cursor query = sqLiteDatabase.query(
                tableName,
                new String[]{"id", "isInArchive"},
                "isInArchive <> 1",
                null,
                null,
                null,
                null
        );
        int count = query.getCount();
        query.close();
        return count;
    }

    public void sendToArchive(Word word) {
        sqLiteDatabase.update(tableName, word.toContentValues(), "id = " + word.getId(), null);
    }

    public void refreshArchive() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("isInArchive", 0);
        sqLiteDatabase.update(tableName, contentValues, null, null);
    }

    public void insertAllWords(List<Word> words) {
        try {
            sqLiteDatabase.beginTransaction();
            for (Word word : words) {
                sqLiteDatabase.insert(tableName, null, word.toContentValues());
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

    }
}

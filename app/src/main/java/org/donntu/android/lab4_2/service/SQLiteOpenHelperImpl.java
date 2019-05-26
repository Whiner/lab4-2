package org.donntu.android.lab4_2.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteOpenHelperImpl extends SQLiteOpenHelper {
    public SQLiteOpenHelperImpl(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("customdatabase", "--- onCreate database ---");
        sqLiteDatabase.execSQL(
                "create table "
                + this.getDatabaseName() + " ("
                + "id integer primary key autoincrement unique,"
                + "russian text unique,"
                + "english text unique,"
                + "isInArchive integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

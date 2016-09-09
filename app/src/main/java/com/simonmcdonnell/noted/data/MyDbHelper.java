package com.simonmcdonnell.noted.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 2;
    private static final String CREATE_TABLE = "CREATE TABLE " + NotesContract.TABLE_NAME + "(" +
            NotesContract.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NotesContract.COL_TITLE + " TEXT, " +
            NotesContract.COL_BODY + " TEXT, " +
            NotesContract.COL_COLOR + " TEXT DEFAULT \"white\", " +
            NotesContract.COL_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP);";

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotesContract.TABLE_NAME);
        onCreate(db);
    }
}

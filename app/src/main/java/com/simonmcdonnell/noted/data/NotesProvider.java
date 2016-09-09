package com.simonmcdonnell.noted.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class NotesProvider extends ContentProvider {
    private SQLiteDatabase database;
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(NotesContract.AUTHORITY, NotesContract.BASE_PATH, NotesContract.NOTE);
        uriMatcher.addURI(NotesContract.AUTHORITY, NotesContract.BASE_PATH + "/#", NotesContract.NOTE_WITH_ID);
    }

    @Override
    public boolean onCreate() {
        database = new MyDbHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == NotesContract.NOTE_WITH_ID){
            selection = NotesContract.COL_ID + " = " + uri.getLastPathSegment();
        }
        return database.query(NotesContract.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                NotesContract.COL_TIME + " DESC"
                );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(NotesContract.TABLE_NAME, null, values);
        return Uri.parse(NotesContract.CONTENT_URI + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(NotesContract.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(NotesContract.TABLE_NAME, values, selection, selectionArgs);
    }
}

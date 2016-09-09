package com.simonmcdonnell.noted.data;

import android.net.Uri;

public class NotesContract {
    public static final String TABLE_NAME = "notes";
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_BODY = "body";
    public static final String COL_COLOR = "color";
    public static final String COL_TIME = "time";
    public static final String AUTHORITY = "com.simonmcdonnell.noted.notesprovider";
    public static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final int NOTE = 1;
    public static final int NOTE_WITH_ID = 2;
}

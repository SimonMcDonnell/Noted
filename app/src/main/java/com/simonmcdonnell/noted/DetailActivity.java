package com.simonmcdonnell.noted;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.simonmcdonnell.noted.data.NotesContract;
import com.simonmcdonnell.noted.widget.MyAppWidgetProvider;

public class DetailActivity extends AppCompatActivity {
    private LinearLayout layout;
    private EditText titleText, bodyText;
    private String noteFilter, oldTitle, oldBody, newTitle, newBody;
    private String oldBackgroundColor, newBackgroundColor;
    private android.support.v7.app.ActionBar actionBar;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        actionBar = getSupportActionBar();

        //set up view
        layout = (LinearLayout) findViewById(R.id.detail_layout);
        titleText = (EditText) findViewById((R.id.title_text_input));
        bodyText = (EditText) findViewById(R.id.body_text_input);

        //recieve intent
        Intent intent = getIntent();
        uri = intent.getParcelableExtra("URI");
        if (uri == null){
            setTitle("New Note");
            setBackgroundColor("white");
            oldBackgroundColor = "white";
            insertNewNote("", "", oldBackgroundColor);
            noteFilter = NotesContract.COL_ID + " = " + uri.getLastPathSegment();
            oldTitle = "";
            oldBody = "";
            bodyText.requestFocus();
            showSoftKeyboard();
        }else{
            setTitle("Edit Note");
            noteFilter = NotesContract.COL_ID + " = " + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri, null, noteFilter, null, null);
            cursor.moveToFirst();
            oldTitle = cursor.getString(cursor.getColumnIndex(NotesContract.COL_TITLE));
            oldBody = cursor.getString(cursor.getColumnIndex(NotesContract.COL_BODY));
            oldBackgroundColor = cursor.getString(cursor.getColumnIndex(NotesContract.COL_COLOR));
            titleText.setText(oldTitle);
            bodyText.setText(oldBody);
            setBackgroundColor(oldBackgroundColor);
        }
    }

    private void showSoftKeyboard(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.detail_menu_delete_button:
                deleteNote();
                break;
            case R.id.pick_color_button:
                showColorDialog();
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String title = titleText.getText().toString();
        String body = bodyText.getText().toString();
        if (title.equals("") && !body.equals("")){
            updateNote(body, title, newBackgroundColor);
        }else{
            updateNote(title, body, newBackgroundColor);
        }
        updateWidget();
    }

    private void setBackgroundColor(String color){
        newBackgroundColor = color;
        int newColor = Utilities.getColor(color);
        layout.setBackgroundResource(newColor);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Utilities.getActionBarColor(color))));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(Utilities.getNotificationColor(color)));
    }

    private void showColorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Colour").setItems(R.array.color_pick_options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                setBackgroundColor("red");
                                break;
                            case 1:
                                setBackgroundColor("blue");
                                break;
                            case 2:
                                setBackgroundColor("green");
                                break;
                            case 3:
                                setBackgroundColor("yellow");
                                break;
                            case 4:
                                setBackgroundColor("white");
                                break;
                            default:
                                setBackgroundColor("white");
                        }
                    }
                }
        );
        builder.show();
    }

    private void deleteNote(){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE){
                    getContentResolver().delete(uri, noteFilter, null);
                    Toast.makeText(DetailActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                    updateWidget();
                    setResult(RESULT_OK);
                    supportFinishAfterTransition();
                }
            }
        };
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Delete note?");
        alert.setPositiveButton("Delete", listener);
        alert.setNegativeButton("Cancel", listener);
        alert.show();
    }

    private void insertNewNote(String title, String body, String color){
        ContentValues values = new ContentValues();
        values.put(NotesContract.COL_TITLE, title);
        values.put(NotesContract.COL_BODY, body);
        values.put(NotesContract.COL_COLOR, color);
        uri = getContentResolver().insert(NotesContract.CONTENT_URI, values);
    }

    private void updateNote(String title, String body, String color){
        ContentValues values = new ContentValues();
        values.put(NotesContract.COL_TITLE, title);
        values.put(NotesContract.COL_BODY, body);
        values.put(NotesContract.COL_COLOR, color);
        getContentResolver().update(NotesContract.CONTENT_URI, values, noteFilter, null);
    }

    private void updateWidget(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        if (appWidgetManager != null){
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, MyAppWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.app_widget_listview);
        }
    }

    private void finishEditing() {
        newTitle = titleText.getText().toString();
        newBody = bodyText.getText().toString();
        if (newTitle.equals("") && newBody.equals("")){
            getContentResolver().delete(uri, noteFilter, null);
            updateWidget();
            setResult(RESULT_OK);
        }else if (newTitle.equals(oldTitle) && newBody.equals(oldBody) && oldBackgroundColor.equals(newBackgroundColor)){
            setResult(RESULT_CANCELED);
        }else if (newTitle.length() == 0){
            updateNote(newBody, "", newBackgroundColor);
            titleText.setText(newBody);
            bodyText.setText("");
            updateWidget();
            setResult(RESULT_OK);
        }else{
            updateNote(newTitle, newBody, newBackgroundColor);
            updateWidget();
            setResult(RESULT_OK);
        }
        supportFinishAfterTransition();
    }
}

package com.simonmcdonnell.noted;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.simonmcdonnell.noted.data.NotesContract;
import com.simonmcdonnell.noted.widget.MyAppWidgetProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int REQUEST_CODE = 101;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private boolean isLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize loader
        getLoaderManager().initLoader(0, null, this);

        //set up of recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(getLayoutManager());
        adapter = new RecyclerViewAdapter(new RecyclerViewAdapter.RecyclerViewAdapterOnClickHandler() {
            @Override
            public void onClick(int id, View view) {
                editNote(id, view);
            }
        }, getResources());
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewNote(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_all_notes){
            deleteAllNotes();
            return true;
        }else if (id == R.id.change_layout_manager){
            changeLayoutManager();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNewNote(View view){
        Intent intent = new Intent(this, DetailActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void editNote(int id, View view) {
        Intent intent = new Intent(this, DetailActivity.class);
        Uri uri = Uri.parse(NotesContract.CONTENT_URI + "/" + id);
        intent.putExtra("URI", uri);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    Pair.create(view.findViewById(R.id.cardview), "background")
                );
        startActivityForResult(intent, REQUEST_CODE, options.toBundle());
    }

    private void deleteAllNotes(){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE){
                    getContentResolver().delete(NotesContract.CONTENT_URI, null, null);
                    Toast.makeText(MainActivity.this, "All notes deleted", Toast.LENGTH_SHORT).show();
                    updateWidget();
                    restartLoader();
                }
            }
        };
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Delete all notes?");
        dialog.setPositiveButton("Delete all notes", listener);
        dialog.setNegativeButton("Cancel", listener);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(0, null, this);
    }

    private void updateWidget(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        if (appWidgetManager != null){
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, MyAppWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.app_widget_listview);
        }
    }

    private RecyclerView.LayoutManager getLayoutManager(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLinearLayoutManager = preferences.getBoolean("layoutManager", true);
        if (isLinearLayoutManager == true){
            isLinear = true;
            return new LinearLayoutManager(this);
        }else{
            isLinear = false;
            return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        }
    }

    private void changeLayoutManager(){
        if (isLinear == true){
            boolean newValue = false;
            isLinear = newValue;
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            writeToSharedPreferences(newValue);
        }else{
            boolean newValue = true;
            isLinear = newValue;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            writeToSharedPreferences(newValue);
        }
    }

    private void writeToSharedPreferences(boolean newValue){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("layoutManager", newValue);
        editor.apply();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NotesContract.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}

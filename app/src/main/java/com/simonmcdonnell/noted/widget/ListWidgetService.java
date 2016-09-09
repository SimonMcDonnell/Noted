package com.simonmcdonnell.noted.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.simonmcdonnell.noted.data.NotesContract;
import com.simonmcdonnell.noted.R;
import com.simonmcdonnell.noted.Utilities;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor cursor = null;

            @Override
            public void onCreate() {
                //nothing to do here
            }

            @Override
            public void onDataSetChanged() {
                if (cursor != null){
                    cursor.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                cursor = getContentResolver().query(NotesContract.CONTENT_URI, null, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (cursor != null){
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)){
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.app_widget_item_layout);
                String title = cursor.getString(cursor.getColumnIndex(NotesContract.COL_TITLE));
                String body = cursor.getString(cursor.getColumnIndex(NotesContract.COL_BODY));
                views.setTextViewText(R.id.app_widget_list_item_title, title);
                views.setTextViewText(R.id.app_widget_list_item_body, body);
                views.setInt(R.id.app_widget_list_item_layout, "setBackgroundResource", Utilities.getColor(cursor.getString(cursor.getColumnIndex(NotesContract.COL_COLOR))));
                final Intent fillInIntent = new Intent();
                long id = getItemId(position);
                Uri uri = Uri.parse(NotesContract.CONTENT_URI + "/" + (int) id);
                fillInIntent.putExtra("URI", uri);
                views.setOnClickFillInIntent(R.id.app_widget_list_item_layout, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.app_widget_item_layout);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (cursor.moveToPosition(position)){
                    return cursor.getInt(cursor.getColumnIndex(NotesContract.COL_ID));
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

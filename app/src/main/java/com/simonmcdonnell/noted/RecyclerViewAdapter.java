package com.simonmcdonnell.noted;

import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simonmcdonnell.noted.data.NotesContract;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private Cursor mCursor;
    private final RecyclerViewAdapterOnClickHandler mClickHandler;
    private final Resources resources;

    public RecyclerViewAdapter(RecyclerViewAdapterOnClickHandler mClickHandler, Resources resources){
        this.mClickHandler = mClickHandler;
        this.resources = resources;
    }

    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        view.setFocusable(true);
        return new RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String title = mCursor.getString(mCursor.getColumnIndex(NotesContract.COL_TITLE));
        String body = mCursor.getString(mCursor.getColumnIndex(NotesContract.COL_BODY));
        holder.view.setCardBackgroundColor(resources.getColor(Utilities.getColor(mCursor.getString(mCursor.getColumnIndex(NotesContract.COL_COLOR)))));
        holder.titleText.setText(title);
        if (body.length() > 300){
            body = body.substring(0, 300) + ".....";
        }
        holder.bodyText.setText(body);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null){
            return 0;
        }else{
            return mCursor.getCount();
        }
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView titleText, bodyText;
        public final CardView view;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = (CardView) itemView.findViewById(R.id.cardview);
            titleText = (TextView) itemView.findViewById(R.id.list_item_title);
            bodyText = (TextView) itemView.findViewById(R.id.list_item_body);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int id = mCursor.getInt(mCursor.getColumnIndex(NotesContract.COL_ID));
            mClickHandler.onClick(id, v);
        }
    }

    public static interface RecyclerViewAdapterOnClickHandler{
        void onClick(int id, View view);
    }
}

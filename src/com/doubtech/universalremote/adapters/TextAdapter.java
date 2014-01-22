package com.doubtech.universalremote.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.doubtech.universalremote.R;
import com.doubtech.universalremote.providers.AbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.providerdo.Parent;

public class TextAdapter extends CursorAdapter {
    public static interface RequestChildAdapterListener {
        Object onRequestChild(Adapter parent, Parent node);
    }

    private int mIdColIndex = -1;
    private int mLabelColIndex = -1;
    private int mAuthorityIndex = -1;

    private String mIdColumn;
    private String mLabelColumn;
    private String mAuthorityColumn;

    private RequestChildAdapterListener mRequestChildListener;
    private int mTable;
    private Adapter mParentAdapter;
    private String mId;

    private static class EmptyCursor extends MatrixCursor {

        public EmptyCursor(String idColumn, String labelColumn) {
            super(new String[] { "_id", idColumn, labelColumn });
            addRow(new Object[] { 0, "0", "Loading..."});
        }
    }

    public static interface SimpleCursorLoader {
        Cursor loadCursor();
    }

    AsyncTask<SimpleCursorLoader, Void, Cursor> mLoader = new AsyncTask<SimpleCursorLoader, Void, Cursor>() {
        @Override
        protected Cursor doInBackground(SimpleCursorLoader... params) {
            return params[0].loadCursor();
        }

        protected void onPostExecute(Cursor result) {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD){
                swapCursor(result);
            } else {
                changeCursor(result);
            }
            notifyDataSetChanged();
        };
    };
    private Parent mParent;

    public TextAdapter(Context context, Parent parent, Cursor cursor, RequestChildAdapterListener listener) {
        super(context, cursor, true);
        mParent = parent;
        mRequestChildListener = listener;
    }

    public TextAdapter(final Context context, final Parent parent, RequestChildAdapterListener listener) {
        this(context, parent, (Cursor) null, listener);

        mLoader.execute(new SimpleCursorLoader() {

                    @Override
                    public Cursor loadCursor() {
                        return AbstractUniversalRemoteProvider.query(
                                context,
                                parent);
                    }
                });
    }

    public TextAdapter(Context context, SimpleCursorLoader cursorLoader, RequestChildAdapterListener listener) {
        this(context, null, (Cursor) null, listener);

        mLoader.execute(cursorLoader);
    }

    public Parent getParentObject() {
        return mParent;
    }

    public TextAdapter setParentAdapter(Adapter adapter) {
        mParentAdapter = adapter;
        return this;
    }

    public Adapter getParentAdapter() {
        return mParentAdapter;
    }

    @Override
    public Object getItem(int position) {
        Cursor cursor = getCursor();

        cursor.moveToPosition(position);
        return mRequestChildListener.onRequestChild(this, Parent.fromCursor(cursor));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.text_view, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view;
        Parent parent = Parent.fromCursor(cursor);
        tv.setText(parent.getName());
    }

    @Override
    public void changeCursor(Cursor cursor) {
        mLabelColIndex = -1;
        mIdColIndex = -1;
        super.changeCursor(cursor);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        mLabelColIndex = -1;
        mIdColIndex = -1;
        return super.swapCursor(newCursor);
    }

    public CharSequence getText(int position) {
        Cursor cursor = getCursor();
        Parent parent = Parent.fromCursor(cursor);
        return parent.getName();
    }

    public Parent getTarget(int position) {
        getCursor().moveToPosition(position);
        return Parent.fromCursor(getCursor());
    }
}

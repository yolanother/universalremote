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

public class TextAdapter extends CursorAdapter {
    public static interface RequestChildAdapterListener {
        Object onRequestChild(Adapter parent, String authority, int parentTable, String id);
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

    public TextAdapter(Context context, String id, int table, Cursor cursor, String authorityColumn, String idColumn, String labelColumn, RequestChildAdapterListener listener) {
        super(context, cursor, true);
        mId = id;
        mTable = table;
        mLabelColumn = labelColumn;
        mIdColumn = idColumn;
        mRequestChildListener = listener;
        mAuthorityColumn = authorityColumn;
    }

    public TextAdapter(Context context, String id, int table, SimpleCursorLoader cursorLoader, String authorityColumn, String idColumn, String labelColumn, RequestChildAdapterListener listener) {
        this(context, id, table, (Cursor) null, authorityColumn, idColumn, labelColumn, listener);

        mLoader.execute(cursorLoader);
    }

    public String getAdapterId() {
        return mId;
    }

    public TextAdapter setParentAdapter(Adapter adapter) {
        mParentAdapter = adapter;
        return this;
    }

    public Adapter getParentAdapter() {
        return mParentAdapter;
    }

    public int getChildTable() {
        return mTable;
    }

    public String getActualId(int position) {
        getCursor().moveToPosition(position);
        return getCursor().getString(mIdColIndex);
    }

    public String getTargetAuthority(int position) {
        initializeColumns(getCursor());
        getCursor().moveToPosition(position);
        return getCursor().getString(mAuthorityIndex);
    }

    @Override
    public Object getItem(int position) {
        Cursor cursor = getCursor();
        initializeColumns(cursor);

        cursor.moveToPosition(position);

        String authority = cursor.getString(mAuthorityIndex);
        String id = cursor.getString(mIdColIndex);

        return mRequestChildListener.onRequestChild(this, authority, mTable, id);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.text_view, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view;
        if (null != mLabelColumn) {
            initializeColumns(cursor);
            tv.setText(cursor.getString(mLabelColIndex));
        } else {
            tv.setText("UNDEFINED LABEL COLUMN");
        }
    }

    private void initializeColumns(Cursor cursor) {
        if (-1 == mLabelColIndex) {
            mLabelColIndex = cursor.getColumnIndex(mLabelColumn);
        }
        if (-1 == mIdColIndex) {
            mIdColIndex  = getCursor().getColumnIndex(mIdColumn);
        }
        if (-1 == mAuthorityIndex) {
            mAuthorityIndex = getCursor().getColumnIndex(mAuthorityColumn);
        }
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
		cursor.moveToPosition(position);
        initializeColumns(cursor);
        return cursor.getString(mLabelColIndex);
	}
}

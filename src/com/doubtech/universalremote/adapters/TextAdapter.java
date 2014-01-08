package com.doubtech.universalremote.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doubtech.universalremote.R;

public class TextAdapter extends CursorAdapter {
	public static interface RequestChildAdapterListener {
		Object onRequestChild(String authority, int parentTable, String id);
	}

	private int mIdColIndex = -1;
	private int mLabelColIndex = -1;
	private int mAuthorityIndex = -1;

	private String mIdColumn;
	private String mLabelColumn;
	private String mAuthorityColumn;
	
	private RequestChildAdapterListener mRequestChildListener;
	private int mTable;

	private static class EmptyCursor extends MatrixCursor {

		public EmptyCursor(String idColumn, String labelColumn) {
			super(new String[] { idColumn, labelColumn });
		}
	}
	
	public TextAdapter(Context context, int table, Cursor c, String authorityColumn, String idColumn, String labelColumn, RequestChildAdapterListener listener) {
		super(context, null == c ? new EmptyCursor(idColumn, labelColumn) : c, true);
		mTable = table;
		mLabelColumn = labelColumn;
		mIdColumn = idColumn;
		mRequestChildListener = listener;
		mAuthorityColumn = authorityColumn;
	}
	
	public int getChildTable() {
		return mTable;
	}
	
	@Override
	public Object getItem(int position) {
		if(-1 == mIdColIndex) {
			mIdColIndex  = getCursor().getColumnIndex(mIdColumn);
		}
		if(-1 == mAuthorityIndex) {
			mAuthorityIndex = getCursor().getColumnIndex(mAuthorityColumn);
		}
		
		String authority = getCursor().getString(mAuthorityIndex);
		String id = getCursor().getString(mIdColIndex);

		return mRequestChildListener.onRequestChild(authority, mTable, id);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.text_view, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView tv = (TextView) view;
		if(null != mLabelColumn) {
			if(-1 == mLabelColIndex) {
				mLabelColIndex = cursor.getColumnIndex(mLabelColumn);
			}
			tv.setText(cursor.getString(mLabelColIndex));
		} else {
			tv.setText("UNDEFINED LABEL COLUMN");
		}
	}
}

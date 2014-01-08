package com.doubtech.universalremote.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.doubtech.universalremote.R;

public class IconTextAdapter extends TextAdapter {

	private String mPackageColumn;
	private String mResourceColumn;
	private int mIconColIndex = -1;

	public IconTextAdapter(Context context, int table, Cursor c,
			String authorityColumn, String idColumn, String labelColumn,
			RequestChildAdapterListener listener) {
		super(context, table, c, authorityColumn, idColumn, labelColumn,
				listener);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.icon_text_view, null);
		view.setTag(new ViewHolder(view));
		return view;
	}

	private class ViewHolder {

		private View mTextView;
		private ImageView mIconView;

		public ViewHolder(View view) {
			mTextView = view.findViewById(R.id.label);
			mIconView = (ImageView) view.findViewById(R.id.icon);
		}
		
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		super.bindView(holder.mTextView, context, cursor);
		if(-1 == mIconColIndex) {
			mIconColIndex = cursor.getColumnIndex(mResourceColumn);
		}
		int res = cursor.getInt(mIconColIndex);
		if(0 != res) {
			holder.mIconView.setVisibility(View.VISIBLE);
			holder.mIconView.setImageResource(res);
		} else {
			holder.mIconView.setVisibility(View.INVISIBLE);
		}
	}
}

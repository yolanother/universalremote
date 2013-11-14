package com.doubtech.universalremote;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doubtech.universalremote.widget.DynamicListView.ISwappableAdapter;
import com.doubtech.universalremote.widget.HierarchicalListView;
import com.doubtech.universalremote.widget.TwoWayListView;

public class RemotePageConfiguration extends Activity {
	private class RemotePageAdapter extends BaseAdapter implements ISwappableAdapter {
		private class ViewHolder {

			private TextView mLabel;

			public ViewHolder(View convertView) {
				mLabel = (TextView) convertView.findViewById(R.id.label);
			}
			
		}
		
		private ArrayList<String> mRemotes;
		private HashMap<String, Integer> mIdMap;

		public RemotePageAdapter() {
			mRemotes = new ArrayList<String>();
			mIdMap = new HashMap<String, Integer>();

			mRemotes.add("Samsung SmartTV");
			mRemotes.add("Pioneer VSX-1020");
			mRemotes.add("GoogleTV");
			mRemotes.add("XBMC");
			mRemotes.add("Custom");
			
			int id = 0;
			for(String remote : mRemotes) {
				mIdMap.put(remote, id++);
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if(null == convertView) {
				convertView = LayoutInflater.from(RemotePageConfiguration.this).inflate(R.layout.remote, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.mLabel.setText(mRemotes.get(position));
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			Object item = getItem(position);
			if(null == item) {
				return -1;
			}
			return mIdMap.get(item);
		}

		@Override
		public Object getItem(int position) {
			if(position < 0 || position >= mRemotes.size()) {
				return null;
			}
			return mRemotes.get(position);
		}

		@Override
		public int getCount() {
			return mRemotes.size();
		}

		@Override
		public void swap(int a, int b) {
			String tmp = mRemotes.get(a);
			mRemotes.set(a, mRemotes.get(b));
			mRemotes.set(b, tmp);
		}
	};

	private TwoWayListView mList;
	private HierarchicalListView mRemotes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_page_configuration);
		mList = (TwoWayListView) findViewById(R.id.remotes);
		mList.setAdapter(new RemotePageAdapter());
		
		mRemotes = (HierarchicalListView) findViewById(R.id.remote_sources);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.remote_page_configuration, menu);
		return true;
	}
}

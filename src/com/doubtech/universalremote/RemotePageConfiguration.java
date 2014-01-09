package com.doubtech.universalremote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.doubtech.universalremote.RemotePage.RemotePageBuilder;
import com.doubtech.universalremote.adapters.TextAdapter;
import com.doubtech.universalremote.adapters.TextAdapter.RequestChildAdapterListener;
import com.doubtech.universalremote.io.RemoteConfigurationWriter;
import com.doubtech.universalremote.providers.AbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.irremotes.IrRemoteProvider;
import com.doubtech.universalremote.utils.Constants;
import com.doubtech.universalremote.utils.IOUtil;
import com.doubtech.universalremote.widget.DynamicListView.ISwappableAdapter;
import com.doubtech.universalremote.widget.HierarchicalListView;
import com.doubtech.universalremote.widget.HierarchicalListView.OnItemClickListener;
import com.doubtech.universalremote.widget.TwoWayListView;

public class RemotePageConfiguration extends Activity {
	private static final String TAG = "UniversalRemote :: RemotePageConfiguration";

    private class RemotePageAdapter extends BaseAdapter implements ISwappableAdapter {
        private class ViewHolder {

            private TextView mLabel;
            private ImageView mScreenshot;
            private FrameLayout mContainer;

            public ViewHolder(View convertView) {
                mLabel = (TextView) convertView.findViewById(R.id.label);
                mScreenshot = (ImageView) convertView.findViewById(R.id.screenshot);
                mContainer = (FrameLayout) convertView.findViewById(R.id.container);
            }
        }

        private ArrayList<RemotePage> mRemotes = new ArrayList<RemotePage>();
        private HashMap<RemotePage, Integer> mIdMap = new HashMap<RemotePage, Integer>();

        public RemotePageAdapter() {
            int id = 0;
            for (RemotePage remote : mRemotes) {
                mIdMap.put(remote, id++);
            }
        }

        public void add(RemotePage page) {
        	mRemotes.add(page);
        	mIdMap.put(page, mIdMap.size());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = LayoutInflater.from(RemotePageConfiguration.this).inflate(R.layout.remote, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        	viewHolder.mContainer.removeAllViews();
            if(null == mRemotes.get(position)) {
            	viewHolder.mLabel.setText("Add page");
            	viewHolder.mScreenshot.setImageResource(R.drawable.ic_action_add_custom);
            } else {
            	RemotePage v = mRemotes.get(position);
            	viewHolder.mLabel.setText(v.getTitle());
            	viewHolder.mScreenshot.setImageDrawable(null);
            	
            	ViewGroup p = (ViewGroup) v.getParent();
            	if(null != p) {
            		p.removeView(v);
            	}

            	viewHolder.mContainer.addView(v);
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            Object item = getItem(position);
            if (null == item) {
                return -1;
            }
            Integer id = mIdMap.get(item);
            return null != id ? id : -1;
        }

        @Override
        public Object getItem(int position) {
            if (position < 0 || position >= mRemotes.size()) {
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
        	RemotePage tmp = mRemotes.get(a);
            mRemotes.set(a, mRemotes.get(b));
            mRemotes.set(b, tmp);
        }
    }

	private static final int LOADER_BRAND = 0;
	private static final int LOADER_MODEL = 1;

    private TwoWayListView mList;
    private HierarchicalListView mRemotes;

	private TextAdapter mBrandsAdapter;
	private TextAdapter mModelsAdapter;

	private LoaderManager loaderManager;
	private LoaderHandler mLoaderHandler = new LoaderHandler();
	
	private class ButtonInfo {
		private String authority;
		private String id;

		public ButtonInfo(String authority, String id) {
			this.authority = authority;
			this.id = id;
		}

		public void click() {
			AbstractUniversalRemoteProvider.sendButton(
					(Context) RemotePageConfiguration.this,
					authority,
					id);
		}
	}
	
	private RequestChildAdapterListener mRequestChildListener = new RequestChildAdapterListener() {
		
		public Object onRequestChild(String authority, int parentTable, String id) {
			mMenuItemAddRemote.setEnabled(false);
			switch(parentTable) {
			case URPContract.TABLE_BRANDS:
				return new TextAdapter(
						RemotePageConfiguration.this,
						URPContract.TABLE_MODELS,
						AbstractUniversalRemoteProvider.queryModels(
								(Context) RemotePageConfiguration.this,
								authority,
								id),
						URPContract.COLUMN_AUTHORITY,
						URPContract.Brands.COLUMN_ID,
						URPContract.Brands.COLUMN_NAME,
						mRequestChildListener);
			case URPContract.TABLE_MODELS:
				return new TextAdapter(
						RemotePageConfiguration.this,
						URPContract.TABLE_BUTTONS,
						AbstractUniversalRemoteProvider.queryButtons(
								RemotePageConfiguration.this,
								authority,
								id),
						URPContract.COLUMN_AUTHORITY,
						URPContract.Buttons.COLUMN_ID,
						URPContract.Buttons.COLUMN_NAME,
						new RequestChildAdapterListener() {
							
							@Override
							public Object onRequestChild(String authority, int parentTable, String id) {
								return new ButtonInfo(authority, id);
							}
						});
				/*return new ButtonLayout.Builder(RemotePageConfiguration.this)
						.addButtons(getContentResolver().query(
								URPContract.getButtonsUri(authority).buildUpon()
									.appendQueryParameter(URPContract.QUERY_PARAMETER_PARENT, id)
									.build(),
								null,
								null,
								null,
								null))
						.build();*/
			}
			return null;
		};
	};
	private RemotePageAdapter mRemotePageAdapter;
	private MenuItem mMenuItemAddRemote;
	private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_page_configuration);
        mList = (TwoWayListView) findViewById(R.id.remotes);
        mRemotePageAdapter = new RemotePageAdapter();
        mList.setAdapter(mRemotePageAdapter);

        mRemotes = (HierarchicalListView) findViewById(R.id.remote_sources);
        mRemotes.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public boolean onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Adapter adapter = ((ListView) adapterView).getAdapter();
				Object item = adapter.getItem(position);
				if(item instanceof ButtonInfo) {
					((ButtonInfo)item).click();
					return true;
				}
				return false;
			}
		});
        mRemotes.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Adapter adapter = ((ListView) adapterView).getAdapter();
				Object item = adapter.getItem(position);
				if(item instanceof TextAdapter) {
					TextAdapter ta = (TextAdapter) item;
					if(ta.getChildTable() == URPContract.TABLE_BUTTONS) {
						RemotePageBuilder builder = new RemotePageBuilder(RemotePageConfiguration.this);
						builder.setTitle(((TextView) view).getText());
						mRemotePageAdapter.add(builder.build(ta.getCursor()));
						mRemotePageAdapter.notifyDataSetChanged();
						return true;
					}
				}
				return false;
			}
		});

        loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_BRAND, null, mLoaderHandler);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.remote_page_configuration, menu);
        mMenuItemAddRemote = menu.findItem(R.id.action_remote);
        mMenuItemAddRemote.setEnabled(false);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.action_save:
    		FileOutputStream fos = null;
    		try {
    			mFile = Constants.REMOTE_FILE;
    			mFile.getParentFile().mkdirs();
    			fos = new FileOutputStream(mFile);
    			RemoteConfigurationWriter writer = new RemoteConfigurationWriter(fos, "Default Configuration");
    			for(RemotePage page : mRemotePageAdapter.mRemotes) {
    				writer.addPage(page);
    			}
    			writer.close();
    		} catch (IOException e) {
    			Log.d(TAG, e.getMessage(), e);
    		} finally {
    			IOUtil.closeQuietly(fos);
    		}
    		setResult(RESULT_OK);
    		finish();
    		return true;
    	case R.id.action_remote:
    		
    		return true;
    	}
    	return super.onMenuItemSelected(featureId, item);
    }
    
    private class LoaderHandler implements LoaderCallbacks<Cursor> {
    	@Override
    	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    		Uri uri;
    		Log.d("AARON", "Cursor Loader: " + id);
    		switch(id) {
    		case LOADER_BRAND:
    			uri = URPContract.getBrandsUri(IrRemoteProvider.AUTHORITY);
    			break;
    		case LOADER_MODEL:
    			uri = Uri.parse(args.getString("uri"));
    			break;
    		default:
    			return null;
    		}

    		Log.d("AARON", "" + uri);
    		return new CursorLoader(
    				RemotePageConfiguration.this,
    				uri,
    				null,
    				null,
    				null,
    				null);
    	}

    	@Override
    	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    		switch(loader.getId()) {
    		case LOADER_BRAND:
    			if(null == mBrandsAdapter) {
    				initializeBrandAdapter(cursor);
    			} else {
    				mBrandsAdapter.changeCursor(cursor);
    			}
    			break;
    			
    		case LOADER_MODEL:
    			mModelsAdapter.changeCursor(cursor);
    		}
    	}

    	@Override
    	public void onLoaderReset(Loader<Cursor> loader) {
    		switch(loader.getId()) {
    		case LOADER_BRAND:
    			mBrandsAdapter.changeCursor(null);
    			break;
    		case LOADER_MODEL:
    			mModelsAdapter.changeCursor(null);
    			break;
    		}
    	}	
    }

	private void initializeBrandAdapter(Cursor cursor) {
		mBrandsAdapter = new TextAdapter(
				this,
				URPContract.TABLE_BRANDS,
				cursor,
				URPContract.COLUMN_AUTHORITY,
				URPContract.Brands.COLUMN_ID,
				URPContract.Brands.COLUMN_NAME,
				mRequestChildListener);
		mRemotes.addAdapter(mBrandsAdapter);
	}
}

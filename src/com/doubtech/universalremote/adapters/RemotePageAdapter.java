package com.doubtech.universalremote.adapters;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.doubtech.geofenceeditor.SimpleGeofence;
import com.doubtech.universalremote.R;
import com.doubtech.universalremote.RemotesLoadedListener;
import com.doubtech.universalremote.io.RemoteConfigurationFactory;
import com.doubtech.universalremote.io.RemoteConfigurationReader;
import com.doubtech.universalremote.utils.Utils;
import com.doubtech.universalremote.widget.RemotePage;

public class RemotePageAdapter extends ListPageAdapter<RemotePage> {

    private Context mContext;
    private Uri mFile;

    public RemotePageAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void initView(View v, RemotePage item, int position) {
        FrameLayout parent = ((FrameLayout)v);
        parent.removeAllViews();
        ViewGroup oldParent = (ViewGroup) item.getParent();
        if (null != oldParent) {
            oldParent.removeView(item);
        }
        ScrollView sv = new ScrollView(mContext);
        sv.addView(item);
        parent.addView(sv);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position < getCount()) {
            return get(position).getTitle();
        }
        return "";
    }

    public RemotePageAdapter open(final Uri uri) {
        if (null != uri) {

            mFile = uri;
            RemoteConfigurationReader reader = RemoteConfigurationFactory.getInstance(mContext, uri);
            if (null != reader) {
                reader.open(uri, true, new RemotesLoadedListener() {
                    @Override
                    public void onRemotesLoaded(Uri uri, String name,
                            List<RemotePage> pages, SimpleGeofence geofence) {
                        for (RemotePage page : pages) {
                            add(page);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onRemoteLoadFailed(final Throwable error) {
                        Utils.runOnMainThread(new Runnable() {

                            @Override
                            public void run() {
                                Log.e("UniversalRemote", "Configuration parse error.", error);
                                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            } else {
                Log.e("UniversalRemote", "Unknown file extension for file: " + uri);
                Toast.makeText(mContext, R.string.unknown_file_extension, Toast.LENGTH_LONG).show();
            }
        }
        return this;
    }
}

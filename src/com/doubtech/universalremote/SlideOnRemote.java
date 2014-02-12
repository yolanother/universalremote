package com.doubtech.universalremote;

import wei.mark.standout.SlideOnWindow;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.doubtech.universalremote.adapters.ListPageAdapter;
import com.doubtech.universalremote.adapters.RemotePageAdapter;
import com.doubtech.universalremote.adapters.RemoteRoomAdapter;
import com.doubtech.universalremote.adapters.RemoteRoomAdapter.RemoteFile;
import com.doubtech.universalremote.io.RemoteFilesLoader;
import com.doubtech.universalremote.utils.Constants;
import com.doubtech.universalremote.widget.RemotePage;

public class SlideOnRemote extends SlideOnWindow {
    ViewPager mViewPager;
    private Uri mFile;
    private ListPageAdapter<RemotePage> mPageAdapter;
    private RemoteRoomAdapter mFiles;
    private OnItemSelectedListener mNavigationListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                long arg3) {
            RemoteFile file = (RemoteFile) mFiles.getItem(position);

            mViewPager.setAdapter(new RemotePageAdapter(SlideOnRemote.this).open(file.getFile()));
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };


    public SlideOnRemote() {
        super(SlideOnRemote.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();



    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup root) {
        View v = inflater.inflate(R.layout.activity_remotes_slideon, root, true);
        v.setBackgroundColor(Color.argb(180, 0, 0, 0));
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mPageAdapter = new ListPageAdapter<RemotePage>(this) {

            @Override
            public void initView(View v, RemotePage item, int position) {
                FrameLayout parent = ((FrameLayout)v);
                parent.removeAllViews();
                ViewGroup oldParent = (ViewGroup) item.getParent();
                if (null != oldParent) {
                    oldParent.removeView(item);
                }
                parent.addView(item);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position < getCount()) {
                    return get(position).getTitle();
                }
                return "";
            }
        };

        mFiles = new RemoteRoomAdapter(this, Constants.REMOTES_DIR);
        Spinner spinner = (Spinner) v.findViewById(R.id.room_spinner);
        spinner.setAdapter(mFiles);
        spinner.setOnItemSelectedListener(mNavigationListener);
        return v;
    }

    @Override
    public String getAppName() {
        return getString(R.string.app_name);
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        if (null != mPageAdapter && mPageAdapter.getCount() > 0) {
            return "" + mPageAdapter.get(mViewPager.getCurrentItem()).getTitle();
        }
        return super.getPersistentNotificationMessage(id);
    }

    @Override
    public int getWidthParam() {
        return LayoutParams.MATCH_PARENT;
    }

    @Override
    public int getAppIcon() {
        return R.drawable.ic_launcher;
    }
}

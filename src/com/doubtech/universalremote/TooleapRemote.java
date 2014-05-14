package com.doubtech.universalremote;

import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.doubtech.universalremote.adapters.ListPageAdapter;
import com.doubtech.universalremote.adapters.RemotePageAdapter;
import com.doubtech.universalremote.adapters.RemoteRoomAdapter;
import com.doubtech.universalremote.adapters.RemoteRoomAdapter.RemoteFile;
import com.doubtech.universalremote.utils.Constants;
import com.doubtech.universalremote.widget.RemotePage;
import com.tooleap.sdk.TooleapActivities;

public class TooleapRemote extends TooleapActivities.Activity {
    ViewPager mViewPager;
    private Uri mFile;
    private ListPageAdapter<RemotePage> mPageAdapter;
    private RemoteRoomAdapter mFiles;
    private OnItemSelectedListener mNavigationListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                long arg3) {
            RemoteFile file = (RemoteFile) mFiles.getItem(position);

            mViewPager.setAdapter(new RemotePageAdapter(TooleapRemote.this).open(file.getFile()));
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

    protected void onCreate(android.os.Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_remotes_slideon);

        mViewPager = (ViewPager) findViewById(R.id.pager);
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
        Spinner spinner = (Spinner) findViewById(R.id.room_spinner);
        spinner.setAdapter(mFiles);
        spinner.setOnItemSelectedListener(mNavigationListener);
    }
}

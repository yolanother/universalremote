package com.doubtech.universalremote;

import wei.mark.standout.StandOutWindow;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.doubtech.universalremote.adapters.ListPageAdapter;
import com.doubtech.universalremote.adapters.RemotePageAdapter;
import com.doubtech.universalremote.adapters.RemoteRoomAdapter;
import com.doubtech.universalremote.adapters.RemoteRoomAdapter.RemoteFile;
import com.doubtech.universalremote.utils.Constants;
import com.doubtech.universalremote.utils.Utils;
import com.doubtech.universalremote.widget.RemotePage;

public class Remotes extends FragmentActivity {

    private static final int REQUEST_CONFIGURE = 0;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    ListPageAdapter<RemotePage> mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private OnNavigationListener mOnNavigationListener = new OnNavigationListener() {

        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            RemoteFile file = (RemoteFile) mFiles.getItem(itemPosition);
            mViewPager.setAdapter(new RemotePageAdapter(Remotes.this).open(file.getFile()));
            return true;
        }
    };

    private RemoteRoomAdapter mFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remotes);

        if (Utils.isXLargeScreen(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setBackgroundResource(R.drawable.background);

        Constants.REMOTE_FILE.getParentFile().mkdirs();

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFiles = new RemoteRoomAdapter(this, Constants.REMOTES_DIR);

        final ActionBar actionBar = getActionBar();
        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                mFiles,
                mOnNavigationListener);

        mFiles.enableLocationServices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFiles.disableLocationServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.remotes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.action_settings:
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_CONFIGURE);
            return true;
        case R.id.action_slideon:
            StandOutWindow.closeAll(this, SlideOnRemote.class);
            StandOutWindow.show(this, SlideOnRemote.class,
                    StandOutWindow.DEFAULT_ID);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}

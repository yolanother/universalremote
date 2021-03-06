package com.doubtech.universalremote;

import java.io.File;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.tooleap.sdk.Tooleap;
import com.tooleap.sdk.TooleapPersistentMiniApp;

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

    private SharedPreferences mPrefs;

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

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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
        // TEMPORARY CODE -->
        case R.id.action_accessory_settings: {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(Constants.EXTRA_ACCESSORY_AUTHORITY, "com.doubtech.gear.universalremote");
            intent.putExtra(Constants.EXTRA_COLUMN_COUNT, 4);
            intent.putExtra(Constants.EXTRA_REMOTES_DIR, new File(Constants.REMOTES_DIR, "Gear").getAbsolutePath());
            intent.putExtra(Constants.EXTRA_ACCESSORY_NAME, "Gear Remotes");
            startActivity(intent);
            break;
        }
        // <-- END TEMORARY CODE
        case R.id.action_settings:
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_CONFIGURE);
            return true;
        case R.id.action_slideon:
            Intent i = new Intent(this, TooleapRemote.class);
            TooleapPersistentMiniApp app = new TooleapPersistentMiniApp(this, i);
            app.setIcon(this, R.drawable.shadow_icon);
            app.bubbleBackgroundColor = Color.argb(0xFF, 0x7b, 0xae, 0x88);
            app.notificationBadgeNumber = 0;
            app.allowUserToDismiss(true);

            long appid = mPrefs.getLong("tooleap", 0);
            if (0 == appid) {
                appid = Tooleap.getInstance(this).addMiniApp(app);
                Editor editor = mPrefs.edit();
                editor.putLong("tooleap", appid);
                editor.commit();
            } else {
                Tooleap.getInstance(this).updateMiniApp(appid, app);
            }

            /*StandOutWindow.closeAll(this, SlideOnRemote.class);
            StandOutWindow.show(this, SlideOnRemote.class,
                    StandOutWindow.DEFAULT_ID);*/
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}

package com.doubtech.universalremote;

import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.StandOutWindow;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Toast;

import com.doubtech.universalremote.io.RemoteConfigurationReader;
import com.doubtech.universalremote.io.RemoteConfigurationReader.RemotesLoadedListener;
import com.doubtech.universalremote.io.RemoteFilesLoader;
import com.doubtech.universalremote.io.RemoteFilesLoader.RemoteFile;
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
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private Uri mFile;

    private RemoteFilesLoader mFileLoader;

    private OnNavigationListener mOnNavigationListener = new OnNavigationListener() {

        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            mFile = FileProvider.getUriForFile(Remotes.this,
                    Constants.AUTHORITY_FILE_PROVIDER,
                    mFiles[itemPosition].getFile());
            open(mFile);
            return true;
        }
    };

    private RemoteFile[] mFiles;

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

        mFileLoader = new RemoteFilesLoader(Constants.REMOTES_DIR);
        mFileLoader.load();
        mFiles = mFileLoader.getRemoteFiles();
        if (mFiles.length > 0) {
            mFile = FileProvider.getUriForFile(this,
                    Constants.AUTHORITY_FILE_PROVIDER,
                    mFiles[0].getFile());
        }

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFileLoader.load();
        mFiles = mFileLoader.getRemoteFiles();

        final ActionBar actionBar = getActionBar();
        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
        // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<RemoteFile>(actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, mFiles), mOnNavigationListener);
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

    private void open(Uri uri) {
        if (null != uri) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(
                    getSupportFragmentManager(),
                    uri);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<RemotePage> mPages = new ArrayList<RemotePage>();

        public SectionsPagerAdapter(FragmentManager fm, Uri file) {
            super(fm);

            mPages.clear();
            notifyDataSetChanged();
            RemoteConfigurationReader reader = new RemoteConfigurationReader(Remotes.this);
            reader.open(file, new RemotesLoadedListener() {

                @Override
                public void onRemotesLoaded(Uri uri, List<RemotePage> pages) {
                    for (RemotePage page : pages) {
                        mPages.add(page);
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onRemoteLoadFailed(Throwable error) {
                    Toast.makeText(Remotes.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            SectionFragment fragment = new SectionFragment();
            fragment.setPage(mPages.get(position));
            return fragment;
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPages.get(position).getTitle();
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class SectionFragment extends Fragment {
        private ScrollView mRootView;
        private RemotePage mPage;

        public SectionFragment() {
        }

        public void setPage(RemotePage remotePage) {
            mPage = remotePage;
            if (null != mRootView) {
                mRootView.removeAllViews();
                mRootView.addView(mPage);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            mRootView = new ScrollView(getActivity());
            if (null != mPage) {
                ViewGroup parent = (ViewGroup) mPage.getParent();
                if (null != parent) {
                    parent.removeView(mPage);
                }
                mRootView.addView(mPage);
            }
            return mRootView;
        }
    }
}

package com.doubtech.universalremote;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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
import android.widget.ScrollView;
import android.widget.Toast;

import com.doubtech.universalremote.io.RemoteConfigurationReader;
import com.doubtech.universalremote.io.RemoteConfigurationReader.RemotesLoadedListener;
import com.doubtech.universalremote.utils.Constants;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remotes);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);

        Constants.REMOTE_FILE.getParentFile().mkdirs();

        mFile = FileProvider.getUriForFile(this,
                Constants.AUTHORITY_FILE_PROVIDER,
                Constants.REMOTE_FILE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        open(mFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.remotes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, RemotePageConfiguration.class);
            intent.setData(mFile);
            startActivityForResult(intent, REQUEST_CONFIGURE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONFIGURE && resultCode == RESULT_OK) {
            open(data.getData());
        }
    }

    private void open(Uri uri) {
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());
        mSectionsPagerAdapter.open(uri);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<RemotePage> mPages = new ArrayList<RemotePage>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void open(Uri file) {
            mFile = file;
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
                mPage.setCellSpacing(getResources().getDimensionPixelSize(R.dimen.cell_padding));
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

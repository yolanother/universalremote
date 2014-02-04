package com.doubtech.universalremote;

import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.SlideOnWindow;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.doubtech.universalremote.io.RemoteConfigurationReader;
import com.doubtech.universalremote.io.RemoteConfigurationReader.RemotesLoadedListener;
import com.doubtech.universalremote.utils.Constants;
import com.doubtech.universalremote.widget.RemotePage;

public class SlideOnRemote extends SlideOnWindow {
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private Uri mFile;

    public SlideOnRemote() {
        super(SlideOnRemote.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFile = FileProvider.getUriForFile(this,
                Constants.AUTHORITY_FILE_PROVIDER,
                Constants.REMOTE_FILE);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup root) {
        View v = inflater.inflate(R.layout.activity_remotes, root, true);
        v.setBackgroundColor(Color.argb(180, 0, 0, 0));
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        open(mFile);
        return v;
    }

    @Override
    public String getAppName() {
        return getString(R.string.app_name);
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        if (null != mSectionsPagerAdapter && mSectionsPagerAdapter.getCount() > 0) {
            return "" + mSectionsPagerAdapter.mPages.get(mViewPager.getCurrentItem()).getTitle();
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

    private void open(Uri uri) {
        mSectionsPagerAdapter = new SectionsPagerAdapter();
        mSectionsPagerAdapter.open(uri);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    class test extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return false;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends PagerAdapter {
        List<RemotePage> mPages = new ArrayList<RemotePage>();

        public SectionsPagerAdapter() {
        }

        public void open(Uri file) {
            mFile = file;
            mPages.clear();
            notifyDataSetChanged();
            RemoteConfigurationReader reader = new RemoteConfigurationReader(SlideOnRemote.this);
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
                    Toast.makeText(SlideOnRemote.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPages.get(position).getTitle();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d("AARON", "Button Count: " + mPages.get(position).getChildCount());
            container.addView(mPages.get(position));
            return mPages.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mPages.get(position));
            super.destroyItem(container, position, object);
        }
    }
}

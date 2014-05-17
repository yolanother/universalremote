package com.doubtech.universalremote.adapters;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doubtech.geofenceeditor.SimpleGeofence;
import com.doubtech.universalremote.R;
import com.doubtech.universalremote.RemotesLoadedListener;
import com.doubtech.universalremote.io.RemoteConfigurationFactory;
import com.doubtech.universalremote.io.RemoteConfigurationJsonReader;
import com.doubtech.universalremote.io.RemoteConfigurationReader;
import com.doubtech.universalremote.io.RemoteConfigurationXmlReader;
import com.doubtech.universalremote.utils.Constants;
import com.doubtech.universalremote.widget.RemotePage;

public class RemoteRoomAdapter extends BaseAdapter implements LocationListener {
    public static final String TAG = "UniversalRemote::RemoteRoomAdapter";
    public static class RemoteFile {
        public RemoteFile(String name, Uri file, SimpleGeofence geofence) {
            mName = name;
            mFile = file;
            mGeofence = geofence;
        }
        private String mName;
        private Uri mFile;
        private SimpleGeofence mGeofence;

        public Uri getFile() {
            return mFile;
        }

        public String getName() {
            return mName;
        }

        @Override
        public String toString() {
            return mName;
        }

        public SimpleGeofence getGeofence() {
            return mGeofence;
        }
    }

    private File mDirectory;
    private Context mContext;
    private Handler mHandler;
    private ExecutorService mExecutor;
    private List<RemoteFile> mFiles;
    private List<RemoteFile> mAllFiles;
    private RemoteConfigurationJsonReader mJsonReader;
    private RemoteConfigurationXmlReader mXmlReader;

    public RemoteRoomAdapter(Context context, File directory) {
        mDirectory = directory;
        mContext = context;
        mHandler = new Handler();

        mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.execute(new Runnable() {

            @Override
            public void run() {
                load();
            }
        });
    }

    public void load() {
        mFiles = new ArrayList<RemoteFile>();
        mAllFiles = new ArrayList<RemoteRoomAdapter.RemoteFile>();
        FilenameFilter filter = new FilenameFilter() {


            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase(Locale.getDefault()).endsWith("xml");
            }
        };


        for (final File file : mDirectory.listFiles(filter)) {

            Uri uri = FileProvider.getUriForFile(mContext,
                    Constants.AUTHORITY_FILE_PROVIDER,
                    file);
            RemoteConfigurationReader reader = RemoteConfigurationFactory.getInstance(mContext, file);
            if (null != reader) {
                reader.open(uri, false, new RemotesLoadedListener() {

                    @Override
                    public void onRemotesLoaded(Uri uri, String name, List<RemotePage> pages,
                            SimpleGeofence geofence) {
                        final RemoteFile rf = new RemoteFile(name, uri, geofence);
                        mAllFiles.add(rf);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                addFile(rf);
                            }
                        });
                    }

                    @Override
                    public void onRemoteLoadFailed(Throwable error) {
                        Log.e(TAG, error.getMessage(), error);
                    }
                });
            } else {
                Log.e(TAG, "Could not determine reader for " + file.getName());
            }
        }
    }

    public void addFile(RemoteFile file) {
        if (!mFiles.contains(file)) {
            mFiles.add(file);

            sort();
            notifyDataSetChanged();

            onLocationChanged(getLocation());
        }
    }

    private void sort() {
        Collections.sort(mFiles, new Comparator<RemoteFile>() {

            @Override
            public int compare(RemoteFile lhs, RemoteFile rhs) {
                return lhs.mName.compareTo(rhs.mName);
            }
        });
    }

    @Override
    public int getCount() {
        return null != mFiles ? mFiles.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) convertView;
        if (null == convertView) {
            tv = (TextView) LayoutInflater.from(mContext)
                    .inflate(R.layout.spinner_text, null);
        }
        tv.setText(mFiles.get(position).mName);
        tv.setTextColor(Color.WHITE);
        return tv;
    }

    public void disableLocationServices() {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);
    }

    public void enableLocationServices() {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        float[] results = new float[1];
        for (RemoteFile file : mAllFiles) {
            SimpleGeofence fence = file.getGeofence();
            if (null != fence) {
                if (null != location) {
                    Location.distanceBetween(
                            fence.getLatitude(),
                            fence.getLongitude(),
                            location.getLatitude(),
                            location.getLongitude(),
                            results);
                }
                if ((null == location || results[0] < fence.getRadius()) &&
                        !mFiles.contains(file)) {
                    mFiles.add(file);
                    sort();
                    notifyDataSetChanged();
                } else {
                    mFiles.remove(file);
                    notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        return location;
    }
}

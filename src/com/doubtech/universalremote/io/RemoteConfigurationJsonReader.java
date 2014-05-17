package com.doubtech.universalremote.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.doubtech.geofenceeditor.SimpleGeofence;
import com.doubtech.universalremote.RemotesLoadedListener;
import com.doubtech.universalremote.utils.Constants;
import com.doubtech.universalremote.utils.Utils;
import com.doubtech.universalremote.widget.RemotePage;

public class RemoteConfigurationJsonReader implements RemoteConfigurationReader {
    private class ErrorRunnable implements Runnable {

        private Throwable mError;
        private RemotesLoadedListener mListener;

        public ErrorRunnable(RemotesLoadedListener listener,
                Throwable e) {
            mError = e;
            mListener = listener;
        }

        @Override
        public void run() {
            if (null != mListener) {
                mListener.onRemoteLoadFailed(mError);
            } else {
                Log.d(TAG, mError.getMessage(), mError);
            }
        }
    }

    private static ExecutorService sLoaderService = Executors.newFixedThreadPool(2);
    private Handler mHandler;
    private Context mContext;

    public RemoteConfigurationJsonReader(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void open(final Uri uri, final boolean readPages, final RemotesLoadedListener listener) {
        sLoaderService.execute(new Runnable() {
            @Override
            public void run() {
                    ParcelFileDescriptor fd = null;
                    FileInputStream stream = null;
                    InputStreamReader streamReader = null;
                    BufferedReader bufferedReader = null;
                    try {
                        fd = mContext.getContentResolver().openFileDescriptor(uri, "r");
                        stream = new FileInputStream(fd.getFileDescriptor());
                        streamReader = new InputStreamReader(stream);
                        bufferedReader = new BufferedReader(streamReader);
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            builder.append(line);
                        }

                        JSONObject json = new JSONObject(builder.toString());
                        JSONArray jsonPages = json.getJSONArray(Constants.FIELD_REMOTE_PAGES);

                        final ArrayList<RemotePage> pages = new ArrayList<RemotePage>();
                        final String name = json.getString(Constants.FIELD_NAME);
                        SimpleGeofence[] geofences = readGeofence(json);
                        final SimpleGeofence geofence = null != geofences? geofences[0] : null;

                        for (int i = 0; i < jsonPages.length(); i++) {
                            JSONObject jsonPage = jsonPages.getJSONObject(i);
                            RemotePage page = new RemotePage(mContext);
                            page.fromJson(jsonPage);
                            pages.add(page);
                        }

                        if (null != listener) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onRemotesLoaded(uri, name, pages, geofence);
                                }
                            });
                        }
                    } catch (IOException | JSONException e) {
                        mHandler.post(new ErrorRunnable(listener, e));
                    } finally {
                        Utils.closeQuietly(bufferedReader);
                        Utils.closeQuietly(streamReader);
                        Utils.closeQuietly(stream);
                        Utils.closeQuietly(fd);
                    }
            }
        });
    }

    public static SimpleGeofence[] readGeofence(JSONObject root) throws JSONException {
        if (!root.has(Constants.FIELD_GEOFENCES)) return null;

        JSONArray geofencesJson = root.getJSONArray(Constants.FIELD_GEOFENCES);
        SimpleGeofence[] geofences = new SimpleGeofence[geofencesJson.length()];
        for (int i = 0; i < geofences.length; i++) {
            JSONObject geofence = geofencesJson.getJSONObject(i);
            geofences[i] = new SimpleGeofence(
                    geofence.getString(SimpleGeofence.EXTRA_ID),
                    geofence.getDouble(SimpleGeofence.EXTRA_LATITUDE),
                    geofence.getDouble(SimpleGeofence.EXTRA_LONGITUDE),
                    (float) geofence.getDouble(SimpleGeofence.EXTRA_RADIUS),
                    geofence.getLong(SimpleGeofence.EXTRA_EXPIRATION),
                    SimpleGeofence.transitionTypeFromString(geofence.getString(SimpleGeofence.EXTRA_TRANSITION_TYPE)));

            if (geofence.has(SimpleGeofence.EXTRA_NAME)) {
                geofences[i].setName(geofence.getString(SimpleGeofence.EXTRA_NAME));
            }
        }
        return geofences;
    }
}

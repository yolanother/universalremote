package com.doubtech.universalremote.io;

import android.net.Uri;

import com.doubtech.universalremote.RemotesLoadedListener;


public interface RemoteConfigurationReader {
    static final String TAG = "UniversalRemote :: RemoteConfigurationReader";

    void open(final Uri uri, final boolean readPages, final RemotesLoadedListener listener);
}

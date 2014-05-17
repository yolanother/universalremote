package com.doubtech.universalremote;

import java.util.List;

import android.net.Uri;

import com.doubtech.geofenceeditor.SimpleGeofence;
import com.doubtech.universalremote.widget.RemotePage;

public interface RemotesLoadedListener {
    void onRemotesLoaded(Uri uri, String name, List<RemotePage> pages, SimpleGeofence geofence);
    void onRemoteLoadFailed(Throwable error);
}

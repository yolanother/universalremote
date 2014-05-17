package com.doubtech.universalremote.io;

import java.io.IOException;

import com.doubtech.geofenceeditor.SimpleGeofence;
import com.doubtech.universalremote.widget.RemotePage;

public interface RemoteConfigurationWriter {

    void addPage(RemotePage page) throws IOException;

    void close() throws IOException;

    void writeGeofence(SimpleGeofence geofence) throws IOException;

}

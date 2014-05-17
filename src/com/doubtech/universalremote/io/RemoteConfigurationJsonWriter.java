package com.doubtech.universalremote.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.doubtech.geofenceeditor.SimpleGeofence;
import com.doubtech.universalremote.utils.Constants;
import com.doubtech.universalremote.widget.RemotePage;

public class RemoteConfigurationJsonWriter implements RemoteConfigurationWriter {
    JSONArray mPages;
    JSONArray mGeofences;
    private OutputStreamWriter mStream;
    private JSONException mJsonException;
    private CharSequence mName;
    public RemoteConfigurationJsonWriter(CharSequence name) throws JSONException {
        mPages = new JSONArray();
        mName = name;
    }
    public RemoteConfigurationJsonWriter(OutputStream stream, CharSequence name) throws JSONException {
        this(name);
        mStream = new OutputStreamWriter(stream);
    }

    @Override
    public void addPage(RemotePage page) throws IOException {
        try {
            mPages.put(page.toJson());
        } catch (JSONException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        mJsonException = null;
        try {
            JSONObject json = new JSONObject();
            json.put(Constants.FIELD_NAME, mName);
            json.put(Constants.FIELD_REMOTE_PAGES, mPages);
            if (null != mGeofences && mGeofences.length() > 0) {
                json.put(Constants.FIELD_GEOFENCES, mGeofences);
            }
            return json.toString(2);
        } catch (JSONException e) {
            mJsonException = e;
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        if (null != mStream) {
            mStream.write(toString());
            mStream.flush();
            if (null != mJsonException) {
                throw new IOException(mJsonException.getMessage(), mJsonException);
            }
        }
    }

    @Override
    public void writeGeofence(SimpleGeofence geofence) throws IOException {
        try {
            JSONObject geofenceJson = new JSONObject();
            geofenceJson.put(SimpleGeofence.EXTRA_ID, geofence.getId());
            if (null != geofence.getName()) {
                geofenceJson.put(SimpleGeofence.EXTRA_NAME, geofence.getName());
            }
            geofenceJson.put(SimpleGeofence.EXTRA_LATITUDE, Double.toString(geofence.getLatitude()));
            geofenceJson.put(SimpleGeofence.EXTRA_LONGITUDE, Double.toString(geofence.getLongitude()));
            geofenceJson.put(SimpleGeofence.EXTRA_RADIUS, Float.toString(geofence.getRadius()));
            geofenceJson.put(SimpleGeofence.EXTRA_EXPIRATION, Long.toString(geofence.getExpirationDuration()));
            geofenceJson.put(SimpleGeofence.EXTRA_TRANSITION_TYPE, geofence.getTransitionTypeString());
            mGeofences.put(geofenceJson);
        } catch (JSONException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}

package com.doubtech.universalremote.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.doubtech.geofenceeditor.SimpleGeofence;
import com.doubtech.universalremote.widget.RemotePage;

public class RemoteConfigurationXmlWriter implements RemoteConfigurationWriter {
    private XmlSerializer mXml;

    public RemoteConfigurationXmlWriter(OutputStream stream, CharSequence name)
            throws IllegalArgumentException, IllegalStateException, IOException {
        mXml = Xml.newSerializer();
        // also set the line separator
        mXml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        mXml.setOutput(new OutputStreamWriter(stream));
        mXml.startDocument("UTF-8", true);
        mXml.startTag("", "remotes");
        if (null != name) {
            mXml.attribute("", "name", name.toString());
        }
    }

    @Override
    public void addPage(RemotePage page) throws IOException {
        page.writeXml(mXml);
    }

    @Override
    public void close() throws IOException {
        mXml.endTag("", "remotes");
        mXml.endDocument();
    }

    @Override
    public void writeGeofence(SimpleGeofence geofence) throws IOException {
        writeGeofence(mXml, geofence);
    }

    public static void writeGeofence(XmlSerializer xml, SimpleGeofence geofence) throws IOException {
        xml.startTag("", "geofence");
        xml.attribute("", SimpleGeofence.EXTRA_ID, geofence.getId());
        if (null != geofence.getName()) {
            xml.attribute("", SimpleGeofence.EXTRA_NAME, geofence.getName());
        }
        xml.attribute("", SimpleGeofence.EXTRA_LATITUDE, Double.toString(geofence.getLatitude()));
        xml.attribute("", SimpleGeofence.EXTRA_LONGITUDE, Double.toString(geofence.getLongitude()));
        xml.attribute("", SimpleGeofence.EXTRA_RADIUS, Float.toString(geofence.getRadius()));
        xml.attribute("", SimpleGeofence.EXTRA_EXPIRATION, Long.toString(geofence.getExpirationDuration()));
        xml.attribute("", SimpleGeofence.EXTRA_TRANSITION_TYPE, geofence.getTransitionTypeString());

        xml.endTag("", "geofence");
    }
}

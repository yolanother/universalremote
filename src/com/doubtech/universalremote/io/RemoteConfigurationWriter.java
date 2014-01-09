package com.doubtech.universalremote.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.doubtech.universalremote.RemotePage;

public class RemoteConfigurationWriter {
    private XmlSerializer mXml;

    public RemoteConfigurationWriter(OutputStream stream, String name)
            throws IllegalArgumentException, IllegalStateException, IOException {
        mXml = Xml.newSerializer();
        // also set the line separator
        mXml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        mXml.setOutput(new OutputStreamWriter(stream));
        mXml.startDocument("UTF-8", true);
        mXml.startTag("", "remotes");
        mXml.attribute("", "name", name);
    }

    public void addPage(RemotePage page) throws IllegalArgumentException,
            IllegalStateException, IOException {
        page.writeXml(mXml);
    }

    public void close() throws IllegalArgumentException, IllegalStateException, IOException {
        mXml.endTag("", "remotes");
        mXml.endDocument();
    }
}

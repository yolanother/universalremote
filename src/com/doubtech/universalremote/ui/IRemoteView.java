package com.doubtech.universalremote.ui;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import com.doubtech.universalremote.json.IJsonElement;
import com.doubtech.universalremote.widget.DropGridLayout.ChildSpec;

public interface IRemoteView extends IJsonElement {
    void writeXml(XmlSerializer xml, ChildSpec childSpec) throws IllegalArgumentException,
            IllegalStateException, IOException;
    void setEditMode(boolean editMode);
}

package com.doubtech.universalremote.ui;

import org.w3c.dom.Element;

import android.content.Context;
import android.graphics.Color;

import com.doubtech.universalremote.ButtonFunction;

public class RemoteToggleButton extends RemoteButton implements IRemoteView {
    public static final String XMLTAG = "toggle-button";

    public RemoteToggleButton(Context context) {
        super(context);
        setBackgroundColor(Color.MAGENTA);
    }

    public void addState(ButtonFunction state) {
        // TODO Auto-generated method stub

    }

    public int getStateCount() {
        return getFunctionCount();
    }

    @Override
    public String getXmlTag() {
        return XMLTAG;
    }

    public static RemoteButton fromXml(Context context, Element item) {
        RemoteToggleButton button = new RemoteToggleButton(context);
        RemoteButton.fromXml(button, context, item);
        return button;
    }
}

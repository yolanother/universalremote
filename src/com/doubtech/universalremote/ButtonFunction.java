package com.doubtech.universalremote;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.utils.ButtonIdentifier;
import com.doubtech.universalremote.utils.ButtonStyler;
import com.doubtech.universalremote.utils.IconLoader;
import com.doubtech.universalremote.utils.ProviderUtils;

public class ButtonFunction extends ButtonFunctionSet {
    private static final long serialVersionUID = 1L;
    private Button mButton;
    private String mLabel;
    private int mButtonIdentifier;
    private transient Bitmap mButtonIcon;
    private transient ConcurrentLinkedQueue<IconLoaderListener> mLoadedListeners = new ConcurrentLinkedQueue<IconLoaderListener>();

    public ButtonFunction(Context context, Button button) {
        mButton = button;
        mLabel = ButtonStyler.getLabel(context.getResources(), mButton.getName());

        // TODO This is ugly and the label object isn't really needed anymore
        mLabel = ButtonStyler.getLabel(context.getResources(), mLabel);
        mButtonIdentifier = mButton.getButtonIdentifier();
        // Button identifier wasn't set so let's try one more time to get it
        if (0 == mButtonIdentifier && null != mLabel) {
            mButtonIdentifier = ButtonIdentifier.getKnownButton(mLabel);
        }
    }

    public ButtonFunction() {
        // This will be set up by a builder/from method
    }

    public void getIcon(final Context context, final IconLoaderListener listener) {
        if (null == mButtonIcon) {
            IconLoader.loadIcon(context,
                    mButton,
                    new IconLoaderListener() {
                        @Override
                        public void onIconLoaded(Bitmap bitmap) {
                            mButtonIcon = bitmap;
                            listener.onIconLoaded(bitmap);
                            while (mLoadedListeners.size() > 0) {
                                mLoadedListeners.poll().onIconLoaded(bitmap);
                            }
                        }
                    });
        } else {
            listener.onIconLoaded(mButtonIcon);
        }
    }

    public String getLabel() {
        return mLabel;
    }

    public int getButtonIdentifier() {
        return mButtonIdentifier;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public static final String XMLTAG = "button-function";

    @Override
    public void writeXml(XmlSerializer xml) throws IllegalArgumentException, IllegalStateException, IOException {
        xml.startTag("", XMLTAG);
        xml.attribute("", "id", mButton.getPathString());
        xml.attribute("", "authority", mButton.getAuthority());
        xml.attribute("", "label", mLabel);
        xml.endTag("", XMLTAG);

        super.writeXml(xml);
    }

    private static Executor mDetailLoader = Executors.newSingleThreadExecutor();

    public static ButtonFunction fromXml(final Context context, Element item) {
        final ButtonFunction details = new ButtonFunction();
        details.mButton = new Button(item.getAttribute("authority"), item.getAttribute("id"), true);
        details.mLabel = item.getAttribute("label");
        details.mButton.setName(details.mLabel);
        details.loadDetails(context);
        return details;
    }

    public Button getButton() {
        return mButton;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object = super.toJson();
        object.put("id", mButton.getPathString());
        object.put("authority", mButton.getAuthority());
        object.put("label", mLabel);
        if (null != mButton.getHardwareUri()) {
            object.put("hardware", mButton.getHardwareUri());
        }
        return object;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        super.fromJson(object);
        try {
            mButton = new Button(
                    object.getString("authority"),
                    object.getString("id"), true);
            mLabel = object.getString("label");
            mButton.setName(mLabel);
        } catch (JSONException e) {
            Log.d("UniversalRemote:JSON", e.getMessage() + ": " + object.toString(2));
            throw e;
        }
    }

    public void loadDetails(final Context context) {
        mDetailLoader.execute(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = ProviderUtils.query(context, mButton);
                try {
                    mButton = (Button) Button.fromCursor(mButton, cursor);
                    // Reset the name again just incase the button instance has changed (it probably has)
                    mButton.setName(mLabel);
                } catch (Exception e) {
                    Log.e("UniversalRemote", e.getMessage(), e);
                }
            }
        });
    }
}

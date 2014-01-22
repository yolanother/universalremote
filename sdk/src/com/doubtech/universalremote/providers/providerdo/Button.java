package com.doubtech.universalremote.providers.providerdo;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.doubtech.universalremote.providers.AbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.URPContract.Parents;
import com.doubtech.universalremote.utils.ButtonIdentifier;

public class Button extends Parent {

    protected int mButtonIdentifier = 0;
    protected HashMap<String, String> mInternalData = new HashMap<String, String>();


    private Button() {

    }

    public Button(String authority, String[] parentIds) {
        super(authority, parentIds);
    }

    public Button(String authority, String path) {
        super(authority, getPath(path));
    }

    static Button fromCursor(Button button, Cursor cursor) {
        // We now have enough to get/save the button to the cache. Get the
        // button from the cache just incase any column data is missing or
        // the cached version has extra data provided by the adapter
        button = (Button) getCached(button);

        int idx = cursor.getColumnIndex(URPContract.Buttons.COLUMN_BUTTON_IDENTIFIER);
        if (idx >= 0) {
            button.mButtonIdentifier = ButtonIdentifier.getKnownButton(cursor.getString(idx));
        }

        return button;
    }

    public static Button fromJson(AbstractUniversalRemoteProvider provider,
            JSONObject obj, Button button) throws JSONException {
        button.mButtonIdentifier = ButtonIdentifier.getKnownButton(obj
                .getString("buttonName"));
        Iterator<?> iterator = obj.keys();
        while (iterator.hasNext()) {
            String key = "" + iterator.next();
            button.putExtra(key, obj.getString(key));
        }
        return button;
    }

    @Override
    public Object[] toRow() {
        Object[] row = new Object[] {
                hashCode(),
                getAuthority(),
                getPathString(),
                getName(),
                Parents.TYPE_BUTTON,
                getButtonIdentifier()
        };
        return row;
    }

    public int getButtonIdentifier() {
        return mButtonIdentifier;
    }

    public String getName() {
        return mName;
    }

    /**
     * Extra data that is private to the local provider that might be needed
     * to send the button
     * @return
     */
    public String getInternalData(String name) {
        return mInternalData.get(name);
    }

    public Button putExtra(String name, String data) {
        mInternalData.put(name, data);
        return this;
    }

    public static Parent fromUri(Uri uri) {
        Button model = new Button();
        return Parent.fromUri(model, uri);
    }

    public void click(Context context) {
        AbstractUniversalRemoteProvider.sendButton(context, this);
    }
}

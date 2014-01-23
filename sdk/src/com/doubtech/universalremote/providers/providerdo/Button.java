package com.doubtech.universalremote.providers.providerdo;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.doubtech.universalremote.providers.AbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.URPContract.Buttons;
import com.doubtech.universalremote.providers.URPContract.Parents;
import com.doubtech.universalremote.providers.providerdo.Parent.ParentBuilder;
import com.doubtech.universalremote.utils.ButtonIdentifier;

public class Button extends Parent {

    protected int mButtonIdentifier = 0;
    protected HashMap<String, String> mInternalData = new HashMap<String, String>();


	
	public static class ButtonBuilder extends ParentBuilder{
		public ButtonBuilder(String authority, String[] path) {
			super(new Button(authority, path, false));
		}
		
		@Override
		public ParentBuilder setName(String name) {
			if(0 == ((Button)mParent).mButtonIdentifier) {
				setButtonIdentifier(ButtonIdentifier.getKnownButton(name));
			}
			return super.setName(name);
		}
		
		public ParentBuilder setButtonIdentifier(int buttonIdentifier) {
			((Button)mParent).mButtonIdentifier = buttonIdentifier;
			return this;
		}
		
		public ParentBuilder putExtra(String name, String extra) {
			((Button)mParent).putExtra(name, extra);
			return this;
		}
	}

    private Button() {

    }

    public Button(String authority, String[] parentIds, boolean needsToFetch) {
        super(authority, parentIds, needsToFetch);
    }

    public Button(String authority, String path, boolean needsToFetch) {
        super(authority, getPath(path), needsToFetch);
    }

    static Button fromCursor(Button button, Cursor cursor) {
        // We now have enough to get/save the button to the cache. Get the
        // button from the cache just incase any column data is missing or
        // the cached version has extra data provided by the adapter
        button = (Button) getCached(button);

        button.mButtonIdentifier = ButtonIdentifier.getKnownButton(button.getName());

        return button;
    }

    public static Button fromJson(AbstractUniversalRemoteProvider provider,
            JSONObject obj, Button button) throws JSONException {
        button.mButtonIdentifier = ButtonIdentifier.getKnownButton(button.getName());
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
                getLevelName(),
                getName(),
                getDescription(),
                Parents.TYPE_BUTTON,
                hasButtonSets() ? 1 : 0,
                getButtonIdentifier()
        };
        return row;
    }
    
    @Override
    public String[] getColumns() {
    	return Buttons.ALL;
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

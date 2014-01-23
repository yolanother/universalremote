package com.doubtech.universalremote.providers.providerdo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.net.Uri;

import com.doubtech.universalremote.providers.AbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.URPContract.Parents;

public class Parent {
    protected String mAuthority;
    protected String[] mPath;
    protected String mName;
    private int mHashCode;
	private boolean mHasButtonSets;
	private String mDescription;
	private boolean mNeedsToFetch = true;
	private Parent[] mChildren = new Parent[0];
	private String mLevelName;

    protected Parent() {

    }

    public Parent(String authority, String[] path, boolean needsToFetch) {
        mAuthority = authority;
        mPath = path;
        mNeedsToFetch = needsToFetch;
    }

    public Parent(String authority, String id, boolean needsToFetch) {
        mAuthority = authority;
        mPath = new String[] {id};
        mNeedsToFetch = needsToFetch;
    }

    public static Parent fromUri(Parent parent, Uri uri) {
        List<String> segments = uri.getPathSegments();
        String[] path = new String[segments.size() - 1];
        for (int i = 0; i < path.length; i++) {
            path[i] = segments.get(i + 1);
        }
        parent.mAuthority = uri.getAuthority();
        parent.mPath = path;
        return getCached(parent);
    }

    public static Parent fromUri(Uri uri) {
        return fromUri(new Parent(), uri);
    }

    public Uri getUri() {
        return URPContract.getUri(mAuthority, URPContract.TABLE_BUTTONS_PATH, getPath());
    }

    public static Parent fromCursor(Cursor cursor) {
        int idx;
        idx = cursor.getColumnIndex(Parents.COLUMN_AUTHORITY);
        String authority = cursor.getString(idx);

        String parent = "";
        idx = cursor.getColumnIndex(Parents.COLUMN_PATH);
        if (idx >= 0) {
            parent = cursor.getString(idx);
        }

        idx = cursor.getColumnIndex(Parents.COLUMN_TYPE);
        boolean isParent = -1 == idx || "parent".equals(cursor.getString(idx));

        Parent node;

        node = isParent ? new Parent(authority, getPath(parent), true) :
        		new Button(authority, getPath(parent), true);

        idx = cursor.getColumnIndex(Parents.COLUMN_NAME);
        if (idx >= 0) {
            node.mName = cursor.getString(idx);
        }

        idx = cursor.getColumnIndex(Parents.COLUMN_LEVEL);
        if (idx >= 0) {
            node.mLevelName = cursor.getString(idx);
        }

        idx = cursor.getColumnIndex(Parents.COLUMN_HAS_BUTTONSETS);
        if (idx >= 0) {
            node.mHasButtonSets = cursor.getInt(idx) != 0;
        }

        if(!isParent) {
        	node = Button.fromCursor((Button) node, cursor);
        }

        return node;
    }

    static String[] getPath(String path) {
        String[] segments = path.split("/");
        for (int i = 0; i < segments.length; i++) {
            segments[i] = Uri.decode(segments[i]);
        }
        return segments;
    }

    static String[] getPath(String parent, JSONObject obj) throws JSONException {
        String[] path = getPath(parent);
        String[] fullPath = new String[path.length + 1];
        for (int i = 0; i < path.length; i++) {
            fullPath[i] = path[i];
        }
        fullPath[path.length] = obj.getString("id");
        return fullPath;
    }

    public static Parent[] fromJson(AbstractUniversalRemoteProvider provider, Parent parentNode, String json) throws JSONException {
        if (null == json || json.length() == 0) return new Parent[0];
        JSONObject obj = new JSONObject(json);
        boolean isParent = "parent".equals(obj.getString("objectType"));
        JSONArray array = obj.getJSONArray("objects");
        Parent[] models = new Parent[array.length()];
        String parent = obj.getString("parent");
        String authority = provider.getAuthority();
        String levelName = null;
        boolean hasButtonSets = false;
        if(obj.has(Parents.COLUMN_HAS_BUTTONSETS)) {
        	hasButtonSets = obj.getBoolean(Parents.COLUMN_HAS_BUTTONSETS);
        }
        if(obj.has(Parents.COLUMN_LEVEL)) {
        	levelName = obj.getString(Parents.COLUMN_LEVEL);
        }
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            String[] path = getPath(parent, obj);
            Parent node = isParent ? new Parent(authority, path, false)
                : new Button(authority, path, false);
            node.mName = obj.getString(Parents.COLUMN_NAME);
        	node.mHasButtonSets = hasButtonSets || provider.hasButtonSets(node);
        	node.mLevelName = levelName;
        	if(obj.has(Parents.COLUMN_DESCRIPTION)) {
        		node.mDescription = obj.getString(Parents.COLUMN_DESCRIPTION);
        	} else {
        		node.mDescription = provider.getDescription(node);
        	}
        	if(!isParent) {
        		node = Button.fromJson(provider, obj, (Button) node);
        	}
            models[i] = getCached(node);
        }
        if(null != parentNode) {
        	parentNode.setChildren(models);
        }
        return models;
    }

    public String[] getColumns() {
        return URPContract.Parents.ALL;
    }

    public Object[] toRow() {
        Object[] row = new Object[] {
                hashCode(),
                getAuthority(),
                getPathString(),
                getLevelName(),
                getName(),
                getDescription(),
                Parents.TYPE_PARENT,
                hasButtonSets() ? 1 : 0
        };
        return row;
    }

    public String getLevelName() {
		return mLevelName;
	}

	public String getName() {
        return mName;
    }

    public String getDescription() {
    	return mDescription;
    }

    public String[] getPath() {
        return mPath;
    }

    public String getPathString() {
        StringBuilder path = new StringBuilder();
        for (String segment : getPath()) {
            path.append("/");
            path.append(Uri.encode(segment));
        }
        return path.toString();
    }

    public String getAuthority() {
        return mAuthority;
    }

    @Override
    public int hashCode() {
        if (0 == mHashCode) {

            mHashCode = getPathString().hashCode();
        }
        return mHashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Parent) {
            Parent b = (Parent) o;
            return getPathString().equals(b.getPathString());
        }
        return false;
    }

    private static ConcurrentHashMap<Parent, Parent> mCache = new ConcurrentHashMap<Parent, Parent>();
    public static Parent getCached(Parent button) {
        Parent b = mCache.get(button);
        if (null == b) {
            b = button;
            // Only cache objects that have all of their data.
            if(!button.needsToFetch()) {
            	mCache.put(b, b);
            }
        }
        return b;
    }

    public boolean hasButtonSets() {
        return mHasButtonSets;
    }
    
    public boolean needsToFetch() {
    	return mNeedsToFetch;
    }

    public void setNeedsToFetch(boolean needsToFetch) {
    	mNeedsToFetch = needsToFetch;
    }

	public void setChildren(Parent[] children) {
		mChildren = children;
	}
	
	public Parent[] getChildren() {
		return mChildren;
	}
}

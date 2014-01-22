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

    protected Parent() {

    }

    public Parent(String authority, String[] path) {
        mAuthority = authority;
        mPath = path;
    }

    public Parent(String authority, String id) {
        mAuthority = authority;
        mPath = new String[] {id};
    }

    public static Parent fromUri(Parent parent, Uri uri) {
        List<String> segments = uri.getPathSegments();
        String[] path = new String[segments.size() - 1];
        for (int i = 0; i < path.length; i++) {
            path[i] = segments.get(i + 1);
        }
        parent.mAuthority = uri.getAuthority();
        parent.mPath = path;
        return parent;
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

        if (isParent) {
            node = new Parent(authority, getPath(parent));
        } else {
            node = new Button(authority, getPath(parent));
            node = Button.fromCursor((Button) node, cursor);
        }

        idx = cursor.getColumnIndex(Parents.COLUMN_NAME);
        if (idx >= 0) {
            node.mName = cursor.getString(idx);
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

    public static Parent[] fromJson(AbstractUniversalRemoteProvider provider, String json) throws JSONException {
        if (null == json || json.length() == 0) return new Parent[0];
        JSONObject obj = new JSONObject(json);
        boolean isParent = "parent".equals(obj.getString("objectType"));
        JSONArray array = obj.getJSONArray("objects");
        Parent[] models = new Parent[array.length()];
        String parent = obj.getString("parent");
        String authority = provider.getAuthority();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            String[] path = getPath(parent, obj);
            Parent node = isParent ? new Parent(authority, path)
                : Button.fromJson(provider, obj, new Button(authority, path));
            node.mName = obj.getString("name");
            models[i] = node;
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
                getName(),
                Parents.TYPE_PARENT
        };
        return row;
    }

    public String getName() {
        return mName;
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
            mCache.put(b, b);
        }
        return b;
    }

    public boolean hasButtons() {
        // TODO Auto-generated method stub
        return false;
    }
}

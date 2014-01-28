package com.doubtech.universalremote.jsonretreivers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import android.content.Context;
import android.util.Log;

import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.utils.IOUtil;
import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;

public abstract class HttpJsonRetreiver implements JsonRetreiver {
    public static final String TAG = "UniversalRemote::HttpJsonRetreiver";

    // TODO replace with restful version...

    private static final int VERSION = 1;
    private DiskLruCache mCache;

    public HttpJsonRetreiver(Context context, File cache) {
        cache.mkdirs();
        try {
            mCache = DiskLruCache.open(cache, VERSION, 1, 1024 * 1024 * 2 /* 2MB */);
        } catch (IOException e) {
            Log.w(TAG, "Could not initialize disk cache, button access may be slow.", e);
        }
    }

    private String get(final URL url) throws IOException {
        final String key = getUrlKey(url);
        Snapshot snapshot = mCache != null ? mCache.get(key) : null;
        String json = "";
        if (null == mCache || null == snapshot) {
            try {
                json = doHttpGet(url);
                if (null == json) {
                    json = "";
                } else if (null != mCache) {
                    Editor editor = mCache.edit(key);
                    if (null != editor) {
                        editor.set(0, json);
                        editor.commit();
                    }
                }
            } catch (IOException e) {
                Log.w(TAG, e.getMessage(), e);
            }
        } else {
            json = snapshot.getString(0);
        }

        IOUtil.closeQuietly(snapshot);
        return json;
    }

    private String getUrlKey(URL url) {
        return url.toString().replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    private String doHttpGet(URL url) throws IOException {
        Log.d(TAG, "Getting " + url);
        return doReadStream(url.openStream());
    }

    private String doReadStream(InputStream stream) throws IOException {
        String json = "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(stream));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                json += inputLine;
        } finally {
            IOUtil.closeQuietly(in);
        }
        return json;
    }

    @Override
    public String getJson(Parent parent) {
        try {
            return get(getUrl(parent));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return "";
    }

    protected abstract URL getUrl(Parent parent);
}

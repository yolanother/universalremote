package com.doubtech.universalremote.providers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.doubtech.universalremote.jsonretreivers.HttpJsonRetreiver;
import com.doubtech.universalremote.jsonretreivers.JsonRetreiver;
import com.doubtech.universalremote.providers.providerdo.Parent;

public abstract class AbstractJsonUniversalRemoteProvider extends
        AbstractUniversalRemoteProvider {
    public static final String TAG = "UniversalRemote : AbstractJsonURP";

    protected class Retreiver extends HttpJsonRetreiver {

        public Retreiver(Context context, File cache) {
            super(context, cache);
        }

        @Override
        protected URL getUrl(Parent parent) {
            try {
                return new URL(getUrlString(parent));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Bad url generated. Should never get here.");
            }
        }
    }

    private Retreiver mRetreiver;

    @Override
    public Parent[] getChildren(Parent parent) {
        try {
            parent = Parent.getCached(parent);
            if (null != parent && parent.getChildren().length > 0) {
                return parent.getChildren();
            }


            return Parent.fromJson(this, parent, getJsonRetreiver().getJson(parent));
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return new Parent[0];
    }

    public Parent getDetails(Parent parent) {
        // Load details for this button
        try {
            Parent.fromJson(this, parent, getJsonRetreiver().getJson(parent.getParent()));
        } catch (JSONException e) {
            Log.w(TAG, e.getMessage(), e);
        }

        // The details were cached on the load so return them.
        return Parent.getCached(parent);
    }

    public JsonRetreiver getJsonRetreiver() {
        if (null == mRetreiver) {
            mRetreiver = new Retreiver(getContext(), new File(getContext().getCacheDir(), getAuthority()));
        }
        return mRetreiver;
    }

    public abstract String getUrlString(Parent parent);
}

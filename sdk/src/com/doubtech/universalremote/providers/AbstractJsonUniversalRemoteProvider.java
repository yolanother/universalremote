package com.doubtech.universalremote.providers;

import org.json.JSONException;

import android.util.Log;

import com.doubtech.universalremote.jsonretreivers.JsonRetreiver;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Parent;

public abstract class AbstractJsonUniversalRemoteProvider extends
        AbstractUniversalRemoteProvider {
    public static final String TAG = "UniversalRemote : AbstractJsonURP";

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

    public abstract JsonRetreiver getJsonRetreiver();
}

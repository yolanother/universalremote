package com.doubtech.universalremote.providers;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.doubtech.universalremote.jsonretreivers.JsonRetreiver;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Parent;

public abstract class AbstractJsonUniversalRemoteProvider extends
        AbstractUniversalRemoteProvider {
    public static final String TAG = "UniversalRemote : AbstractJsonURP";

    @Override
    public Parent[] get(Parent parent) {
        try {
            return Parent.fromJson(this, getJsonRetreiver().getJson(parent));
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return new Parent[0];
    }

    public abstract JsonRetreiver getJsonRetreiver();
}

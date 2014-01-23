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
    public Parent[] get(Parent parent) {
        try {
        	parent = Parent.getCached(parent);
        	if(!parent.needsToFetch() && parent instanceof Button) {
        		return new Parent[] { parent };
        	} else if (null != parent && parent.getChildren().length > 0) {
        		return parent.getChildren();
        	}
    		return Parent.fromJson(this, parent, getJsonRetreiver().getJson(parent));
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return new Parent[0];
    }

    public abstract JsonRetreiver getJsonRetreiver();
}

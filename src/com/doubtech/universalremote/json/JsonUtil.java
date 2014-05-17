package com.doubtech.universalremote.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.doubtech.universalremote.ButtonFunction;
import com.doubtech.universalremote.ButtonFunctionSet;

public class JsonUtil {
    private static final String TAG = "UniversalRemote::JsonUtil";

    public static boolean getBoolean(JSONObject object, String name, boolean defaultValue) {
        try {
            return object.has(name) ? object.getBoolean(name) : defaultValue;
        } catch (JSONException e) {
            report(object, e);
            return defaultValue;
        }
    }

    public static int getInt(JSONObject object, String name, int defaultValue) {
        try {
            return object.has(name) ? object.getInt(name) : defaultValue;
        } catch (JSONException e) {
            report(object, e);
            return defaultValue;
        }
    }

    public static ButtonFunctionSet getButton(Context context, JSONObject object, String name) {
        ButtonFunctionSet button = null;
        if (object.has(name)) {
            try {
                button = new ButtonFunction();
                button.fromJson(object.getJSONObject(name));
                button.loadDetails(context);
            } catch (JSONException e) {
                report(object, e);
                button = null;
            }
        }
        return button;
    }

    private static void report(JSONObject object, JSONException e) {
        try {
            Log.w("UniversalRemote:JSON", e.getMessage() + "\n" + object.toString(2));
        } catch (JSONException e1) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Puts an IJsonElement object in a JSONObject. If the sourceObject is null
     * nothing happens.
     * @param targetObject The object you want to add to
     * @param name The name you want to use to store your sourceObject
     * @param sourceObject The IJsonElement you want to store.
     * @return Returns the new JSONObject created by the IJsonElement
     */
    public static JSONObject put(JSONObject targetObject, String name, IJsonElement sourceObject) {
        JSONObject object = null;
        if (null != sourceObject && null != targetObject) {
            try {
                object = sourceObject.toJson();
                targetObject.put(name, object);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return object;
    }

    public static String toDebugString(IJsonElement jsonElement) {
        if (null == jsonElement) return null;

        try {
            String debugString = toDebugString(jsonElement.toJson());
            if (null == debugString) {
                return jsonElement.toString();
            } else {
                return debugString;
            }
        } catch (JSONException e) {
            return jsonElement.toString();
        }
    }

    public static String toDebugString(JSONObject object) {
        if (null == object) return null;
        try {
            return object.toString(2);
        } catch (JSONException e) {
            return null;
        }
    }
}

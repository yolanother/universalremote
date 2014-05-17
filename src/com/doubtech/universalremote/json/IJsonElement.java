package com.doubtech.universalremote.json;

import org.json.JSONException;
import org.json.JSONObject;

public interface IJsonElement {
    JSONObject toJson() throws JSONException;
    void fromJson(JSONObject object) throws JSONException;
}

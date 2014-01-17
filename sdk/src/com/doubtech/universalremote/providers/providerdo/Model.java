package com.doubtech.universalremote.providers.providerdo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.URPContract.Buttons;
import com.doubtech.universalremote.providers.URPContract.Models;

public class Model {
    protected String mAuthority;
    protected String mBrandId;
    protected String mModelId;
    protected String mName;
    protected int mModelIdentifier;

    protected Model() {

    }

    public Model(String authority, String brandId, String modelId) {
        mAuthority = authority;
        mBrandId = brandId;
        mModelId = modelId;
        mModelId = modelId;
    }

    public static Model fromUri(Uri uri) {
        Model model = new Model();
        model.mAuthority = uri.getAuthority();
        model.mBrandId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_BRANDID);
        model.mModelId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_MODELID);
        return model;
    }

    public static Model fromCursor(Context context, String authority, Cursor cursor) {
        Model model = new Model();
        int idx;
        idx = cursor.getColumnIndex(Models.COLUMN_AUTHORITY);
        if (idx >= 0) {
            model.mAuthority = cursor.getString(idx);
        } else {
            model.mAuthority = authority;
        }

        idx = cursor.getColumnIndex(Models.COLUMN_BRAND_ID);
        if (idx >= 0) {
            model.mBrandId = cursor.getString(idx);
        }

        idx = cursor.getColumnIndex(Models.COLUMN_MODEL_ID);
        if (idx >= 0) {
            model.mModelId = cursor.getString(idx);
        }

        idx = cursor.getColumnIndex(Models.COLUMN_NAME);
        if (idx >= 0) {
            model.mName = cursor.getString(idx);
        }

        return model;
    }

    public static Model[] fromJson(Context context, String authority, String json) throws JSONException {
        if (null == json || json.length() == 0) return new Model[0];
        JSONObject obj = new JSONObject(json);
        JSONArray array = obj.getJSONArray("models");
        Model[] models = new Model[array.length()];
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            Model model = new Model();

            model.mAuthority = authority;
            model.mBrandId = obj.getString("brandId");
            model.mModelId = obj.getString("modelId");
            model.mModelId = obj.getString("modelId");
            model.mName = obj.getString("modelName");
            models[i] = model;
        }
        return models;
    }

    public Object[] toRow() {
        Object[] row = new Object[URPContract.Models.ALL.length];
        row[Buttons.COLIDX_ID] = getModelId().hashCode();
        row[Models.COLIDX_AUTHORITY] = getAuthority();
        row[Models.COLIDX_BRAND_ID] = getBrandId();
        row[Models.COLIDX_MODEL_ID] = getModelId();
        row[Models.COLIDX_NAME] = getName();
        return row;
    }

    public String getName() {
        return mName;
    }

    public String getModelId() {
        return mModelId;
    }

    public String getBrandId() {
        return mBrandId;
    }

    public String getAuthority() {
        return mAuthority;
    }

    @Override
    public int hashCode() {
        return mBrandId.hashCode() | mModelId.hashCode() | mModelId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Model) {
            Model b = (Model) o;
            return (mBrandId == b.mBrandId || mBrandId.equals(b.mBrandId)) &&
                    (mModelId == b.mModelId || mModelId.equals(b.mModelId));
        }
        return false;
    }
}

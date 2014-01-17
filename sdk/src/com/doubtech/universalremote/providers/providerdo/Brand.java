package com.doubtech.universalremote.providers.providerdo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.URPContract.Brands;
import com.doubtech.universalremote.providers.URPContract.Buttons;

public class Brand {
    protected String mAuthority;
    protected String mBrandId;
    protected String mName;

    protected Brand() {

    }

    public Brand(String authority, String brandId) {
        mAuthority = authority;
        mBrandId = brandId;
    }

    public static Brand fromUri(Uri uri) {
        Brand brand = new Brand();
        brand.mAuthority = uri.getAuthority();
        brand.mBrandId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_BRANDID);
        return brand;
    }

    public static Brand fromCursor(Context context, String authority, Cursor cursor) {
        Brand brand = new Brand();
        int idx;
        idx = cursor.getColumnIndex(Brands.COLUMN_AUTHORITY);
        if (idx >= 0) {
            brand.mAuthority = cursor.getString(idx);
        } else {
            brand.mAuthority = authority;
        }

        idx = cursor.getColumnIndex(Brands.COLUMN_BRAND_ID);
        if (idx >= 0) {
            brand.mBrandId = cursor.getString(idx);
        }

        idx = cursor.getColumnIndex(Brands.COLUMN_NAME);
        if (idx >= 0) {
            brand.mName = cursor.getString(idx);
        }

        return brand;
    }

    public static Brand[] fromJson(Context context, String authority, String json) throws JSONException {
        if (null == json || json.length() == 0) return new Brand[0];
        JSONObject obj = new JSONObject(json);
        JSONArray array = obj.getJSONArray("brands");
        Brand[] brands = new Brand[array.length()];
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            Brand brand = new Brand();

            brand.mAuthority = authority;
            brand.mBrandId = obj.getString("brandId");
            brand.mName = obj.getString("brandName");
            brands[i] = brand;
        }
        return brands;
    }

    public Object[] toRow() {
        Object[] row = new Object[URPContract.Brands.ALL.length];
        row[Buttons.COLIDX_ID] = getBrandId().hashCode();
        row[Brands.COLIDX_AUTHORITY] = getAuthority();
        row[Brands.COLIDX_BRAND_ID] = getBrandId();
        row[Brands.COLIDX_NAME] = getName();
        return row;
    }

    public String getName() {
        return mName;
    }

    public String getBrandId() {
        return mBrandId;
    }

    public String getAuthority() {
        return mAuthority;
    }

    @Override
    public int hashCode() {
        return mBrandId.hashCode() | mBrandId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Brand) {
            Brand b = (Brand) o;
            return (mBrandId == b.mBrandId || mBrandId.equals(b.mBrandId)) &&
                    mBrandId.equals(b.mBrandId);
        }
        return false;
    }
}

package com.doubtech.universalremote.providers.providerdo;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.net.Uri;

import com.doubtech.universalremote.providers.AbstractJsonUniversalRemoteProvider;
import com.doubtech.universalremote.providers.BaseAbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.URPContract.Buttons;
import com.doubtech.universalremote.utils.ButtonIdentifier;

public class Button {
	protected String mAuthority;
	protected String mBrandId;
	protected String mModelId;
	protected String mButtonId;
	protected String mName = "";
	protected int mButtonIdentifier = 0;
	protected HashMap<String, String> mInternalData = new HashMap<String, String>();
	
	protected Button() {

	}

	public Button(String authority, String brandId, String modelId, String buttonId) {
		mAuthority = authority;
		mBrandId = brandId;
		mModelId = modelId;
		mButtonId = buttonId;
	}

	public static Button fromUri(Uri uri) {
		Button button = new Button();
		button.mAuthority = uri.getAuthority();
		button.mBrandId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_BRANDID);
		button.mModelId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_MODELID);
		button.mButtonId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_BUTTON_ID);
		return button;
	}
	
	public static Button fromCursor(BaseAbstractUniversalRemoteProvider provider, String authority, Cursor cursor) {
		Button button = new Button();
		int idx;
		idx = cursor.getColumnIndex(Buttons.COLUMN_AUTHORITY);
		if(idx >= 0) {
			button.mAuthority = cursor.getString(idx);
		} else {
			button.mAuthority = authority;
		}

		idx = cursor.getColumnIndex(provider.getButtonsColNameBrandId());
		if(idx >= 0) {
			button.mBrandId = cursor.getString(idx);
		}

		idx = cursor.getColumnIndex(provider.getButtonsColNameModelId());
		if(idx >= 0) {
			button.mModelId = cursor.getString(idx);
		}

		idx = cursor.getColumnIndex(provider.getButtonsColNameId());
		if(idx >= 0) {
			button.mButtonId = cursor.getString(idx);
		}

		idx = cursor.getColumnIndex(provider.getButtonsColNameButtonName());
		if(idx >= 0) {
			button.mName = ButtonIdentifier.getLabel(provider.getContext().getResources(), cursor.getString(idx));
		}

		idx = cursor.getColumnIndex(provider.getButtonsColNameButtonIdentifier());
		if(idx >= 0) {
			button.mButtonIdentifier = ButtonIdentifier.getKnownButton(cursor.getString(idx));
		}

		return button;
	}

	public static Button[] fromJson(AbstractJsonUniversalRemoteProvider provider, String authority, String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		JSONArray array = obj.getJSONArray("buttons");
		Button[] buttons = new Button[array.length()];
		for(int i = 0; i < array.length(); i++) {
			obj = array.getJSONObject(i);
			Button button = new Button();
			
			button.mAuthority = authority;
			button.mBrandId = obj.getString("brandId");
			button.mModelId = obj.getString("modelId");
			button.mButtonId = obj.getString("buttonId");
			button.mName = ButtonIdentifier.getLabel(provider.getContext().getResources(), obj.getString("buttonName"));
			button.mButtonIdentifier = ButtonIdentifier.getKnownButton(obj.getString("buttonName"));
			provider.onPutExtras(button, obj);
		}
		return buttons;
	}

	public Object[] toRow() {
		Object[] row = new Object[URPContract.Buttons.ALL.length];
		row[Buttons.COLIDX_AUTHORITY] = getAuthority();
		row[Buttons.COLIDX_BRAND_ID] = getBrandId();
		row[Buttons.COLIDX_MODEL_ID] = getModelId();
		row[Buttons.COLIDX_ID] = getButtonId();
		row[Buttons.COLIDX_NAME] = getName();
		row[Buttons.COLIDX_BUTTON_IDENTIFIER] = getButtonIdentifier();
		return row;
	}

	public int getButtonIdentifier() {
		return mButtonIdentifier;
	}

	public String getName() {
		return mName;
	}

	public String getButtonId() {
		return mButtonId;
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

	/**
	 * Extra data that is private to the local provider that might be needed
	 * to send the button
	 * @return
	 */
	public String getInternalData(String name) {
		return mInternalData.get(name);
	}
	
	public Button putExtra(String name, String data) {
		mInternalData.put(name, data);
		return this;
	}

	@Override
	public int hashCode() {
		return mBrandId.hashCode() | mModelId.hashCode() | mButtonId.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Button) {
			Button b = (Button) o;
			return (mBrandId == b.mBrandId || mBrandId.equals(b.mBrandId)) &&
				    (mModelId == b.mModelId || mModelId.equals(b.mModelId)) &&
					mButtonId.equals(b.mButtonId);
		}
		return false;
	}
}

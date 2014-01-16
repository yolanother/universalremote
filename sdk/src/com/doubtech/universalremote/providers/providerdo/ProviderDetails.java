package com.doubtech.universalremote.providers.providerdo;

import com.doubtech.universalremote.providers.URPContract;

import android.database.Cursor;

public class ProviderDetails {
	private String mAuthority;
	private String mName;
	private String mDescription;
	private boolean mIsEnabled;
	
	public String getDescription() {
		return mDescription;
	}
	
	public String getName() {
		return mName;
	}
	
	public boolean isEnabled() {
		return mIsEnabled;
	}

	public String getAuthority() {
		return mAuthority;
	}
	
	public static ProviderDetails fromCursor(Cursor cursor) {
		ProviderDetails details = new ProviderDetails();
		details.mAuthority = cursor.getString(URPContract.ProviderDetails.COLIDX_AUTHORITY);
		details.mName = cursor.getString(URPContract.ProviderDetails.COLIDX_NAME);
		details.mDescription = cursor.getString(URPContract.ProviderDetails.COLIDX_DESCRIPTION);
		details.mIsEnabled = cursor.getInt(URPContract.ProviderDetails.COLIDX_IS_ENABLED) == 1;
		return details;
	}
	
	@Override
	public String toString() {
		return mName + " (" + mAuthority + ":" + (mIsEnabled ? "enabled" : "disabled") + ")\n    " + mDescription;
	}
}

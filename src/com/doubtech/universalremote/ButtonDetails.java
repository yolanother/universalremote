package com.doubtech.universalremote;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.providers.AbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.URPContract;

public class ButtonDetails implements Parcelable {
	
	
	private String mAuthority;
	private String mId;
	private String mLabel;
	private int mButtonIdentifier;
	private transient Bitmap mButtonIcon;
	private transient ConcurrentLinkedQueue<IconLoaderListener> mLoadedListeners = new ConcurrentLinkedQueue<IconLoaderListener>();

	public ButtonDetails(String authority, Cursor buttonCursor) {
		mAuthority = authority;
		mId = buttonCursor.getString(URPContract.Buttons.COLIDX_ID);
		mLabel = buttonCursor.getString(URPContract.Buttons.COLIDX_NAME);
		mButtonIdentifier = buttonCursor.getInt(URPContract.Buttons.COLIDX_BUTTON_IDENTIFIER);

		// Button identifier wasn't set so let's try one more time to get it
		if(0 == mButtonIdentifier && null != mLabel) {
			mButtonIdentifier = ButtonIdentifier.getKnownButton(mLabel);
		}
	}
	
	public ButtonDetails(Parcel parcel) {
		mAuthority = parcel.readString();
		mId = parcel.readString();
		mLabel = parcel.readString();
	}

	public void getIcon(final Context context, final IconLoaderListener listener) {
		if(null == mButtonIcon) {
			AbstractUniversalRemoteProvider.loadIcon(context, mAuthority, mId, new IconLoaderListener() {
				@Override
				public void onIconLoaded(Bitmap bitmap) {
					mButtonIcon = bitmap;
					listener.onIconLoaded(bitmap);
					while(mLoadedListeners.size() > 0) {
						mLoadedListeners.poll().onIconLoaded(bitmap);
					}
				}
			});
		} else {
			listener.onIconLoaded(mButtonIcon);
		}
	}

	public String getAuthority() {
		return mAuthority;
	}

	public String getId() {
		return mId;
	}

	public String getLabel() {
		return mLabel;
	}
	
	public int getButtonIdentifier() {
		return mButtonIdentifier;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mAuthority);
		dest.writeString(mId);
		dest.writeString(mLabel);
	}

    public static final Parcelable.Creator<ButtonDetails> CREATOR = new Parcelable.Creator<ButtonDetails>() {
        public ButtonDetails createFromParcel(Parcel in) {
            return new ButtonDetails(in); 
        }

        public ButtonDetails[] newArray(int size) {
            return new ButtonDetails[size];
        }
    };
}

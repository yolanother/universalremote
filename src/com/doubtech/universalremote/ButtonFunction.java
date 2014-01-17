package com.doubtech.universalremote;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.providers.BaseAbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.utils.ButtonIdentifier;

public class ButtonFunction extends ButtonFunctionSet implements Parcelable {
    private static final long serialVersionUID = 1L;
    private String mAuthority;
    private String mBrandId;
    private String mModelId;
    private String mId;
    private String mLabel;
    private int mButtonIdentifier;
    private transient Bitmap mButtonIcon;
    private transient ConcurrentLinkedQueue<IconLoaderListener> mLoadedListeners = new ConcurrentLinkedQueue<IconLoaderListener>();

    public ButtonFunction(Context context, String authority, Cursor buttonCursor) {
        mAuthority = authority;
        mBrandId = buttonCursor.getString(URPContract.Buttons.COLIDX_BRAND_ID);
        mModelId = buttonCursor.getString(URPContract.Buttons.COLIDX_MODEL_ID);
        mId = buttonCursor.getString(URPContract.Buttons.COLIDX_BUTTON_ID);
        mLabel = buttonCursor.getString(URPContract.Buttons.COLIDX_NAME);

        // TODO This is ugly and the label object isn't really needed anymore
        mLabel = ButtonIdentifier.getLabel(context.getResources(), mLabel);
        mButtonIdentifier = buttonCursor.getInt(URPContract.Buttons.COLIDX_BUTTON_IDENTIFIER);

        // Button identifier wasn't set so let's try one more time to get it
        if (0 == mButtonIdentifier && null != mLabel) {
            mButtonIdentifier = ButtonIdentifier.getKnownButton(mLabel);
        }
    }

    public ButtonFunction(Parcel parcel) {
        mAuthority = parcel.readString();
        mBrandId = parcel.readString();
        mModelId = parcel.readString();
        mId = parcel.readString();
        mLabel = parcel.readString();
        mButtonIdentifier = parcel.readInt();
    }

    ButtonFunction() {
        // This will be set up by a builder/from method
    }

    public void getIcon(final Context context, final IconLoaderListener listener) {
        if (null == mButtonIcon) {
            BaseAbstractUniversalRemoteProvider.loadIcon(context, mAuthority, mId, new IconLoaderListener() {
                @Override
                public void onIconLoaded(Bitmap bitmap) {
                    mButtonIcon = bitmap;
                    listener.onIconLoaded(bitmap);
                    while (mLoadedListeners.size() > 0) {
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
        dest.writeString(mBrandId);
        dest.writeString(mModelId);
        dest.writeString(mId);
        dest.writeString(mLabel);
        dest.writeInt(mButtonIdentifier);
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public static final Parcelable.Creator<ButtonFunction> CREATOR = new Parcelable.Creator<ButtonFunction>() {
        public ButtonFunction createFromParcel(Parcel in) {
            return new ButtonFunction(in);
        }

        public ButtonFunction[] newArray(int size) {
            return new ButtonFunction[size];
        }
    };
    public static final String XMLTAG = "button-function";

    @Override
    public void writeXml(XmlSerializer xml) throws IllegalArgumentException, IllegalStateException, IOException {
        xml.startTag("", XMLTAG);
        xml.attribute("", "brandId", mId);
        xml.attribute("", "modelId", mId);
        xml.attribute("", "id", mId);
        xml.attribute("", "authority", mAuthority);
        xml.attribute("", "label", mLabel);
        xml.endTag("", XMLTAG);

        super.writeXml(xml);
    }

    public static ButtonFunction fromXml(Context context, Element item) {
        ButtonFunction details = new ButtonFunction();
        details.mBrandId = item.getAttribute("brandId");
        details.mModelId = item.getAttribute("modelId");
        details.mId = item.getAttribute("id");
        details.mAuthority = item.getAttribute("authority");
        details.mLabel = item.getAttribute("label");
        return details;
    }

    public String getBrandId() {
        return mBrandId;
    }

    public String getModelId() {
        return mModelId;
    }
}

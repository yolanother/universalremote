package com.doubtech.universalremote.providers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.providers.URPContract.Brands;
import com.doubtech.universalremote.providers.URPContract.Buttons;
import com.doubtech.universalremote.providers.URPContract.Models;
import com.doubtech.universalremote.providers.providerdo.ProviderDetails;
import com.doubtech.universalremote.utils.ButtonIdentifier;
import com.doubtech.universalremote.utils.ButtonIds;

/**
 * @hide
 * @author a1.jackson
 *
 */
public abstract class BaseAbstractUniversalRemoteProvider extends ContentProvider {
    private UriMatcher mUriMatcher;

    public BaseAbstractUniversalRemoteProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(getAuthority(), URPContract.TABLE_BRANDS_PATH, URPContract.TABLE_BRANDS);
        mUriMatcher.addURI(getAuthority(), URPContract.TABLE_MODELS_PATH, URPContract.TABLE_MODELS);
        mUriMatcher.addURI(getAuthority(), URPContract.TABLE_BUTTONS_PATH, URPContract.TABLE_BUTTONS);
        mUriMatcher.addURI(getAuthority(), URPContract.TABLE_BUTTONS_PATH + "/*", URPContract.TABLE_BUTTONS);
        mUriMatcher.addURI(getAuthority(), URPContract.TABLE_BUTTON_LAYOUT_PATH, URPContract.TABLE_BUTTON_LAYOUT);
        mUriMatcher.addURI(getAuthority(), URPContract.TABLE_PROVIDER_DETAILS_PATH, URPContract.TABLE_PROVIDER_DETAILS);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode)
            throws FileNotFoundException {
        switch (mUriMatcher.match(uri)) {
        case URPContract.TABLE_BUTTONS:
            List<String> buttons = uri.getQueryParameters(URPContract.QUERY_PARAMETER_BUTTON_ID);
            if (buttons.size() == 0) {
                throw new FileNotFoundException("No button id specified.");
            }
            return openButtonIconAsset(getButtons(uri, null));
        }
        return null;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        switch (mUriMatcher.match(uri)) {
            case URPContract.TABLE_BUTTONS:
                List<String> buttons = uri.getQueryParameters(URPContract.QUERY_PARAMETER_BUTTON_ID);
                if (buttons.size() == 0) {
                    throw new FileNotFoundException("No button id specified.");
                }
                return openButtonIcon(getButtons(uri, null));
        }
        return null;
    }

    public ParcelFileDescriptor openButtonIcon(Cursor button) {
        return null;
    }

    public AssetFileDescriptor openButtonIconAsset(Cursor button) {
        button.moveToFirst();
        String name = button.getString(URPContract.Buttons.COLIDX_NAME);
        int iconId = ButtonIdentifier.getIconId(name);
        try {
            if (0 != iconId) {
                return getContext()
                        .getResources()
                        .openRawResourceFd(iconId);
            }
        } catch (Exception e) {
            Log.d("UniversalRemote", "Could not open icon file.", e);
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
    	Log.d("AARON", "Running query for " + uri + ": " + mUriMatcher.match(uri));
        switch(mUriMatcher.match(uri)) {
        case URPContract.TABLE_BRANDS:
            return getBrands(uri);
        case URPContract.TABLE_MODELS:
            return getModels(uri);
        case URPContract.TABLE_BUTTONS:
            if (URPContract.BUTTON_COMMAND_SEND.equals(uri.getLastPathSegment())) {
                if (uri.getQueryParameters(URPContract.QUERY_PARAMETER_BUTTON_ID).size() == 0) {
                    throw new IllegalArgumentException("No buttons were selected.");
                }
                return sendButtons(getButtons(uri, selectionArgs));
            }
            return getButtons(uri, selectionArgs);
        case URPContract.TABLE_BUTTON_LAYOUT:
            return getButtonLayouts(uri);
        case URPContract.TABLE_PROVIDER_DETAILS:
        	Log.d("AARON", "Getting details for " + getAuthority());
        	MatrixCursor cursor = new MatrixCursor(URPContract.ProviderDetails.ALL);
        	cursor.addRow(new Object[] {
        		getAuthority(),
        		getProviderName(),
        		getProviderDescription(),
        		isProviderEnabled() ? 1 : 0
        	});
        	cursor.moveToFirst();
        	return cursor;
        default:
             throw new IllegalArgumentException("Invalid uri " + uri.toString());
        }
    }
    
    public abstract String getProviderName();
    public abstract String getProviderDescription();
    public abstract boolean isProviderEnabled();

    public abstract Cursor sendButtons(Cursor buttons);

    private Cursor getButtonLayouts(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    private Cursor getButtons(Uri uri, String[] selections) {
        List<String> buttons = uri.getQueryParameters(URPContract.QUERY_PARAMETER_BUTTON_ID);
        if (null != buttons && buttons.size() > 0) {
            selections = buttons.toArray(new String[buttons.size()]);
        }
        String modelId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_BRANDID);
        String remoteId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_MODELID);
        Cursor cursor = getButtons(new String[] {
                "'" + getAuthority() + "' as " + URPContract.Buttons.COLUMN_AUTHORITY,
                getButtonsColNameId() + " as " + URPContract.Buttons.COLUMN_ID,
                getButtonsColNameModelId() + " as " + URPContract.Buttons.COLUMN_MODEL_ID,
                getButtonsColNameButtonName() + " as " + URPContract.Buttons.COLUMN_NAME,
                getButtonsColNameButtonIdentifier() + " as " + URPContract.Buttons.COLUMN_BUTTON_IDENTIFIER
            },
            modelId,
            remoteId,
            selections,
            null);
        cursor.moveToFirst();
        return cursor;
    }

    public String getButtonsColNameButtonIdentifier() {
        return Buttons.COLUMN_BUTTON_IDENTIFIER;
    }

    public String getButtonsColNameButtonName() {
    	return Buttons.COLUMN_NAME;
    }

	public String getButtonsColNameBrandId() {
		return Buttons.COLUMN_BRAND_ID;
	}

    public String getButtonsColNameModelId() {
    	return Buttons.COLUMN_MODEL_ID;
    }

    public String getButtonsColNameId() {
    	return Buttons.COLUMN_ID;
    }

    protected abstract Cursor getButtons(String[] projection, String modelId, String remoteId, String[] buttons, String sortOrder);

    private Cursor getModels(Uri uri) {
        String brandId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_BRANDID);
        return getModels(new String[] {
                "'" + getAuthority() + "' as " + URPContract.Models.COLUMN_AUTHORITY,
                getModelColNameId() + " as " + URPContract.Models.COLUMN_ID,
                getModelColNameBrandId() + " as " + URPContract.Models.COLUMN_BRAND_ID,
                getModelColNameModelName() + " as " + URPContract.Models.COLUMN_NAME
            },
            brandId,
            null,
            null);
    }

    private Cursor getBrands(Uri uri) {
        return getBrands(new String[] {
                "'" + getAuthority() + "' as " + URPContract.Brands.COLUMN_AUTHORITY,
                getBrandColNameId() + " as " + URPContract.Brands.COLUMN_ID,
                getBrandColNameBrandName() + " as " + URPContract.Brands.COLUMN_NAME,
                getBrandColNameLogo() + " as " + URPContract.Brands.COLUMN_LOGO
            },
            null,
            null,
            null);
    }

    protected String getBrandColNameLogo() {
        return "null";
    }

    public String getBrandColNameBrandName() {
    	return Brands.COLUMN_NAME;
    }

    public String getBrandColNameId() {
    	return Brands.COLUMN_ID;
    }

    protected abstract Cursor getBrands(String[] projection, String selection, String[] selectionArgs, String sortOrder);

    public String getModelColNameModelName() {
    	return Models.COLUMN_NAME;
    }

    public  String getModelColNameId() {
    	return Models.COLUMN_ID;
    }

    public String getModelColNameBrandId() {
    	return Models.COLUMN_BRAND_ID;
    }

    protected abstract Cursor getModels(String[] projection, String selection, String[] selectionArgs, String sortOrder);

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public abstract String getAuthority();

    public static Cursor queryButtons(Context context, String authority, String brandId, String modelId) {
        return context.getContentResolver().query(
                        URPContract.getButtonsUri(authority).buildUpon()
                        .appendQueryParameter(URPContract.QUERY_PARAMETER_BRANDID, brandId)
                        .appendQueryParameter(URPContract.QUERY_PARAMETER_MODELID, modelId)
                            .build(),
                        null,
                        null,
                        null,
                        null);
    }

    public static Cursor queryModels(Context context, String authority,
            String brandId) {
        return context.getContentResolver().query(
                URPContract.getModelsUri(authority).buildUpon()
                .appendQueryParameter(URPContract.QUERY_PARAMETER_BRANDID, brandId)
                .build(),
            null,
            null,
            null,
            null);
    }

	public static Cursor queryBrands(Context context, String authority) {
        return context.getContentResolver().query(
                URPContract.getBrandsUri(authority),
            null,
            null,
            null,
            null);
	}
    
    public static ProviderDetails queryProviderDetails(Context context, String authority) {
    	return ProviderDetails.fromCursor(context.getContentResolver().query(
    			URPContract.getProviderDetailsUri(authority),
    			null,
    			null,
    			null,
    			null));
    }

    public static Cursor sendButton(Context context, String authority, String brandId, String modelId, String id) {
        return context.getContentResolver().query(
                URPContract.getButtonsUri(authority).buildUpon()
                .appendEncodedPath(URPContract.BUTTON_COMMAND_SEND)
                .appendQueryParameter(URPContract.QUERY_PARAMETER_BRANDID, brandId)
                .appendQueryParameter(URPContract.QUERY_PARAMETER_MODELID, modelId)
                .appendQueryParameter(URPContract.QUERY_PARAMETER_BUTTON_ID, id).build(),
            new String[] { id },
            null,
            null,
            null);
    }

    private static ConcurrentHashMap<String, Bitmap> mIconCache = new ConcurrentHashMap<String, Bitmap>();
    public static void loadIcon(final Context context, final String authority, final String id,
            final IconLoaderListener iconLoaderListener) {
        Bitmap mButtonIcon = mIconCache.get(authority + "." + id);
        if (null == mButtonIcon) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        AssetFileDescriptor desc = context.getContentResolver().openAssetFileDescriptor(URPContract.getButtonUri(authority, id), "r");
                        if (null != desc) {
                            Bitmap mButtonIcon = BitmapFactory.decodeStream(desc.createInputStream());
                            if (null != mButtonIcon) {
                                mIconCache.put(authority + "." + id, mButtonIcon);
                                iconLoaderListener.onIconLoaded(mButtonIcon);
                                return;
                            }
                        }
                        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(URPContract.getButtonUri(authority, id), "r");
                        if (null != pfd) {
                            Bitmap mButtonIcon = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
                            if (null != mButtonIcon) {
                                mIconCache.put(authority + "." + id, mButtonIcon);
                            }
                            iconLoaderListener.onIconLoaded(mButtonIcon);
                        }
                    } catch (IOException e) {
                        Log.w("ButtonDetails", e.getMessage(), e);
                    }
                }
            }.start();
        } else {
            iconLoaderListener.onIconLoaded(mButtonIcon);
        }
    }
}

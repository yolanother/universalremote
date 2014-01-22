package com.doubtech.universalremote.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.providers.URPContract.Buttons;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.providers.providerdo.ProviderDetails;
import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;

/**
 * @hide
 * @author a1.jackson
 *
 */
public abstract class AbstractUniversalRemoteProvider extends ContentProvider {
    private UriMatcher mUriMatcher;

    public AbstractUniversalRemoteProvider() {
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
            Parent button = Parent.fromUri(uri);
            return openButtonIconAsset(button);
        }
        return null;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        switch (mUriMatcher.match(uri)) {
            case URPContract.TABLE_BUTTONS:
                Parent button = Parent.fromUri(uri);
                return openButtonIcon(button);
        }
        return null;
    }

    ParcelFileDescriptor openButtonIcon(Parent button) throws FileNotFoundException {
        File file = getIconFile(button);
        if (null != file) {
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        }
        return null;
    }

    /**
     * Gets a resource id for the icon for a button.
     * @param button The button that needs a label
     * @return Returns 0 if there is no resource icon for this button or the R.drawable id
     */
    public int getIconId(Parent button) {
        return 0;
    }

    /**
     * Gets a file path for an image to be used as a button label.
     *
     * NOTE:
     * This will be passed through the provider system so the icon can be in the
     * application's data directory.
     * @param button
     * @return Returns null if no file is found for this button or the path to the file
     */
    public File getIconFile(Parent button) {
        return null;
    }

    AssetFileDescriptor openButtonIconAsset(Parent button) {
        int iconId = getIconId(button);
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

    private String[] getSegments(Uri uri) {
        List<String> fullSegments = uri.getPathSegments();
        String[] segments = new String[fullSegments.size() - 1];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = fullSegments.get(i + 1);
        }
        return segments;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        List<String> segments = uri.getPathSegments();
        String table = segments.get(0);
        if (URPContract.TABLE_PROVIDER_DETAILS_PATH.equals(table)) {
            MatrixCursor cursor = new MatrixCursor(URPContract.ProviderDetails.ALL);
            cursor.addRow(new Object[] {
                getAuthority(),
                getProviderName(),
                getProviderDescription(),
                isProviderEnabled() ? 1 : 0
            });
            cursor.moveToFirst();
            return cursor;
        } else if (URPContract.TABLE_BUTTONS_PATH.equals(table)) {
            Parent[] nodes = get(Parent.fromUri(uri));
            return getCursor(nodes);
        } else if (URPContract.BUTTON_COMMAND_SEND.equals(table)) {
            Parent[] nodes = get(Parent.fromUri(uri));
            List<Button> buttons = new ArrayList<Button>();
            for (Parent node : nodes) {
                if (node instanceof Button) {
                    buttons.add((Button) node);
                }
            }
            sendButtons(buttons.toArray(new Button[0]));
            return getCursor(nodes);
        }

            /*if (URPContract.BUTTON_COMMAND_SEND.equals(uri.getLastPathSegment())) {
                if (uri.getQueryParameters(URPContract.QUERY_PARAMETER_BUTTON_ID).size() == 0) {
                    throw new IllegalArgumentException("No buttons were selected.");
                }
                List<String> buttonIds = ;
                String brandId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_BRANDID);
                String modelId = uri.getQueryParameter(URPContract.QUERY_PARAMETER_MODELID);
                Button[] buttons = new Button[buttonIds.size()];
                for (int i = 0; i < buttons.length; i++) {
                    buttons[i] = new Button(
                            getAuthority(),
                            brandId,
                            modelId,
                            buttonIds.get(i));
                }

                MatrixCursor cursor = new MatrixCursor(Buttons.ALL);
                for (Button button : sendButtons(buttons)) {
                    cursor.addRow(button.toRow());
                }
                return cursor;
            }
            return getButtons(uri, selectionArgs);*/
        throw new IllegalArgumentException("Unknown query: " + uri);
    }

    private Cursor getCursor(Parent[] nodes) {
        MatrixCursor cursor = new MatrixCursor(nodes[0].getColumns());
        for (Parent node : nodes) {
            cursor.addRow(node.toRow());
        }
        return cursor;
    }

    public abstract Parent[] get(Parent parent);

    public abstract String getProviderName();
    public abstract String getProviderDescription();
    public abstract boolean isProviderEnabled();

    public abstract Button[] sendButtons(Button[] buttons);

    public String getButtonsColNameButtonIdentifier() {
        return Buttons.COLUMN_BUTTON_IDENTIFIER;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public abstract String getAuthority();

    public static Cursor query(Context context, Parent parent) {
        return context.getContentResolver().query(
                        parent.getUri(),
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

    public static Cursor sendButton(Context context, Button button) {
        return context.getContentResolver().query(
                URPContract.getUri(button.getAuthority(), URPContract.BUTTON_COMMAND_SEND, button.getPath()),
            null,
            null,
            null,
            null);
    }

    private static ConcurrentHashMap<String, Bitmap> mIconCache = new ConcurrentHashMap<String, Bitmap>();
    private static DiskLruCache sIconDiskCache;

    private static String getIconCacheKey(Button button) {
        return (button.getAuthority() + "." +
                button.getPathString()
                ).replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    public static void loadIcon(final Context context, final Button button,
            final IconLoaderListener iconLoaderListener) {
        if (null == sIconDiskCache) {
            try {
                File cache = new File(context.getCacheDir(), "iconCache");
                cache.mkdirs();
                sIconDiskCache = DiskLruCache.open(
                        cache,
                        1,
                        1,
                        1024 * 1024 * 2 /* 2MB */);
            } catch (IOException e) {
                Log.w("UniversalRemote", "Icon caching unavailable: " + e.getMessage());
            }
        }
        final String key = getIconCacheKey(button);
        Bitmap mButtonIcon = mIconCache.get(key);
        if (null == mButtonIcon) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Uri uri = button.getUri();
                        Snapshot snapshot = sIconDiskCache.get(key);
                        if (null == sIconDiskCache || null == snapshot) {

                            AssetFileDescriptor desc = context.getContentResolver().openAssetFileDescriptor(uri, "r");
                            if (null != desc) {
                                Bitmap mButtonIcon = BitmapFactory.decodeStream(desc.createInputStream());
                                saveIconToCache(mButtonIcon, key);
                                if (null != mButtonIcon) {
                                    mIconCache.put(key, mButtonIcon);
                                    iconLoaderListener.onIconLoaded(mButtonIcon);
                                    return;
                                }
                            }
                            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(button.getUri(), "r");
                            if (null != pfd) {
                                Bitmap mButtonIcon = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
                                saveIconToCache(mButtonIcon, key);
                                if (null != mButtonIcon) {
                                    mIconCache.put(key, mButtonIcon);
                                }
                                iconLoaderListener.onIconLoaded(mButtonIcon);
                            }
                        } else {
                            Bitmap icon = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                            if (null != icon) {
                                mIconCache.put(key, icon);
                            }
                            iconLoaderListener.onIconLoaded(icon);
                        }
                    } catch (IOException e) {
                        Log.w("ButtonDetails", e.getMessage(), e);
                    }
                }

                private void saveIconToCache(Bitmap icon, String key) throws IOException {
                    if (null != sIconDiskCache && null != icon) {
                        Editor editor = sIconDiskCache.edit(key);
                        OutputStream stream = editor.newOutputStream(0);
                        icon.compress(CompressFormat.PNG, 9, stream);
                        stream.close();
                        editor.commit();
                    }
                }
            }.start();
        } else {
            iconLoaderListener.onIconLoaded(mButtonIcon);
        }
    }
}

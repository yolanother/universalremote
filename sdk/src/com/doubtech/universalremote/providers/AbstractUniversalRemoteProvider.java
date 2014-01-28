package com.doubtech.universalremote.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.doubtech.universalremote.providers.URPContract.Buttons;
import com.doubtech.universalremote.providers.URPContract.Parents;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.utils.StringUtils;

/**
 * @hide
 * @author a1.jackson
 *
 */
public abstract class AbstractUniversalRemoteProvider extends ContentProvider {
    private static final String TAG = "AbstractUniversalRemoteProvicer";
    private static final boolean DEBUG = false;
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

    @SuppressWarnings("unused")
    @Override
    public synchronized AssetFileDescriptor openAssetFile(Uri uri, String mode)
            throws FileNotFoundException {
        List<String> segments = uri.getPathSegments();
        String table = segments.get(0);
        if (URPContract.TABLE_BUTTONS_PATH.equals(table)) {
            Parent nodes = getCachedDetails(Parent.fromUri(uri));

            if (!nodes.needsToFetch() && DEBUG) {
                synchronized(this) {
                    Log.d(TAG, "[ASSET] ============================");
                    Log.d(TAG, "[ASSET] " + StringUtils.implode("/", uri.getPathSegments()));
                    Log.d(TAG, "[ASSET] /" + StringUtils.implode("/", nodes.getPath()));
                    Log.d(TAG, "[ASSET] " + nodes.getName());
                    Log.d(TAG, "[ASSET] " + getIconId(nodes));
                    Log.d(TAG, "[ASSET] ============================\n\n");
                }
                return openButtonIconAsset(nodes);
            }
        }
        return null;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        List<String> segments = uri.getPathSegments();
        String table = segments.get(0);
        if (URPContract.TABLE_BUTTONS_PATH.equals(table)) {
            Parent[] nodes = getChildren(Parent.fromUri(uri));

            if (nodes.length > 0) {
                return openButtonIcon(nodes[0]);
            }
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
    public Cursor query(final Uri uri, String[] projection, String selection,
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
            Parent[] nodes = getChildren(Parent.fromUri(uri));
            return getCursor(nodes);
        } else if (URPContract.BUTTON_COMMAND_SEND.equals(table)) {
            // TODO Might want to replace with a single thread executor.
            new Thread() {
                public void run() {
                    Button button = (Button) getDetails(Parent.fromUri(uri));
                    sendButtons(new Button[] { button });
                }
            }.start();
            return new MatrixCursor(new String[] {"Sent"});
        }
        throw new IllegalArgumentException("Unknown query: " + uri);
    }

    private Cursor getCursor(Parent[] nodes) {
        MatrixCursor cursor;
        if (nodes.length > 0) {
            cursor = new MatrixCursor(nodes[0].getColumns());
            for (Parent node : nodes) {
                cursor.addRow(node.toRow());
            }
        } else {
            cursor = new MatrixCursor(Parents.ALL);
        }
        cursor.moveToFirst();
        return cursor;
    }
    /**
     * If this node's details haven't been loaded yet it's details will be populated
     * here.
     * @param parent
     * @return
     */
    public Parent getCachedDetails(Parent parent) {
        parent = Parent.getCached(parent);
        if (!parent.needsToFetch()) {
            return parent;
        }

        return getDetails(parent);
    }

    public abstract Parent[] getChildren(Parent parent);
    /**
     * If this node's details haven't been loaded yet it's details will be populated
     * here.
     * @param parent
     * @return
     */
    public abstract Parent getDetails(Parent parent);

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

    public boolean hasButtonSets(Parent node) {
        return false;
    }

    public String getDescription(Parent node) {
        return null;
    }
}

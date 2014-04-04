package com.doubtech.universalremote.providers;

import java.io.File;
import java.io.FileNotFoundException;
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
    private static final String TAG = "AbstractUniversalRemoteProvider";
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_CURSOR = false;
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
        } else if (URPContract.TABLE_PROVIDER_DETAILS_PATH.equals(table)) {
            int iconId = getProviderIcon();
            try {
                if (0 != iconId) {
                    return getContext()
                            .getResources()
                            .openRawResourceFd(iconId);
                }
            } catch (Exception e) {
                Log.d("UniversalRemote", "Could not open icon file.", e);
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
        try {
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
            } else if (URPContract.TABLE_BUTTON_DETAILS_PATH.equals(table)) {
                return getCursor(new Parent[] { getCachedDetails(Parent.fromUri(uri)) });
            } else if (URPContract.TABLE_BUTTONS_PATH.equals(table)) {
                Parent[] nodes = getChildren(Parent.fromUri(uri));
                return getCursor(nodes);
            } else if (URPContract.BUTTON_COMMAND_SEND.equals(table)) {
                // TODO Might want to replace with a single thread executor.
                new Thread() {
                    public void run() {
                        try {
                            Button button = (Button) getCachedDetails(Parent.fromUri(uri));
                            sendButtons(new Button[] { button });
                        } catch (Exception e) {
                            // Gotta catch em all so buggy plugins don't bring down
                            // the main application.
                            if (!onError(e)) {
                                Log.e(TAG, "Error sending button " + uri + "\n" + e.getMessage(), e);
                            }
                        }
                    }
                }.start();
                return new MatrixCursor(new String[] {"Sent"});
            }
        } catch (Exception e) {
            // Gotta catch em all so buggy plugins don't bring down
            // the main application.
            if (!onError(e)) {
                Log.d(TAG, e.getMessage(), e);
                throw new IllegalArgumentException(e.getMessage(), e);
            } else {
                return null;
            }
        }
        throw new IllegalArgumentException("Unknown query: " + uri);
    }

    /**
     * An error was caught during a query or button send operation.
     * @param e
     * @returns Return true if you have handled this exception and do not want to show the extra error logs.
     */
    public boolean onError(Exception e) {
        // Override this if you are concerned with handling error messages.
        return false;
    }

    private Cursor getCursor(Parent[] nodes) {
        MatrixCursor cursor;
        if (nodes.length > 0) {
            cursor = new MatrixCursor(nodes[0].getColumns());
            String table = "";
            for (String col : nodes[0].getColumns()) {
                table += col + "\t";
            }
            if (DEBUG_CURSOR) Log.d(TAG, table);
            for (Parent node : nodes) {
                if (DEBUG_CURSOR) {
                    String row = "";
                    for (Object col : node.toRow()) {
                        row += col + "\t";
                    }
                    Log.d(TAG, row);
                }
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

        parent = getDetails(parent);
        return Parent.getCached(parent);
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

    /**
     * Returns true if the parent is the root of the provider's nodes.
     * @return
     */
    public boolean isRoot(Parent parent) {
        return null == parent || parent.getPath().length == 0;
    }

    /**
     * Returns an icon representing this provider
     * @return
     */
    public int getProviderIcon() {
        return 0;
    }
}

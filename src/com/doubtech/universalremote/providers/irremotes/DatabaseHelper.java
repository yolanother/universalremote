package com.doubtech.universalremote.providers.irremotes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Brands;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Buttons;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Remotes;
import com.doubtech.universalremote.utils.IOUtil;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String TAG = "UniversalRemote : DatabaseHelper";
    private Context mContext;

    private static DatabaseHelper sInstance;

    public synchronized static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            File remotesDb = new File(context.getCacheDir(), "remotes.db");
            if (!remotesDb.exists()) {
                extractSourceDatabase(context, "remotes.db", remotesDb);
            }
            sInstance = new DatabaseHelper(context);
        }

        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, new File(context.getCacheDir(), "remotes.db").getAbsolutePath(), null, VERSION);
        mContext = context.getApplicationContext();
    }

    public String createTable(String name, String[] columns, String[] columnTypes) {
        if (columns.length != columnTypes.length) {
            throw new RuntimeException();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ");
        builder.append(name);
        builder.append("(");
        for (int i = 0; i < columns.length; i++) {
            builder.append(columns[i] + " " + columnTypes[i]);
            if (i + 1 < columns.length) {
                builder.append(", ");
            }
        }
        builder.append(");");
        return builder.toString();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Importing existing database so we don't need to create.
    }

    private boolean importDatabase(File tempFile) {
        if (!extractSourceDatabase(mContext, "remotes.db", tempFile)) return false;
        boolean success = true;

        SQLiteDatabase db = getWritableDatabase();
        try {
            boolean attached = false;
            try {
                db.execSQL(String.format("ATTACH DATABASE '%s' as source;", tempFile.getAbsolutePath()));
                attached = true;
                String insert = "INSERT INTO main.%s SELECT * from source.%s;";
                db.execSQL(String.format(insert, Brands.TABLE_NAME, Brands.TABLE_NAME));
                db.execSQL(String.format(insert, Remotes.TABLE_NAME, Remotes.TABLE_NAME));
                db.execSQL(String.format(insert, Buttons.TABLE_NAME, Buttons.TABLE_NAME));
            } finally {
                if (attached) {
                    db.execSQL("DETACH DATABASE source;");
                }
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage(), e);
            success = false;
        } finally {
            if (null != tempFile && tempFile.exists()) {
                tempFile.delete();
            }
        }

        return success;
    }

    public static boolean extractSourceDatabase(Context context, String assetName, File file) {
        boolean success = true;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getResources().getAssets().open(assetName);

            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            success = false;
        } finally {
            IOUtil.closeQuietly(is);
            IOUtil.closeQuietly(fos);
        }
        return success;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

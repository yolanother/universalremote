package com.doubtech.universalremote.providers.irremotes.o1r;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.doubtech.universalremote.providers.irremotes.DatabaseHelper;
import com.doubtech.universalremote.providers.irremotes.o1r.O1rDataProviderContract.Tables;
import com.doubtech.universalremote.providers.irremotes.o1r.O1rDataProviderContract.Views;

public class O1rDatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    @SuppressWarnings("unused")
	private static final String TAG = "UniversalRemote : O1rDatabaseHelper";
    private Context mContext;

    private static O1rDatabaseHelper sInstance;

    public synchronized static O1rDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            File remotesDb = context.getDatabasePath("remotes.db");
            boolean importDb = !remotesDb.exists();
            sInstance = new O1rDatabaseHelper(context);
            if (importDb) {
                sInstance.importO1RDatabase();
            }
        }

        return sInstance;
    }

    public O1rDatabaseHelper(Context context) {
        super(context, "remotes.db", null, VERSION);
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
        db.execSQL(createTable(
                Tables.Brands.TABLE_NAME,
                Tables.Brands.Columns.ALL,
                Tables.Brands.Columns.ALL_TYPES));

        db.execSQL(createTable(
                Tables.CodeLink.TABLE_NAME,
                Tables.CodeLink.Columns.ALL,
                Tables.CodeLink.Columns.ALL_TYPES));

        db.execSQL(createTable(
                Tables.Codes.TABLE_NAME,
                Tables.Codes.Columns.ALL,
                Tables.Codes.Columns.ALL_TYPES));

        db.execSQL(createTable(
                Tables.DeviceTypes.TABLE_NAME,
                Tables.DeviceTypes.Columns.ALL,
                Tables.DeviceTypes.Columns.ALL_TYPES));

        db.execSQL(createTable(
                Tables.SetOfCodes.TABLE_NAME,
                Tables.SetOfCodes.Columns.ALL,
                Tables.SetOfCodes.Columns.ALL_TYPES));

        db.execSQL(Views.IRCodes.CREATION_QUERY);

        db.execSQL("INSERT INTO Brands values(-1, 'Recently Added');");
    }

    public void importO1RDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        File tempFile = null;
        try {
            tempFile = new File(mContext.getFilesDir().getPath(), "/temp.db");

            DatabaseHelper.extractSourceDatabase(mContext, "O1R_DATABASE_1AUG10.db", tempFile);

            boolean attached = false;
            try {
                db.execSQL(String.format("ATTACH DATABASE '%s' as source;", tempFile.getAbsolutePath()));
                attached = true;
                String insert = "INSERT INTO main.%s SELECT * from source.%s;";
                String query = String.format(insert, "Brands", "M_Brands");
                db.execSQL(query);
                db.execSQL(String.format(insert, "CodeLink", "M_CodeLink"));
                db.execSQL(String.format(insert, "Codes", "M_Codes"));
                db.execSQL(String.format(insert, "DeviceTypes", "M_DeviceTypes"));
                db.execSQL(String.format(insert, "SetOfCodes", "M_SetOfCodes"));
            } finally {
                if (attached) {
                    db.execSQL("DETACH DATABASE source;");
                }
            }

        } finally {
            if (null != tempFile && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

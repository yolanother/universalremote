package com.doubtech.universalremote.providers.irremotes;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.doubtech.universalremote.R;
import com.doubtech.universalremote.ir.IrManager;
import com.doubtech.universalremote.providers.BaseAbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Brands;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Buttons;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Remotes;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.utils.StringUtils;

public class IrRemoteProvider extends BaseAbstractUniversalRemoteProvider {
    private static final String TAG = "UniversalRemote : IrRemoteProvider";

    public static final String AUTHORITY = "com.doubtech.universalremote.providers.irremotes.LircProvider";

    private DatabaseHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = DatabaseHelper.getInstance(getContext());
        return super.onCreate();
    }

    @Override
    public String getAuthority() {
        return AUTHORITY;
    }

    @Override
    protected Cursor getBrands(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return compileQuery(Brands.TABLE_NAME, projection, selection, selectionArgs, Brands.Columns.BrandName);
    }

    @Override
    public String getBrandColNameBrandName() {
        return Brands.Columns.BrandName;
    }

    @Override
    public String getBrandColNameId() {
        return Brands.Columns.BrandID;
    }

    @Override
    protected Cursor getModels(String[] projection, String brandId,
            String[] selectionArgs, String sortOrder) {
        return compileQuery(Remotes.TABLE_NAME, projection, null != brandId ? Remotes.Columns.BrandId + " = " + brandId : brandId, selectionArgs, Remotes.Columns.RemoteName);
    }

    @Override
    public String getModelColNameBrandId() {
        return Remotes.Columns.BrandId;
    }

    @Override
    public String getModelColNameModelName() {
        return Remotes.Columns.RemoteName;
    }

    @Override
    public String getModelColNameId() {
        return Remotes.Columns.RemoteId;
    }
    
    @Override
    public Button[] sendButtons(Button[] buttons) {
    	SparseArray<Button> map = new SparseArray<Button>();
    	StringBuilder query = new StringBuilder("select ");
    	StringUtils.implode(",", query, Buttons.Columns.ALL);
    	query.append(" from ");
    	query.append(Buttons.TABLE_NAME);
    	query.append(" where ");
    	query.append(Buttons.Columns.ButtonId);
    	query.append(" in (");
    	for(int i = 0; i < buttons.length; i++) {
    		query.append(buttons[i].getButtonId());
    		if(i + 1 < buttons.length) {
    			query.append(", ");
    		}
    		map.put(Integer.parseInt(buttons[i].getButtonId()), buttons[i]);
    	}
    	query.append(")");
        final SQLiteDatabase db = mHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query.toString(), null);
    	if(cursor.moveToFirst()) {
    		do {
    			Button button = map.get(cursor.getInt(Buttons.Columns.PROJECTION_BUTTON_ID));
    			button.putExtra(Buttons.Columns.ButtonCode,
    					cursor.getString(Buttons.Columns.PROJECTION_BUTTON_CODE));
    		} while(cursor.moveToNext());
    	}

    	IrManager ir = IrManager.getInstance(getContext());
    	for(Button button : buttons) {
    		ir.transmitPronto(button.getInternalData(Buttons.Columns.ButtonCode));
    	}
    	
    	return buttons;
    }

    @Override
    protected Cursor getButtons(String[] projection, String brandId, String modelId,
            String[] buttons, String sortOrder) {
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        StringBuilder query = new StringBuilder("select ");
        projection = Buttons.Columns.ALL;
        for (int i = 0; i < projection.length; i++) {
            query.append(projection[i]);
            if (i + 1 < projection.length) {
                query.append(", ");
            }
        }
        if (projection.length > 0) {
            query.append(", ");
        }
        query.append(Buttons.Columns.ButtonCode);
        query.append(" from ");
        query.append(Buttons.TABLE_NAME);
        if (null != modelId || null != buttons && buttons.length > 0) {
            query.append(" where ");

            if (null != modelId) {
                query.append(Buttons.Columns.RemoteId);
                query.append(" = ");
                query.append(modelId);
            }

            if (null != buttons && buttons.length > 0) {
                if (null != modelId) {
                    query.append(" and ");
                }
                query.append("(");
                for (int i = 0; i < buttons.length; i++) {
                    String button = buttons[i];
                    query.append(Buttons.Columns.ButtonId);
                    query.append("=");
                    query.append(button);
                    if (i + 1 < buttons.length) {
                        query.append(" or ");
                    }
                }
                query.append(")");
            }
        }
        query.append(" order by " + Buttons.Columns.ButtonLabel);
        Cursor cursor = db.rawQuery(query.toString(), null);

        MatrixCursor modifiedCursor = new MatrixCursor(URPContract.Buttons.ALL);
        if (cursor.moveToFirst()) {
        	do {
        		Button button = Button.fromCursor(this, getAuthority(), cursor);
        		button.putExtra(Buttons.Columns.ButtonCode, cursor.getString(cursor.getColumnIndex(Buttons.Columns.ButtonCode)));
                modifiedCursor.addRow(button.toRow());
                
            } while (cursor.moveToNext());
        }
        return modifiedCursor;
    }

    @Override
    public String getButtonsColNameModelId() {
        return Buttons.Columns.RemoteId;
    }

    @Override
    public String getButtonsColNameId() {
        return Buttons.Columns.ButtonId;
    }

    @Override
    public String getButtonsColNameButtonName() {
        return Buttons.Columns.ButtonName;
    }

    private Cursor compileQuery(String table, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mHelper.getReadableDatabase();


        Log.d(TAG, compileQueryString(table, projection, selection, selectionArgs, sortOrder));

        Cursor cursor = db.query(
                true,
                table,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null,
                null);
        return cursor;
    }

    private String compileQueryString(String table,
            String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        if (null != projection && projection.length > 0) {
            for (int i = 0; i < projection.length; i++) {
                query.append(projection[i]);
                if (i + 1 < projection.length) {
                    query.append(",");
                }
            }
        } else {
            query.append("*");
        }

        query.append(" FROM ");
        query.append(table);

        if (null != selection) {
            String where = selection;
            for (int i = 0; null != selectionArgs && i < selectionArgs.length && where.contains("?"); i++) {
                where = where.replaceFirst("\\?", selectionArgs[i]);
            }
            query.append(" WHERE ");
            query.append(where);
        }

        if (null != sortOrder) {
            query.append(" ORDER BY ");
            query.append(sortOrder);
        }
        return query.toString();
    }

    @Override
    public String getProviderName() {
    	return getContext().getResources().getString(R.string.lirc_provider_name);
    }

    @Override
    public String getProviderDescription() {
    	return getContext().getResources().getString(R.string.lirc_provider_desc);
    }

    @Override
    public boolean isProviderEnabled() {
    	return IrManager.isSupported(getContext());
    }
}

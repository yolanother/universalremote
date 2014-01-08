package com.doubtech.universalremote.providers.irremotes;

import java.io.OutputStream;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.doubtech.universalremote.ButtonIdentifier;
import com.doubtech.universalremote.ButtonIdentifier.ResourceLabel;
import com.doubtech.universalremote.ir.IrManager;
import com.doubtech.universalremote.providers.AbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Brands;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Buttons;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Remotes;

public class IrRemoteProvider extends AbstractUniversalRemoteProvider {
    private static final String TAG = "UniversalRemote : IrRemoteProvider";

	public static final String AUTHORITY = "com.doubtech.universalremote.providers.irremotes";
	
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
	protected String getBrandColNameBrandName() {
		return Brands.Columns.BrandName;
	}
	
	@Override
	protected String getBrandColNameId() {
		return Brands.Columns.BrandID;
	}
	
	@Override
	protected Cursor getModels(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return compileQuery(Remotes.TABLE_NAME, projection, null != selection ? Remotes.Columns.BrandId + " = " + selection : selection, selectionArgs, Remotes.Columns.RemoteName);
	}

	@Override
	protected String getModelColNameBrandId() {
		return Remotes.Columns.BrandId;
	}
	
	@Override
	protected String getModelColNameModelName() {
		return Remotes.Columns.RemoteName;
	}
	
	@Override
	protected String getModelColNameId() {
		return Remotes.Columns.RemoteId;
	}

	private class FullResourceLabel extends ResourceLabel {
		@Override
		public String getLabelText() {
			if(0 != getLabelId()) {
				return getContext().getResources().getString(getLabelId());
			}
			return null;
		}
	}

	@Override
	protected Cursor getButtons(String[] projection, String modelId,
			String[] buttons, String sortOrder) {
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        StringBuilder query = new StringBuilder("select ");
        for(int i = 0; i < projection.length; i++) {
        	query.append(projection[i]);
        	if(i + 1 < projection.length) {
        		query.append(", ");
        	}
        }
        if(projection.length > 0) {
        	query.append(", ");
        }
        query.append("ButtonCode");
        query.append(" from ");
        query.append(Buttons.TABLE_NAME);
        if(null != modelId || null != buttons && buttons.length > 0) {
            query.append(" where ");

	        if(null != modelId) {
	        	query.append(Buttons.Columns.RemoteId);
	        	query.append(" = ");
	        	query.append(modelId);
        	}
	        
	        if(null != buttons && buttons.length > 0) {
	        	if(null != modelId) {
	        		query.append(" and ");
	        	}
	        	query.append("(");
	        	for(int i = 0; i < buttons.length; i++) {
	        		String button = buttons[i];
	        		query.append(Buttons.Columns.ButtonId);
	        		query.append("=");
	        		query.append(button);
	        		if(i + 1 < buttons.length) {
	        			query.append(" or ");
	        		}
	        	}
	        	query.append(")");
	        }
        }
        query.append(" order by " + Buttons.Columns.ButtonLabel);
        Cursor cursor = db.rawQuery(query.toString(), null);
        String[] columns = new String[cursor.getColumnCount()];
        for(int i = 0; i < cursor.getColumnCount(); i++) {
        	columns[i] = cursor.getColumnName(i);
        }
        MatrixCursor modifiedCursor = new MatrixCursor(columns);
        if(cursor.moveToFirst()) {
        	do {
        		Object[] row = new Object[cursor.getColumnCount()];
    			FullResourceLabel outputLabel = new FullResourceLabel();
    			ButtonIdentifier.getLabel(cursor.getString(URPContract.Buttons.COLIDX_NAME), outputLabel);
        		for(int i = 0; i < cursor.getColumnCount(); i++) {        			
        			switch(i) {
        			case URPContract.Buttons.COLIDX_NAME:
        				row[i] = outputLabel.toString();
        				break;
        			case URPContract.Buttons.COLIDX_ID:
        			case URPContract.Buttons.COLIDX_MODEL_ID:
        				row[i] = cursor.getInt(i);
        				break;
        			case URPContract.Buttons.COLIDX_BUTTON_IDENTIFIER:
        				row[i] = outputLabel.getButtonIdentifier();
        			default:
        				row[i] = cursor.getString(i);
        				break;
        			}
        		}
        		modifiedCursor.addRow(row);
        	} while(cursor.moveToNext());
        }
    	return modifiedCursor;
	}

	@Override
	protected String getButtonsColNameModelId() {
		return Buttons.Columns.RemoteId;
	}
	
	@Override
	protected String getButtonsColNameId() {
		return Buttons.Columns.ButtonId;
	}
	
	@Override
	protected String getButtonsColNameButtonName() {
		return Buttons.Columns.ButtonName;
	}

	@Override
	public Cursor sendButtons(Cursor buttons) {
		IrManager ir = IrManager.getInstance(getContext());
		int buttonCodeColumn = buttons.getColumnIndex("ButtonCode");
		if(-1 == buttonCodeColumn) {
			Log.d(TAG, "Query didn't include button column.");
			throw new IllegalArgumentException();
		}
		if(buttons.moveToFirst()) {
			int limit = 0;
			do {
				Log.i(TAG, "Sending pronto code " + buttons.getString(buttonCodeColumn));
				ir.transmitPronto(buttons.getString(buttonCodeColumn));
			} while(buttons.moveToNext() && limit++ < 50);
		}
		return buttons;
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
	public ParcelFileDescriptor openButtonIcon(Cursor button) {
		return null;
	}
	
	@Override
	public AssetFileDescriptor openButtonIconAsset(Cursor button) {
		button.moveToFirst();
		FullResourceLabel outputLabel = new FullResourceLabel();
		ButtonIdentifier.getLabel(button.getString(URPContract.Buttons.COLIDX_NAME), outputLabel);
		try {
			if(0 != outputLabel.getIconId()) {
				return getContext()
						.getResources()
						.openRawResourceFd(outputLabel.getIconId());
			}
		} catch (Exception e) {
			Log.d(TAG, "Could not open icon file.", e);
		}
		return null;
	}
}

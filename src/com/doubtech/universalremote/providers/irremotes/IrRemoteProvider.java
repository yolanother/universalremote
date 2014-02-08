package com.doubtech.universalremote.providers.irremotes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.doubtech.universalremote.R;
import com.doubtech.universalremote.ir.IrManager;
import com.doubtech.universalremote.providers.AbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Brands;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Buttons;
import com.doubtech.universalremote.providers.irremotes.DataProviderContract.Tables.Remotes;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Button.ButtonBuilder;
import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.providers.providerdo.Parent.ParentBuilder;
import com.doubtech.universalremote.utils.ButtonStyler;
import com.doubtech.universalremote.utils.StringUtils;

public class IrRemoteProvider extends AbstractUniversalRemoteProvider {
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
    public Parent[] getChildren(Parent parent) {
        if (null == parent || parent.getPath().length == 0) {
            return getBrands(parent);
        } else if (parent.getPath().length == 1) {
            return getModels(parent);
        } else if (parent.getPath().length == 2 || parent.getPath().length == 3) {
            return getButtons(parent);
        }
        return new Parent[0];
    }

    @Override
    public Parent getDetails(Parent parent) {
        return getButtons(parent)[0];
    }

    private Parent[] getBrands(Parent parent) {
        Cursor cursor = compileQuery(Brands.TABLE_NAME, null, null, null, Brands.Columns.BrandName);
        Parent[] brands = new Parent[cursor.getCount()];
        cursor.moveToFirst();
        String levelName = getContext().getResources().getString(R.string.level_brands);
        for (int i = 0; i < brands.length; i++) {
            String id = cursor.getString(Brands.Columns.PROJECTION_BRAND_ID);
            brands[i] = new ParentBuilder(getAuthority(), new String[] { id })
                .setName(cursor.getString(Brands.Columns.PROJECTION_BRAND_NAME))
                .setLevelName(levelName)
                .build();
            cursor.moveToNext();
        }
        return brands;
    }

    private Parent[] getModels(Parent parent) {
        String brandId = parent.getId();
        Cursor cursor = compileQuery(Remotes.TABLE_NAME, null, null != brandId ? Remotes.Columns.BrandId + " = " + brandId : brandId, null, Remotes.Columns.RemoteName);
        Parent[] brands = new Parent[cursor.getCount()];
        cursor.moveToFirst();
        String levelName = getContext().getResources().getString(R.string.level_buttons);
        for (int i = 0; i < brands.length; i++) {
            String id = cursor.getString(Remotes.Columns.PROJECTION_REMOTE_ID);
            brands[i] = new ParentBuilder(getAuthority(), new String[] { brandId, id })
                .setName(cursor.getString(Remotes.Columns.PROJECTION_REMOTE_NAME))
                .setHasButtonSets(true)
                .setLevelName(levelName)
                .build();
            cursor.moveToNext();
        }
        return brands;
    }

    private Button[] getButtons(Parent parent) {
        String[] buttonIds = null;
        String modelId = parent.getId();
        if (parent.getPath().length == 3) {
            buttonIds = new String[] { parent.getId() };
            modelId = parent.getPath()[1];
        }
        Cursor cursor = getButtons(Buttons.Columns.ALL, modelId, buttonIds, null);
        Button[] brands = new Button[cursor.getCount()];
        cursor.moveToFirst();
        String levelName = getContext().getResources().getString(R.string.level_models);
        for (int i = 0; i < brands.length; i++) {
            String id = cursor.getString(Buttons.Columns.PROJECTION_BUTTON_ID);
            brands[i] = (Button) new ButtonBuilder(getAuthority(), new String[] { parent.getPath()[0], parent.getPath()[1], id })
                .putExtra(Buttons.Columns.ButtonCode, cursor.getString(Buttons.Columns.PROJECTION_BUTTON_CODE))
                .setName(cursor.getString(Buttons.Columns.PROJECTION_BUTTON_NAME))
                .setHasButtonSets(true)
                .setLevelName(levelName)
                .build();
            cursor.moveToNext();
        }
        return brands;
    }

    @Override
    public Button[] sendButtons(Button[] buttons) {
        boolean fetch = false;

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = (Button) Parent.getCached(buttons[i]);
            if (buttons[i].needsToFetch()) fetch = true;
        }

        if (fetch) {
            SparseArray<Button> map = new SparseArray<Button>();
            StringBuilder query = new StringBuilder("select ");
            StringUtils.implode(",", query, Buttons.Columns.ALL);
            query.append(" from ");
            query.append(Buttons.TABLE_NAME);
            query.append(" where ");
            query.append(Buttons.Columns.ButtonId);
            query.append(" in (");
            for (int i = 0; i < buttons.length; i++) {
                query.append(buttons[i].getId());
                if (i + 1 < buttons.length) {
                    query.append(", ");
                }
                map.put(Integer.parseInt(buttons[i].getId()), buttons[i]);
            }
            query.append(")");
            final SQLiteDatabase db = mHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(query.toString(), null);
            if (cursor.moveToFirst()) {
                do {
                    Button button = map.get(cursor.getInt(Buttons.Columns.PROJECTION_BUTTON_ID));
                    button.putExtra(Buttons.Columns.ButtonCode,
                            cursor.getString(Buttons.Columns.PROJECTION_BUTTON_CODE));
                } while (cursor.moveToNext());
            }
        }

        IrManager ir = IrManager.getInstance(getContext());
        for (Button button : buttons) {
            ir.transmitPronto(button.getInternalData(Buttons.Columns.ButtonCode));
        }

        return buttons;
    }

    @Override
    public int getIconId(Parent button) {
        int id = ButtonStyler.getIconId(button.getName());
        return id;
    }

    protected Cursor getButtons(String[] projection, String modelId,
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

        return cursor;
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

    @Override
    public int getProviderIcon() {
        return R.drawable.lirc;
    }
}

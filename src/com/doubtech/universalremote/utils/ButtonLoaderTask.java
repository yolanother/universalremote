package com.doubtech.universalremote.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.doubtech.universalremote.providers.BaseAbstractUniversalRemoteProvider;

public class ButtonLoaderTask extends AsyncTask<String, Void, Cursor> {
    private Context mContext;

    public ButtonLoaderTask(Context context) {
        mContext = context;
    }

    @Override
    protected Cursor doInBackground(String... params) {
        String authority = params[0];
        String brandId = params[1];
        String modelId = params[2];

        return BaseAbstractUniversalRemoteProvider.queryButtons(mContext, authority, brandId, modelId);
    }

    public void execute(String authority, String brandId, String modelId) {
        super.execute(authority, brandId, modelId);
    }
}

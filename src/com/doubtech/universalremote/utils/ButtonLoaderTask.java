package com.doubtech.universalremote.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.doubtech.universalremote.providers.AbstractUniversalRemoteProvider;
import com.doubtech.universalremote.providers.providerdo.Parent;

public class ButtonLoaderTask extends AsyncTask<Parent, Void, Cursor> {
    private Context mContext;

    public ButtonLoaderTask(Context context) {
        mContext = context;
    }

    @Override
    protected Cursor doInBackground(Parent... params) {

        return AbstractUniversalRemoteProvider.query(mContext, params[0]);
    }
}

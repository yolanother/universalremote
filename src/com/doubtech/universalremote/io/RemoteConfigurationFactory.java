package com.doubtech.universalremote.io;

import java.io.File;
import java.util.Locale;

import android.content.Context;
import android.net.Uri;

public class RemoteConfigurationFactory {

    public static RemoteConfigurationReader getInstance(Context context, File file) {
        return getInstance(context, file.getName());
    }

    public static RemoteConfigurationReader getInstance(Context context, Uri uri) {
        return getInstance(context, uri.toString());
    }

    public static RemoteConfigurationReader getInstance(Context context, String file) {
        if (null == file) return null;

        if (file.toString().toLowerCase(Locale.getDefault()).endsWith("xml")) {
            return new RemoteConfigurationXmlReader(context);
        } else if (file.toString().toLowerCase(Locale.getDefault()).endsWith("json")) {
            return new RemoteConfigurationJsonReader(context);
        }

        return null;
    }
}

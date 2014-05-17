package com.doubtech.universalremote.utils;

import java.io.File;

import android.os.Environment;

public class Constants {
    public static final File REMOTES_DIR = new File(Environment.getExternalStorageDirectory(), "UniversalRemote");
    public static final File REMOTE_FILE = new File(REMOTES_DIR, "remote.xml");
    public static final String AUTHORITY_FILE_PROVIDER = "com.doubtech.universalremote.fileprovider";


    public static final String EXTRA_REMOTES_DIR = "dir";
    public static final String EXTRA_COLUMN_COUNT = "colCount";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_ACCESSORY_AUTHORITY = "accessory";
    public static final String EXTRA_ACCESSORY_NAME = "accessoryname";

    public static final String FIELD_REMOTE_PAGES = "pages";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_GEOFENCES = "geofences";
}

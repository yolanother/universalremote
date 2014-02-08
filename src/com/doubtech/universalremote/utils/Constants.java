package com.doubtech.universalremote.utils;

import java.io.File;

import android.os.Environment;

public class Constants {
    public static final File REMOTES_DIR = new File(Environment.getExternalStorageDirectory(), "UniversalRemote");
    public static final File REMOTE_FILE = new File(REMOTES_DIR, "remote.xml");
    public static final String AUTHORITY_FILE_PROVIDER = "com.doubtech.universalremote.fileprovider";
}

package com.doubtech.universalremote.utils;

import java.io.File;

import android.os.Environment;

public class Constants {
    public static final File REMOTE_FILE = new File(Environment.getExternalStorageDirectory(), "UniversalRemote/remote.xml");
	public static final String AUTHORITY_FILE_PROVIDER = "com.doubtech.universalremote.fileprovider";
}

package com.doubtech.universalremote.utils;

import java.io.Closeable;
import java.io.IOException;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;

public class Utils {
    public static void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static IOException closeQuietly(Closeable closable) {
        if (null != closable) {
            try {
                closable.close();
            } catch (IOException e) {
                return e;
            }
        }
        return null;
    }
}

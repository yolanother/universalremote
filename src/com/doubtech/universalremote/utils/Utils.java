package com.doubtech.universalremote.utils;

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
}

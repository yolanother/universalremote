package com.doubtech.universalremote.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.providers.providerdo.Button;

public class IconLoader {
    private static ConcurrentHashMap<String, Bitmap> mIconCache = new ConcurrentHashMap<String, Bitmap>();
    static Executor mIconLoader = Executors.newFixedThreadPool(4);
    public static void loadIcon(final Context context, final Button button,
            final IconLoaderListener iconLoaderListener) {
        final String key = getIconCacheKey(button);
        Bitmap mButtonIcon = mIconCache.get(key);
        if (null == mButtonIcon) {
            mIconLoader.execute(new Runnable() {

                @Override
                public void run() {
                    Uri uri = button.getUri();
                    File dir = new File(context.getCacheDir(), "iconcache");
                    File cacheFile = new File(dir, key + ".png");

                    if (cacheFile.exists()) {
                        Bitmap icon = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
                        if (null != key && null != icon) {
                            mIconCache.put(key, icon);
                            iconLoaderListener.onIconLoaded(icon);
                        }
                    }

                    AssetFileDescriptor desc;
                    try {
                        desc = context.getContentResolver().openAssetFileDescriptor(uri, "r");
                        if (null != desc) {
                            Bitmap mButtonIcon = BitmapFactory.decodeStream(desc.createInputStream());
                            if (null != mButtonIcon) {
                                mIconCache.put(key, mButtonIcon);
                                iconLoaderListener.onIconLoaded(mButtonIcon);

                                cacheFile.getParentFile().mkdirs();
                                FileOutputStream fos = new FileOutputStream(cacheFile);
                                mButtonIcon.compress(CompressFormat.PNG, 9, fos);
                                fos.close();
                                return;
                            }
                        }
                    } catch (FileNotFoundException e) {
                        Log.w("UniversalRemote", e.getMessage(), e);
                    } catch (IOException e) {
                        Log.w("UniversalRemote", e.getMessage(), e);
                    }
                }
            });
        } else {
            iconLoaderListener.onIconLoaded(mButtonIcon);
        }
    }

    private static String getIconCacheKey(Button button) {
        StringBuilder builder = new StringBuilder();
        String[] path = button.getPath();
        builder.append(button.getAuthority());
        for (String segment : path) {
            builder.append("/");
            builder.append(Uri.encode(segment).replaceAll("[^a-zA-Z0-9_-]", "_"));
        }
        return builder.toString();
    }
}
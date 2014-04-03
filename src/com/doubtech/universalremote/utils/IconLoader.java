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
import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.providerdo.Button;

public class IconLoader {
    private static ConcurrentHashMap<String, Bitmap> mIconCache = new ConcurrentHashMap<String, Bitmap>();
    static Executor mIconLoader = Executors.newFixedThreadPool(4);

    public static void loadProviderIcon(Context context, String authority, final IconLoaderListener iconLoaderListener) {
        Uri uri = URPContract.getProviderDetailsUri(authority);
        loadIcon(context, authority, uri, null, iconLoaderListener);
    }

    public static void loadIcon(Context context, Button button,
            final IconLoaderListener iconLoaderListener) {
        final String key = getIconCacheKey(button);
        Uri uri = button.getUri();

        loadIcon(context, key, uri, button.getName(), iconLoaderListener);
    }

    public static void loadIcon(final Context context, final String key, final Uri uri, final String buttonName,
            final IconLoaderListener iconLoaderListener) {
        Bitmap mButtonIcon = mIconCache.get(key);
        if (null != buttonName) {
            loadDefaultAsset(context, key, buttonName, iconLoaderListener);
        }
        if (null == mButtonIcon) {
            mIconLoader.execute(new Runnable() {

                @Override
                public void run() {
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
                        } else if (null != buttonName) {
                            loadDefaultAsset(context, key, buttonName, iconLoaderListener);
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

    private static void loadDefaultAsset(Context context, String key, String buttonName,
            IconLoaderListener iconLoaderListener) {
        int id = ButtonStyler.getIconId(buttonName);
        if (0 != id) {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(id));
            if (null != key && null != bitmap) {
                mIconCache.put(key, bitmap);
                onIconLoaded(bitmap, iconLoaderListener);
            }
        }
    }

    private static void onIconLoaded(final Bitmap bitmap, final IconLoaderListener listener) {
        Utils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.onIconLoaded(bitmap);
                }
            });
    }

    private static String getIconCacheKey(Button button) {
        StringBuilder builder = new StringBuilder();
        String[] path = button.getPath();
        builder.append(button.getAuthority());
        for (String segment : path) {
            builder.append("/");
            builder.append(Uri.encode(segment).replaceAll("[^a-zA-Z0-9_-]", "_"));
        }
        builder.append("/");
        builder.append(Uri.encode(button.getName()).replaceAll("[^a-zA-Z0-9_-]", "_"));
        return builder.toString();
    }
}

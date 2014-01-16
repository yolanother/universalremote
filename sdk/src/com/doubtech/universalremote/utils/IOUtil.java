package com.doubtech.universalremote.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {
    public static void closeQuietly(Closeable... closeables) {
        for (Closeable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch(IOException ex) {
                    // Swallow exception
                }
            }
        }
    }
}

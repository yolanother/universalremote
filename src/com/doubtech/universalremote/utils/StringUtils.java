package com.doubtech.universalremote.utils;

import java.util.Collection;

public class StringUtils {
    public static void implode(String delimeter, StringBuilder query, String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            query.append(strings[i]);
            if (i + 1 < strings.length) {
                query.append(delimeter);
            }
        }
    }

    public static void implode(String delimeter, StringBuilder query, Object ... strings) {
        if (strings.length == 1 && strings[0] instanceof String[]) {
            implode(delimeter, query, (String[]) strings[0]);
        } else if (strings.length == 1 && strings[0] instanceof Collection<?>) {
            Collection<?> strs = (Collection<?>) strings[0];
            int i = 0;
            for (Object str : strs) {
                query.append(str);
                if (++i < strs.size()) {
                    query.append(delimeter);
                }
            }
        } else {
            for (int i = 0; i < strings.length; i++) {
                query.append(strings[i]);
                if (i + 1 < strings.length) {
                    query.append(delimeter);
                }
            }
        }
    }
}

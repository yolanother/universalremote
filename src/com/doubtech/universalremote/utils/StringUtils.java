package com.doubtech.universalremote.utils;

import java.util.Collection;

public class StringUtils {
    public static StringBuilder implode(String delimeter, StringBuilder query, String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            query.append(strings[i]);
            if (i + 1 < strings.length) {
                query.append(delimeter);
            }
        }
        return query;
    }

    public static StringBuilder implode(String delimeter, StringBuilder query, int[] strings) {
        for (int i = 0; i < strings.length; i++) {
            query.append(strings[i]);
            if (i + 1 < strings.length) {
                query.append(delimeter);
            }
        }
        return query;
    }

    public static StringBuilder implode(String delimeter, StringBuilder query, float[] strings) {
        for (int i = 0; i < strings.length; i++) {
            query.append(strings[i]);
            if (i + 1 < strings.length) {
                query.append(delimeter);
            }
        }
        return query;
    }

    public static StringBuilder implode(String delimeter, StringBuilder query, double[] strings) {
        for (int i = 0; i < strings.length; i++) {
            query.append(strings[i]);
            if (i + 1 < strings.length) {
                query.append(delimeter);
            }
        }
        return query;
    }

    public static StringBuilder implode(String delimeter, StringBuilder query, long[] strings) {
        for (int i = 0; i < strings.length; i++) {
            query.append(strings[i]);
            if (i + 1 < strings.length) {
                query.append(delimeter);
            }
        }
        return query;
    }

    public static StringBuilder implode(String delimeter, StringBuilder query, short[] strings) {
        for (int i = 0; i < strings.length; i++) {
            query.append(strings[i]);
            if (i + 1 < strings.length) {
                query.append(delimeter);
            }
        }
        return query;
    }

    public static StringBuilder implode(String delimeter, StringBuilder query, byte[] strings) {
        for (int i = 0; i < strings.length; i++) {
            query.append(strings[i]);
            if (i + 1 < strings.length) {
                query.append(delimeter);
            }
        }
        return query;
    }

    public static StringBuilder implode(String delimeter, StringBuilder query, Object ... strings) {
        if (strings.length == 1 && strings[0] instanceof Object[]) {
            implode(delimeter, query, (Object[]) strings[0]);
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
        return query;
    }

    public static String implode(String delimeter, String[] strings) {
        return implode(delimeter, new StringBuilder(), strings).toString();
    }

    public static String implode(String delimeter, int[] strings) {
        return implode(delimeter, new StringBuilder(), strings).toString();
    }

    public static String implode(String delimeter, float[] strings) {
        return implode(delimeter, new StringBuilder(), strings).toString();
    }

    public static String implode(String delimeter, double[] strings) {
        return implode(delimeter, new StringBuilder(), strings).toString();
    }

    public static String implode(String delimeter, long[] strings) {
        return implode(delimeter, new StringBuilder(), strings).toString();
    }

    public static String implode(String delimeter, short[] strings) {
        return implode(delimeter, new StringBuilder(), strings).toString();
    }

    public static String implode(String delimeter, byte[] strings) {
        return implode(delimeter, new StringBuilder(), strings).toString();
    }

    public static String implode(String delimeter, Object ... strings) {
        return implode(delimeter, new StringBuilder(), strings).toString();
    }
}

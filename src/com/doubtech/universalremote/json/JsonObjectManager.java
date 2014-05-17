package com.doubtech.universalremote.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public class JsonObjectManager {
    public static final String FIELD_TYPE = "__type";

    public static IJsonElement fromJson(JSONObject object, Object... params) throws ClassNotFoundException, JSONException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (!object.has(FIELD_TYPE)) throw new ClassNotFoundException("JSonObject does not have a " + FIELD_TYPE + " field to indicate object type.");
        String control = object.getString(FIELD_TYPE);
        Class<?> c = Class.forName(control);
        Class<? extends IJsonElement> type = (Class<? extends IJsonElement>) c;
        if (null == type) throw new ClassNotFoundException("Field thpe " + type + " has not been registered   with JsonObjectManager.");

        // And now for some massive over engineering :-P. For this app the first
        // constructor will probably always be the right one so it shouldn't have
        // much of a performance hit
        Constructor<?>[] constructors = type.getConstructors();
        for (Constructor constructor : constructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (params.length == paramTypes.length) {
                for (int i = 0; i < params.length; i++) {
                    Class<?> paramType = paramTypes[i];
                    if (!paramType.isAssignableFrom(params[i].getClass())) {
                        Log.d("AARON", "Param didn't match: " + paramType + "==" + params[i]);
                        break;
                    }
                    try {
                        IJsonElement element = (IJsonElement) constructor.newInstance(params);
                        element.fromJson(object);
                        return element;
                    } catch (ClassCastException e) {
                        Log.w("UniversalRemote", "Could not read control for " + object.getString(FIELD_TYPE), e);
                    }
                }
            }
        }

        throw new InvalidParameterException("Your configuration file may be out of date, we couldn't find a control that matched " + control);
    }

    public static JSONObject toJson(IJsonElement object) throws JSONException {
        JSONObject jobj = object.toJson();
        jobj.put(FIELD_TYPE, object.getClass().getName());
        return jobj;
    }
}

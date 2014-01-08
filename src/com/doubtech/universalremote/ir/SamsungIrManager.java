package com.doubtech.universalremote.ir;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;

public class SamsungIrManager extends IrManager {
    public static final String TAG = "SamsungIrManager";
    private final static String IRDA_SERVICE = "irda";
    private final static String FEATURE_IRDA_SERVICE = "com.sec.feature.irda_service";
    private Object mService;
    private Class<?> mClass;
    private Method mWriteIrsendMethod;
    private Method mReadIrsendMethod;


    SamsungIrManager(Context context) {
        mService = context.getSystemService(IRDA_SERVICE);
        if(null == mService) throw new IRNotSupportedException();
        if (!context.getPackageManager().hasSystemFeature(FEATURE_IRDA_SERVICE)) {
            Log.w(TAG, "This is not a Samsung built IR service, your IR service milage may vary.");
        }

        // Load the required methods
        try {
            mClass = mService.getClass();
            mWriteIrsendMethod = mClass.getMethod("write_irsend", String.class);
        } catch (NoSuchMethodException e) {
            throw new IRNotSupportedException("Could not find necessary IR interfaces for sending IR messages. IR Manager service will not be available.", e);
        }

        // Load the optional methods
        try {
            mReadIrsendMethod = mClass.getMethod("read_irsend");
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "Read irsend not available. Only ir transmissions will be supported.");
        }
    }

    @Override
    void transmitImpl(int carrierFrequency, int[] pattern)
    		throws InvalidIrCodeException {
        try {
        	StringBuilder data = new StringBuilder(Integer.toString(carrierFrequency));
        	for(int timing : pattern) {
        		data.append(" ");
        		data.append(timing);
        	}
        	Log.d(TAG, "Sending " + data);
            mWriteIrsendMethod.invoke(mService, data.toString());

            // Catch blocks should be unnecessary, if we have properly checked
            // for irda support the arguments, access, and invocation errors
            // shouldn't be hit.
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Could not execute write, error resolving service libraries.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not execute write, error resolving service libraries.", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Could not execute write, error resolving service libraries.", e);
        }
    }

    public String readIrData() {
        if (null == mReadIrsendMethod) {
            return null;
        }
        try {
            return (String) mReadIrsendMethod.invoke(mService);

            // Catch blocks should be unnecessary, if we have properly checked
            // for irda support the arguments, access, and invocation errors
            // shouldn't be hit.
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Could not execute write, error resolving service libraries.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not execute write, error resolving service libraries.", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Could not execute write, error resolving service libraries.", e);
        }
    }

    public static boolean isIrSupported(Context context) {
        return null != context.getSystemService(IRDA_SERVICE);
    }
}

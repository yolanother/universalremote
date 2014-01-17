package com.doubtech.universalremote.ir;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

public abstract class IrManager {
    public static final String TAG = "IrManager";

    private static IrManager mInstance;
    private ExecutorService mSendPool;

    public static IrManager getInstance(Context context) {
        if (null == mInstance) {
            if (KitKatIrManager.isIrSupported(context)) {
                return new KitKatIrManager(context);
            }
            if (SamsungIrManager.isIrSupported(context)) {
                return new SamsungIrManager(context);
            }
        } else {
            return mInstance;
        }
        throw new IRNotSupportedException();
    }

    public static boolean isSupported(Context context) {
        return KitKatIrManager.isIrSupported(context) ||
                SamsungIrManager.isIrSupported(context);
    }

    public IrManager() {
        mSendPool = Executors.newSingleThreadExecutor();
    }

    public void transmitPronto(String timings) {
        String[] timingStringSet = timings.split("[ ,]");
        if (timingStringSet.length > 4) {
            int frequency = (int) (1000000/(Integer.parseInt(timingStringSet[1]) * .241246));
            int[] timingIntSet = new int[timingStringSet.length - 4];
            for (int i = 4; i < timingIntSet.length; i++) {
                if (timingStringSet[i].length() > 0) {
                    timingIntSet[i - 4] = Integer.parseInt(timingStringSet[i]);
                }
            }

            transmit(frequency, timingIntSet);
        }
    }

    public void transmit(final int frequency, final int[] timings) {
        mSendPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        transmitImpl(frequency, timings);
                    } catch(RejectedExecutionException e) {
                        destroy();
                    } catch (InvalidIrCodeException e) {
                        Log.w(TAG, "Could not transmit ir code.", e);
                    }
                }
            });
    }

    public synchronized void destroy() {
        mSendPool.shutdown();
        try {
            mSendPool.shutdownNow();
            if (!mSendPool.awaitTermination(60, TimeUnit.SECONDS)) {
                Log.e(TAG, "Send pool did not shutdown properly.");
            }
        } catch(InterruptedException e) {
            mSendPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        mInstance = null;
    }

    abstract void transmitImpl(int frequency, int[] timings) throws InvalidIrCodeException;
}

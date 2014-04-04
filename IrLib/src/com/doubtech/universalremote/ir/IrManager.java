package com.doubtech.universalremote.ir;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

public abstract class IrManager {
    public static final String TAG = "IrManager";

    static final boolean DEBUG = false;

    public static final int PRONTO_HEADER_BLOCK_LENGTH = 4;

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
        mSendPool = Executors.newFixedThreadPool(8);
    }

    public void transmitPronto(String pronto) {
        String[] prontotringSet = pronto.split("[ ,]");
        if (prontotringSet.length > PRONTO_HEADER_BLOCK_LENGTH) {
            int frequency = (int) (1000000/(Integer.parseInt(prontotringSet[1]) * .241246));
            int[] timingIntSet = new int[prontotringSet.length - PRONTO_HEADER_BLOCK_LENGTH];
            for (int i = 0; i < timingIntSet.length; i++) {
                if (prontotringSet[i].length() > 0) {
                    timingIntSet[i] = Integer.parseInt(prontotringSet[i + PRONTO_HEADER_BLOCK_LENGTH]);
                }
            }

            transmit(frequency, timingIntSet);
        }
    }

    /**
     * Send an ir code stored in a string
     * Format:
     * Frequency,time0,time1,time2...
     * @param timingString
     */
    public void transmitTimingString(String timingString) {
        String[] set = timingString.split("[ ,]");
        int frequency = Integer.parseInt(set[0]);
        int[] timings = new int[set.length - 1];
        for (int i = 0; i < timings.length; i++) {
            timings[i] = Integer.parseInt(set[i + 1]);
        }
        transmit(frequency, timings);
    }

    public static String prontoToTimings(String pronto) {
        StringBuilder timings = new StringBuilder();
        String[] prontotringSet = pronto.split("[ ,]");
        if (prontotringSet.length > PRONTO_HEADER_BLOCK_LENGTH) {
            int frequency = (int) (1000000/(Integer.parseInt(prontotringSet[1]) * .241246));
            timings.append(frequency);
            int[] timingIntSet = new int[prontotringSet.length - PRONTO_HEADER_BLOCK_LENGTH];
            for (int i = 0; i < timingIntSet.length; i++) {
                if (prontotringSet[i].length() > 0) {
                    timings.append(",");
                    timings.append(Integer.parseInt(prontotringSet[i + PRONTO_HEADER_BLOCK_LENGTH]));
                }
            }
        }
        return timings.toString();
    }

    public void transmit(final int frequency, final int[] timings) {
        mSendPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (DEBUG) Log.d(TAG, frequency + "," + StringUtils.implode(",", timings));
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

    abstract void transmitImpl(int frequency, int[] pronto) throws InvalidIrCodeException;

    public static String getIrUri(String pronto) {
        return "ir://" + pronto;
    }
}

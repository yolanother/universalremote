package com.doubtech.universalremote.ir;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Build;

@TargetApi(19)
public class KitKatIrManager extends IrManager {

    private ConsumerIrManager mIrManager;

    KitKatIrManager(Context context) {
        mIrManager = (ConsumerIrManager) context.getSystemService(Service.CONSUMER_IR_SERVICE);

        if (!mIrManager.hasIrEmitter()) {
            throw new IRNotSupportedException();
        }
    }

    @Override
    void transmitImpl(int carrierFrequency, int[] pattern) throws InvalidIrCodeException {
        try {
            mIrManager.transmit(carrierFrequency, pattern);
        } catch (IllegalArgumentException e) {
            throw new InvalidIrCodeException(e);
        }
    }

    public static boolean isIrSupported(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ConsumerIrManager irManager = (ConsumerIrManager) context.getSystemService(Service.CONSUMER_IR_SERVICE);

            return irManager.hasIrEmitter();
        }
        return false;
    }
}

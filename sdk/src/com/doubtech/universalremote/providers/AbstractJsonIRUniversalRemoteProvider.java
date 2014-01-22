package com.doubtech.universalremote.providers;

import com.doubtech.universalremote.ir.IrManager;
import com.doubtech.universalremote.providers.providerdo.Button;

public abstract class AbstractJsonIRUniversalRemoteProvider extends
        AbstractJsonUniversalRemoteProvider {

    @Override
    public Button[] sendButtons(Button[] buttons) {
        IrManager manager = IrManager.getInstance(getContext());
        for (Button button : buttons) {
            String buttonData = button.getInternalData("buttonCode");
            manager.transmitPronto(buttonData);
        }
        return buttons;
    }

    @Override
    public boolean isProviderEnabled() {
        return IrManager.isSupported(getContext());
    }
}

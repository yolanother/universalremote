package com.doubtech.universalremote.providers;

import org.json.JSONException;
import org.json.JSONObject;

import com.doubtech.universalremote.ir.IrManager;
import com.doubtech.universalremote.providers.providerdo.Button;

public abstract class AbstractJsonIRUniversalRemoteProvider extends
        AbstractJsonUniversalRemoteProvider {

    @Override
    public Button[] sendButtons(Button[] buttons) {
        IrManager manager = IrManager.getInstance(getContext());
        for (Button button : buttons) {
            String buttonData = button.getInternalData("buttonData");
            manager.transmitPronto(buttonData);
        }
        return buttons;
    }

    @Override
    public void onPutExtras(Button button,
            JSONObject buttonData) throws JSONException {
        button.putExtra("buttonCode", buttonData.getString("buttonCode"));
    }

    @Override
    public boolean isProviderEnabled() {
        return IrManager.isSupported(getContext());
    }
}

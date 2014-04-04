package com.doubtech.universalremote.providers.irremotes;

import android.util.Log;

import com.doubtech.universalremote.R;
import com.doubtech.universalremote.ir.IrManager;
import com.doubtech.universalremote.providers.AbstractJsonIRUniversalRemoteProvider;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.utils.ButtonStyler;

public class GlobalCacheProvider extends AbstractJsonIRUniversalRemoteProvider {
    @Override
    public String getProviderName() {
        return getContext().getString(R.string.globalcache_provider_name);
    }

    @Override
    public String getProviderDescription() {
        return getContext().getString(R.string.globalcache_provider_desc);
    }

    @Override
    public String getAuthority() {
        return "com.doubtech.universalremote.providers.irremotes.GlobalCache";
    }

    @Override
    public String getUrlString(Parent parent) {
        return "http://ir.doubtech.com/json.php/globalcache" + parent.getPathString();
    }

    @Override
    public int getIconId(Parent button) {
        return ButtonStyler.getIconId(button.getName());
    }

    @Override
    public Button[] sendButtons(Button[] buttons) {
        IrManager manager = IrManager.getInstance(getContext());
        for (Button button : buttons) {
            String buttonData = button.getInternalData(INTERNAL_DATA_BUTTON_CODE);
            String[] code = buttonData.split(",");
            int[] timings = new int[code.length - 1];
            for (int i = 0; i < timings.length; i++) {
                timings[i] = Integer.parseInt(code[i + 1]);
            }
            manager.transmit(Integer.parseInt(code[0]), timings);
        }
        return buttons;
    }

    @Override
    public Parent getDetails(Parent parent) {
        parent = super.getDetails(parent);
        if (parent instanceof Button) {

            ((Button) parent).setHardwareUri(IrManager.getIrUri(((Button) parent).getInternalData("buttonCode")));
        }
        return parent;
    }

    @Override
    public int getProviderIcon() {
        return R.drawable.globalcache;
    }
}

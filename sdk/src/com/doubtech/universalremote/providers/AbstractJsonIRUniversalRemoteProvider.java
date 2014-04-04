package com.doubtech.universalremote.providers;

import com.doubtech.universalremote.ir.IrManager;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Parent;

/**
 * Class to handle JSON based IR codes
 * @author yolan
 *
 * NOTE: This provider assumes you are providing an ir code in PRONTO format in
 * the Button object's internal data with the key INTERNAL_DATA_BUTTON_CODE
 */
public abstract class AbstractJsonIRUniversalRemoteProvider extends
        AbstractJsonUniversalRemoteProvider {
    public static final String INTERNAL_DATA_BUTTON_CODE = "buttonCode";

    @Override
    public Button[] sendButtons(Button[] buttons) {
        IrManager manager = IrManager.getInstance(getContext());
        for (Button button : buttons) {
            String buttonData = button.getInternalData(INTERNAL_DATA_BUTTON_CODE);
            manager.transmitPronto(buttonData);
        }
        return buttons;
    }

    @Override
    public Parent getDetails(Parent parent) {
        parent = super.getDetails(parent);
        setHardwareUri(parent);
        return parent;
    }

    private void setHardwareUri(Parent parent) {
        if (parent instanceof Button) {
            Button button = (Button) parent;
            String buttonData = button.getInternalData(INTERNAL_DATA_BUTTON_CODE);
            if (null != buttonData) {
                button.setHardwareUri(IrManager.getIrUri(IrManager.prontoToTimings(buttonData)));
            }
        }
    }

    @Override
    public Parent[] getChildren(Parent parent) {
        Parent[] children =  super.getChildren(parent);
        for (Parent node : children) {
            setHardwareUri(node);
        }
        return children;
    }

    @Override
    public boolean isProviderEnabled() {
        return IrManager.isSupported(getContext());
    }
}

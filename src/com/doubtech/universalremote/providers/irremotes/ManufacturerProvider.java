package com.doubtech.universalremote.providers.irremotes;

import com.doubtech.universalremote.R;
import com.doubtech.universalremote.providers.AbstractJsonIRUniversalRemoteProvider;
import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.utils.ButtonStyler;

public class ManufacturerProvider extends AbstractJsonIRUniversalRemoteProvider {
    @Override
    public String getProviderName() {
        return getContext().getString(R.string.manufacturer_provider_name);
    }

    @Override
    public String getProviderDescription() {
        return getContext().getString(R.string.manufacturer_provider_desc);
    }

    @Override
    public String getAuthority() {
        return "com.doubtech.universalremote.providers.irremotes.Manufacturer";
    }

    public String getUrlString(Parent parent) {
        return "http://ir.doubtech.com/json.php/manufacturers" + parent.getPathString();
    }

    @Override
    public int getIconId(Parent button) {
        return ButtonStyler.getIconId(button.getName());
    }

    @Override
    public int getProviderIcon() {
        return R.drawable.manufacturer;
    }
}

package com.doubtech.universalremote.utils;

import android.content.Context;
import android.database.Cursor;

import com.doubtech.universalremote.ButtonFunction;
import com.doubtech.universalremote.ButtonFunctionSet;
import com.doubtech.universalremote.providers.URPContract;
import com.doubtech.universalremote.providers.providerdo.Button;
import com.doubtech.universalremote.providers.providerdo.Parent;
import com.doubtech.universalremote.providers.providerdo.ProviderDetails;

public class ProviderUtils {
    public static Cursor query(Context context, Parent parent) {
        return context.getContentResolver().query(
                        parent.getUri(),
                        null,
                        null,
                        null,
                        null);
    }

    public static ProviderDetails queryProviderDetails(Context context, String authority) {
        return ProviderDetails.fromCursor(context.getContentResolver().query(
                URPContract.getProviderDetailsUri(authority),
                null,
                null,
                null,
                null));
    }

    public static Cursor sendButton(Context context, Button button) {
        if (null != button) {
            return context.getContentResolver().query(
                    URPContract.getUri(button.getAuthority(), URPContract.BUTTON_COMMAND_SEND, button.getPath()),
                null,
                null,
                null,
                null);
        }
        return null;
    }

    public static void sendButton(Context context, ButtonFunctionSet buttonFunctionSet) {
        if (null != buttonFunctionSet) {
            for (ButtonFunction button : buttonFunctionSet) {
                if (null != button) {
                    sendButton(context, (Button) button.getButton());
                }
            }
        }
    }
}

package com.doubtech.universalremote;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Log;

import com.doubtech.universalremote.ir.IrManager;
import com.doubtech.universalremote.listeners.IconLoaderListener;
import com.doubtech.universalremote.utils.DirectHardwareSchemas;
import com.doubtech.universalremote.utils.ProviderUtils;

public class ButtonFunctionSet extends ArrayList<ButtonFunction> {
    private static final long serialVersionUID = 1L;

    public void send(Context context) {
        for (ButtonFunction function : this) {
            String hwuri = function.getButton().getHardwareUri();
            if (null != hwuri) {
                if (hwuri.startsWith(DirectHardwareSchemas.HWSCHEMA_IR)) {
                    IrManager.getInstance(context).transmitTimingString(hwuri.substring(DirectHardwareSchemas.HWSCHEMA_IR.length()));
                } else if (hwuri.startsWith(DirectHardwareSchemas.HWSCHEMA_HTTP)) {
                    try {
                        URL url = new URL(hwuri);
                        url.openConnection();
                    } catch (MalformedURLException e) {
                        Log.w("UniversalRemote", "Invalid hardware uri was provided by "
                                + function.getButton().getAuthority()
                                + "::" + hwuri);
                    } catch (IOException e) {
                        Log.w("UniversalRemote", function.getButton().getAuthority() + "\n" + e.getMessage(), e);
                    }
                } else {
                    Log.w("UniversalRemote", "Invalid hardware uri was provided by "
                            + function.getButton().getAuthority()
                            + "::" + function.getButton().getName());
                }
            } else {
                ProviderUtils.sendButton(context,
                        function.getButton());
            }
            for (ButtonFunction subfunction : function) {
                subfunction.send(context);
            }
        }
    }

    public static ButtonFunctionSet fromXml(Context context, String tag, Element item) {
        NodeList list = item.getElementsByTagName(tag);
        if (list.getLength() == 0) return new ButtonFunctionSet();
        return fromXml(context, (Element) list.item(0));
    }

    public static ButtonFunctionSet fromXml(Context context, Element item) {
        NodeList list = item.getElementsByTagName(ButtonFunction.XMLTAG);
        ButtonFunctionSet buttonList = new ButtonFunctionSet();
        if (list.getLength() == 0) return buttonList;

        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i) instanceof Element) {
                Element element = (Element) list.item(i);
                buttonList.add(ButtonFunction.fromXml(context, element));
            }
        }
        return buttonList;
    }

    public void writeXml(XmlSerializer xml) throws IllegalArgumentException, IllegalStateException, IOException {
        for (ButtonFunction function : this) {
            function.writeXml(xml);
        }
    }

    public void getIcon(Context context, IconLoaderListener listener) {
        if (size() > 0) {
            get(0).getIcon(context, listener);
        } else {
            listener.onIconLoaded(null);
        }
    }

    public String getLabel() {
        if (size() > 0) {
            return get(0).getLabel();
        }
        return "";
    }
}

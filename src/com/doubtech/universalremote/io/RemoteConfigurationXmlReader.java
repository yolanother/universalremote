package com.doubtech.universalremote.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.doubtech.geofenceeditor.SimpleGeofence;
import com.doubtech.universalremote.RemotesLoadedListener;
import com.doubtech.universalremote.widget.RemotePage;

public class RemoteConfigurationXmlReader implements RemoteConfigurationReader {
    private class ErrorRunnable implements Runnable {

        private Throwable mError;
        private RemotesLoadedListener mListener;

        public ErrorRunnable(RemotesLoadedListener listener,
                Throwable e) {
            mError = e;
            mListener = listener;
        }

        @Override
        public void run() {
            if (null != mListener) {
                mListener.onRemoteLoadFailed(mError);
            } else {
                Log.d(TAG, mError.getMessage(), mError);
            }
        }
    }

    private static ExecutorService sLoaderService = Executors.newSingleThreadExecutor();
    private Handler mHandler;
    private Context mContext;

    public RemoteConfigurationXmlReader(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void open(final Uri uri, final boolean readPages, final RemotesLoadedListener listener) {
        sLoaderService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ParcelFileDescriptor fd = mContext.getContentResolver().openFileDescriptor(uri, "r");
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;

                    builder = factory.newDocumentBuilder();
                    Document dom = builder.parse(new FileInputStream(fd.getFileDescriptor()));
                    Element root = dom.getDocumentElement();
                    final List<RemotePage> pages = readPages ? read(root) : null;
                    final SimpleGeofence geofence = readGeofence(root);
                    final String name = root.getAttribute("name");
                    if (null != listener) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onRemotesLoaded(uri, name, pages, geofence);
                            }
                        });
                    }
                } catch (FileNotFoundException e) {
                    mHandler.post(new ErrorRunnable(listener, e));
                } catch (ParserConfigurationException e) {
                    mHandler.post(new ErrorRunnable(listener, e));
                } catch (SAXException e) {
                    mHandler.post(new ErrorRunnable(listener, e));
                } catch (IOException e) {
                    mHandler.post(new ErrorRunnable(listener, e));
                }
            }
        });
    }

    private List<RemotePage> read(Element root) throws ParserConfigurationException, SAXException, IOException {
        List<RemotePage> pages = new ArrayList<RemotePage>();
        NodeList nodes = root.getElementsByTagName(RemotePage.XMLTAG);
        for (int i = 0; i < nodes.getLength(); i++) {
            pages.add(RemotePage.fromXml(mContext, (Element) nodes.item(i)));
        }

        return pages;
    }

    public static SimpleGeofence readGeofence(Element root) {
        NodeList nodes = root.getElementsByTagName("geofence");
        for (int i = 0; i < nodes.getLength(); i++) {
            // TODO support multiple geofences
            Element node = (Element) nodes.item(i);
            SimpleGeofence geofence = new SimpleGeofence(
                    node.getAttribute(SimpleGeofence.EXTRA_ID),
                    Double.parseDouble(node.getAttribute(SimpleGeofence.EXTRA_LATITUDE)),
                    Double.parseDouble(node.getAttribute(SimpleGeofence.EXTRA_LONGITUDE)),
                    Float.parseFloat(node.getAttribute(SimpleGeofence.EXTRA_RADIUS)),
                    Long.parseLong(node.getAttribute(SimpleGeofence.EXTRA_EXPIRATION)),
                    SimpleGeofence.transitionTypeFromString(node.getAttribute(SimpleGeofence.EXTRA_TRANSITION_TYPE)));

            if (node.hasAttribute(SimpleGeofence.EXTRA_NAME)) {
                geofence.setName(node.getAttribute(SimpleGeofence.EXTRA_NAME));
            }
            return geofence;
        }
        return null;
    }
}

package com.doubtech.universalremote.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import android.os.ParcelFileDescriptor;

import com.doubtech.universalremote.widget.RemotePage;

public class RemoteConfigurationReader {
    private static final String TAG = "UniversalRemote :: RemoteConfigurationReader";

    public interface RemotesLoadedListener {
        void onRemotesLoaded(Uri uri, List<RemotePage> pages);
        void onRemoteLoadFailed(Throwable error);
    }

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
            mListener.onRemoteLoadFailed(mError);
        }
    }

    private static ExecutorService sLoaderService = Executors.newSingleThreadExecutor();
    private Handler mHandler;
    private Context mContext;

    public RemoteConfigurationReader(Context context) {
        mContext = context;
        mHandler = new Handler();
    }

    public void open(final Uri uri, final RemotesLoadedListener listener) {
        sLoaderService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ParcelFileDescriptor fd = mContext.getContentResolver().openFileDescriptor(uri, "r");
                    final List<RemotePage> pages = read(new FileInputStream(fd.getFileDescriptor()));
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            listener.onRemotesLoaded(uri, pages);
                        }
                    });
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

    public List<RemotePage> read(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<RemotePage> pages = new ArrayList<RemotePage>();
        DocumentBuilder builder;

        builder = factory.newDocumentBuilder();
        Document dom = builder.parse(stream);
        Element root = dom.getDocumentElement();
        NodeList nodes = root.getElementsByTagName(RemotePage.XMLTAG);
        for (int i = 0; i < nodes.getLength(); i++) {
            pages.add(RemotePage.fromXml(mContext, (Element) nodes.item(i)));
        }

        return pages;
    }
}

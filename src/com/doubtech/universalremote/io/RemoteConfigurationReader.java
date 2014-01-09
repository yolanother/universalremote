package com.doubtech.universalremote.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.doubtech.universalremote.RemotePage;

public class RemoteConfigurationReader {
	private static final String TAG = "UniversalRemote :: RemoteConfigurationReader";

	public List<RemotePage> read(Context context, InputStream stream) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<RemotePage> pages = new ArrayList<RemotePage>();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document dom = builder.parse(stream);
			Element root = dom.getDocumentElement();
			NodeList nodes = root.getElementsByTagName(RemotePage.XMLTAG);
			for(int i = 0; i < nodes.getLength(); i++) {
				pages.add(RemotePage.fromXml(context, (Element) nodes.item(i)));
			}
		} catch (ParserConfigurationException e) {
			Log.d(TAG, e.getMessage(), e);
		} catch (SAXException e) {
			Log.d(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		
		return pages;
	}
}

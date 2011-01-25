package org.far.twoduiproject;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.content.ContentValues;
import android.content.Context;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class FeedParser {

	private DatabaseHelper mDbHelper;
	
	public FeedParser(Context context){
		mDbHelper = mDbHelper.getInstance(context);
	}
	
	/**
	 * Preliminary method to parse data from an Atom 2.0 RSS formated XML file input stream.
	 * @param atomstream
	 * @throws IOException
	 * @throws SAXException
	 */
	public void parseAtomStream(InputStream atomstream, final int categoryid) throws IOException, SAXException {
		final ContentValues itemvalues = new ContentValues();
		
		RootElement root = new RootElement("rss");
		Element item = root.getChild("channel").getChild("item");

		// set handlers for elements we want to react to
		item.setEndElementListener(new EndElementListener() {
			public void end() {
				// TODO: add item to list and return it or add it to db right away.
				mDbHelper.addItem(itemvalues, categoryid);
				itemvalues.clear();
			}
		});
		item.getChild("title").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				itemvalues.put(DatabaseHelper.TITLE, body);
			}
		});
		item.getChild("description").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				itemvalues.put(DatabaseHelper.DESCRIPTION, body);
			}
		});
		item.getChild("link").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				itemvalues.put(DatabaseHelper.LINK, body);
			}
		});
		item.getChild("pubDate").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				itemvalues.put(DatabaseHelper.PUBDATE, body);
			}
		});

		Xml.parse(atomstream, Xml.Encoding.UTF_8, root.getContentHandler());
	}
}

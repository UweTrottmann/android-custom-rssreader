package org.far.twoduiproject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

	static SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

	public FeedParser(Context context) {
		mDbHelper = mDbHelper.getInstance(context);
	}

	/**
	 * Preliminary method to parse data from an RSS 2.0 formatted input stream.
	 * 
	 * @param atomstream
	 * @param categoryid
	 * @throws IOException
	 * @throws SAXException
	 */
	public void parseAtomStream(InputStream atomstream, final int categoryid) throws IOException,
			SAXException {
		final ContentValues itemvalues = new ContentValues();

		RootElement root = new RootElement("rss");
		Element item = root.getChild("channel").getChild("item");

		// set handlers for elements we want to react to
		item.setEndElementListener(new EndElementListener() {
			public void end() {
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
				Date date;
				try {
					date = FORMATTER.parse(body.trim());
					Calendar cal = new GregorianCalendar();
					cal.setTime(date);
					itemvalues.put(DatabaseHelper.PUBDATE, cal.getTimeInMillis());
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});

		mDbHelper.beginTransaction();
		try {
			Xml.parse(atomstream, Xml.Encoding.UTF_8, root.getContentHandler());
			mDbHelper.setTransactionSuccessful();
		} finally {
			mDbHelper.endTransaction();
		}
	}
}

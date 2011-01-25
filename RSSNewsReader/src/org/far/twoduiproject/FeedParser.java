package org.far.twoduiproject;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class FeedParser {

	/**
	 * Preliminary method to parse data from an Atom 2.0 RSS formated XML file input stream.
	 * @param atomstream
	 * @throws IOException
	 * @throws SAXException
	 */
	public void parseAtomStream(InputStream atomstream) throws IOException, SAXException {
		RootElement root = new RootElement("rss");
		Element item = root.getChild("channel").getChild("item");

		// set handlers for elements we want to react to
		item.setEndElementListener(new EndElementListener() {
			public void end() {
				// TODO: add item to list and return it or add it to db right away.
			}
		});
		item.getChild("title").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {

			}
		});
		item.getChild("description").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {

			}
		});
		item.getChild("link").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {

			}
		});
		item.getChild("pubDate").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {

			}
		});

		Xml.parse(atomstream, Xml.Encoding.UTF_8, root.getContentHandler());
	}
}

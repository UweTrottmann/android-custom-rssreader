
package org.far.twoduiproject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.xml.sax.SAXException;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import android.widget.Toast;

public class FeedParser {

    static SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    /**
     * Parse data from an RSS 2.0 formatted input stream into the database.
     * 
     * @param atomstream
     * @param categoryid
     * @throws IOException
     * @throws SAXException
     */
    public static void parseAtomStream(InputStream atomstream, final int categoryid,
            final DatabaseHelper mDbHelper, Xml.Encoding encoding) throws IOException, SAXException {
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
            Xml.parse(atomstream, encoding, root.getContentHandler());
            mDbHelper.setTransactionSuccessful();
        } finally {
            mDbHelper.endTransaction();
        }
    }

    public static void parseInitialSetup(final SQLiteDatabase db, Context context) {
        RootElement root = new RootElement("config");
        Element category = root.getChild("categories").getChild("category");
        Element provider = root.getChild("providers").getChild("provider");

        // listen for categories
        final ContentValues values = new ContentValues();

        category.setEndElementListener(new EndElementListener() {
            public void end() {
                db.insert(DatabaseHelper.CATEGORY_TABLE, null, values);
                values.clear();
            }
        });
        category.getChild("name").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                values.put(DatabaseHelper.CATEGORY_NAME, body);
            }
        });
        category.getChild("id").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                values.put(DatabaseHelper.CATEGORY_ID, body);
            }
        });

        // listen for providers
        final ArrayList<PCategory> catMap = new ArrayList<PCategory>();
        final ContentValues catinfo = new ContentValues();

        provider.setEndElementListener(new EndElementListener() {
            public void end() {
                // insert provider in provider table
                db.insert(DatabaseHelper.PROVIDER_TABLE, null, values);

                // insert available categories in preference table
                ContentValues catvalues = new ContentValues();
                for (PCategory category : catMap) {
                    catvalues.put(DatabaseHelper.PREF_PROVIDERID, values
                            .getAsInteger(DatabaseHelper.PROVIDER_ID));
                    catvalues.put(DatabaseHelper.FEEDPATH, category.getPath());
                    catvalues.put(DatabaseHelper.PREF_CATEGORY_ID, category.getId());
                    catvalues.put(DatabaseHelper.PREF_ENCODING, category.getEncoding());

                    db.insert(DatabaseHelper.PREFERENCE_TABLE, null, catvalues);
                    catvalues.clear();
                }

                catMap.clear();
                values.clear();
            }
        });
        provider.getChild("name").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                values.put(DatabaseHelper.PROVIDER_NAME, body);
            }
        });
        provider.getChild("pid").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                values.put(DatabaseHelper.PROVIDER_ID, body);
            }
        });

        // listen for a providers categories
        Element pcategory = provider.getChild("pcategory");

        pcategory.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                catMap.add(new PCategory(catinfo.getAsInteger("id"), catinfo.getAsString("path"),
                        catinfo.getAsString("encoding")));
                catinfo.clear();
            }
        });
        pcategory.getChild("id").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                catinfo.put("id", Integer.valueOf(body));
            }
        });
        pcategory.getChild("path").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                catinfo.put("path", body);
            }
        });
        pcategory.getChild("encoding").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                catinfo.put("encoding", body);
            }
        });

        // begin parsing
        db.beginTransaction();
        try {
            InputStream in = context.getResources().getAssets().open("config.xml");
            Xml.parse(in, Xml.Encoding.UTF_8, root.getContentHandler());
            db.setTransactionSuccessful();
        } catch (IOException e) {
            Toast.makeText(context, "IO error while reading in config.xml", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        } catch (SAXException e) {
            Toast.makeText(context, "SAX error while reading in config.xml", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}

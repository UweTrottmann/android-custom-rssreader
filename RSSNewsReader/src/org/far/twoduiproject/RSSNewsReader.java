
package org.far.twoduiproject;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class RSSNewsReader extends ListActivity {
    private static final String KEY_FIRSTRUN = "firstrun";

    private DatabaseHelper mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());

        doFirstRunSetup();

        /*
         * All of the following is for testing purposes only, will be replaced
         * with actual frontend (listviews, etc.)
         */
        // mDbHelper.clear();
        // try {
        // FeedParser.parseAtomStream(getResources().getAssets().open("bbc_business_atom2.xml"),
        // 0, mDbHelper, Xml.Encoding.UTF_8);
        // FeedParser.parseAtomStream(getResources().getAssets().open("cnn_business.xml"),
        // 0,
        // mDbHelper, Xml.Encoding.ISO_8859_1);
        // FeedParser.parseAtomStream(getResources().getAssets().open("bbc_politics.xml"),
        // 1,
        // mDbHelper, Xml.Encoding.UTF_8);
        // FeedParser.parseAtomStream(getResources().getAssets().open("cnn_sports.xml"),
        // 2,
        // mDbHelper, Xml.Encoding.ISO_8859_1);
        // FeedParser.parseAtomStream(getResources().getAssets().open("bbc_technology_atom2.xml"),
        // 3, mDbHelper, Xml.Encoding.UTF_8);
        // FeedParser.parseAtomStream(getResources().getAssets().open("cnn_technology.xml"),
        // 3,
        // mDbHelper, Xml.Encoding.ISO_8859_1);
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (SAXException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        fillData();
    }

    private void fillData() {
        Cursor categories = mDbHelper.getCategories();
        startManagingCursor(categories);

        String[] from;
        int[] to;
        int layout;

        // Create an array to specify the fields we want to display in the
        // list
        from = new String[] {
            DatabaseHelper.CATEGORY_NAME
        };

        // and an array of the fields we want to bind those fields to
        to = new int[] {
            R.id.categoryname
        };

        layout = R.layout.categories_simple;

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter categoriesAdapter = new SimpleCursorAdapter(getApplicationContext(),
                layout, categories, from, to);

        setListAdapter(categoriesAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newsreader_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_preferences:
                // show preferences
                startActivity(new Intent(getApplicationContext(), MyNewsReaderSettings.class));

                return true;
            case R.id.menu_update:
                Toast.makeText(getApplicationContext(),
                        "Clearing database and parsing selected feeds", Toast.LENGTH_LONG).show();
                updateFeeds();
                return true;
            case R.id.menu_showtreeview:
                startActivity(new Intent(getApplicationContext(), ExpandableList.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Redirects the user to the settings activity, if the app is run for the
     * first time on a device.
     */
    private void doFirstRunSetup() {
        // display set-up activity on first-run
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        boolean isFirstRun = prefs.getBoolean(KEY_FIRSTRUN, true);

        if (isFirstRun) {
            // show preferences
            Toast.makeText(getApplicationContext(),
                    "First run, showing settings (use back button to go to mainscreen again)",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), MyNewsReaderSettings.class));
            // set firstrun false
            prefs.edit().putBoolean(KEY_FIRSTRUN, false).commit();
            // get all feeds in, user has to manually update if he changed
            // preferences
            updateFeeds();
        }
    }

    /**
     * Clears existing news items and parses the feeds which are set enabled in
     * the preferences table.
     */
    private void updateFeeds() {
        new Thread(new Runnable() {
            public void run() {
                mDbHelper.clearExistingFeeds();
                Cursor prefs = mDbHelper.getPreferences();
                prefs.moveToFirst();

                while (!prefs.isAfterLast()) {

                    // check if category is enabled for provider
                    if (prefs.getInt(prefs.getColumnIndexOrThrow(DatabaseHelper.ENABLED)) == 1) {

                        // get encoding and category id
                        Xml.Encoding encoding = Xml.Encoding.valueOf(prefs.getString(prefs
                                .getColumnIndexOrThrow(DatabaseHelper.PREF_ENCODING)));
                        int categoryid = prefs.getInt(prefs
                                .getColumnIndexOrThrow(DatabaseHelper.PREF_CATEGORY_ID));

                        // open xml and parse it into the database
                        try {
                            InputStream in = getResources().getAssets().open(
                                    prefs.getString(prefs
                                            .getColumnIndexOrThrow(DatabaseHelper.FEEDPATH)));
                            FeedParser.parseAtomStream(in, categoryid, mDbHelper, encoding);
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Could not open feed",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (SAXException e) {
                            Toast.makeText(getApplicationContext(), "Could not parse feed",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    prefs.moveToNext();
                }
                prefs.close();
            }
        }).start();
    }
}

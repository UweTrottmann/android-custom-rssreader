
package org.far.twoduiproject;

import java.io.IOException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class RSSNewsReader extends Activity {
    private static final String KEY_FIRSTRUN = "firstrun";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        doFirstRunSetup();

        /*
         * All of the following is for testing purposes only, will be replaced
         * with actual frontend (listviews, etc.)
         */
        FeedParser parser = new FeedParser(getApplicationContext());
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        dbHelper.clear();
        dbHelper.addCategory("Technology", 123);

        try {
            parser.parseAtomStream(getResources().getAssets().open("bbc_business_atom2.xml"), 123);
        } catch (IOException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();
        } catch (SAXException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();
        }
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

                // TODO: clear database, reparse all items and refresh list view

                Toast.makeText(getApplicationContext(),
                        "TODO: clear database, reparse all items and refresh list view now",
                        Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "First run, showing settings (use back button to go to mainscreen again)",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), MyNewsReaderSettings.class));
            // set firstrun false
            prefs.edit().putBoolean(KEY_FIRSTRUN, false).commit();
        }
    }
}

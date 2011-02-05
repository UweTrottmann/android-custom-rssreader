
package org.far.twoduiproject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.SAXException;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RSSNewsReader extends ListActivity {
    private static final String KEY_FIRSTRUN = "firstrun";

    public static final String TAG = "RSSNewsReader";

    static final String KEY_LISTTYPE = "listtype";

    private static final int ID_SIMPLELIST = 0;

    private static final int ID_FISHEYELIST = 1;

    private static final int ID_TREEVIEWLIST = 2;

    static final int EXPANDED_FONTSIZE = 30;

    protected static final float NORMAL_FONTSIZE = 8;

    public static final int DIALOG_UPDATEFEEDS = 0;

    private static final String STATE_UPDATE_IN_PROGRESS = "updateinprogress";

    private DatabaseHelper mDbHelper;

    private UpdateTask mUpdateTask;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);

        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());

        doFirstRunSetup();

        fillData();

        // // example of usage MeasurementModule
        // MeasurementModule.initializeSession(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((CustomListView) getListView()).setFirstRun();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onCancelUpdate();
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
        boolean isSimpleList = RSSNewsReader.isSimpleList(getApplicationContext());
        if (isSimpleList) {
            to = new int[] {
                R.id.categoryname
            };
            layout = R.layout.categories_simple;
        } else {
            to = new int[] {
                R.id.fisheye_item
            };
            layout = R.layout.fisheye_row;
        }

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter categoriesAdapter = new SimpleCursorAdapter(getApplicationContext(),
                layout, categories, from, to);

        setFishEyeListener(getListView(), getApplicationContext());

        setListAdapter(categoriesAdapter);

        ((CustomListView) getListView()).setFirstRun();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(getApplicationContext(), NewsList.class);
        i.putExtra(DatabaseHelper.CATEGORY_ID, id);
        startActivity(i);
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
                startActivity(new Intent(getApplicationContext(), MyNewsReaderSettings.class));
                return true;
            case R.id.menu_update:
                onUpdate();
                return true;
            case R.id.menu_usesimple:
                changeAndSaveListType(item, ID_SIMPLELIST);
                // // start measuring
                // MeasurementModule.startMeasurement(MeasurementModule.SIMPLE_LIST);
                fillData();
                return true;
            case R.id.menu_usefisheye:
                changeAndSaveListType(item, ID_FISHEYELIST);
                // // start measuring
                // MeasurementModule.startMeasurement(MeasurementModule.FISHEYE_LIST);
                fillData();
                return true;
            case R.id.menu_usetree:
                changeAndSaveListType(item, ID_TREEVIEWLIST);
                // // start measuring
                // MeasurementModule.startMeasurement(MeasurementModule.TREE_VIEW_LIST);
                // start treeview activity
                startActivity(new Intent(getApplicationContext(), ExpandableList.class));
                return true;
                // case R.id.menu_dumpmeasurements:
                // if (isExtStorageAvailable()) {
                // new DumpMeasurementsTask().execute();
                // } else {
                // Toast.makeText(getApplicationContext(),
                // "No external storage available",
                // Toast.LENGTH_SHORT).show();
                // }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Changes the checked state of the menu item and stores the selection of
     * list type in a SharedPreference.
     * 
     * @param item
     * @param listtypeID
     */
    private void changeAndSaveListType(MenuItem item, int listtypeID) {
        // check/uncheck the item
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
        // save selected listtype
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putInt(KEY_LISTTYPE, listtypeID).commit();
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
        }
    }

    /**
     * Clears existing news items and parses the feeds which are set enabled in
     * the preferences table.
     */
    private void onUpdate() {
        if (mUpdateTask == null || mUpdateTask.getStatus() == AsyncTask.Status.FINISHED) {
            mUpdateTask = (UpdateTask) new UpdateTask().execute();
        } else {
            Toast.makeText(getApplicationContext(), "Update in progress", Toast.LENGTH_SHORT).show();
        }
    }

    private void onCancelUpdate() {
        if (mUpdateTask != null && mUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
            mUpdateTask.cancel(true);
            mUpdateTask = null;
        }
    }

    private class UpdateTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
            Toast.makeText(getApplicationContext(), "Updating feeds", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String errorMsg = null;
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

                    URL url;
                    try {
                        url = new URL(prefs.getString(prefs
                                .getColumnIndexOrThrow(DatabaseHelper.FEEDPATH)));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // open xml and parse it into the database
                    try {
                        URLConnection connection = url.openConnection();
                        connection.setConnectTimeout(25000);
                        connection.setReadTimeout(90000);
                        InputStream in = connection.getInputStream();

                        FeedParser.parseAtomStream(in, categoryid, mDbHelper, encoding);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                        errorMsg = e.getMessage();
                    } catch (SAXException e) {
                        Log.e(TAG, e.getMessage(), e);
                        errorMsg = e.getMessage();
                    }
                }

                prefs.moveToNext();
            }
            prefs.close();

            return errorMsg;
        }

        @Override
        protected void onCancelled() {
            setProgressBarIndeterminateVisibility(false);
        }

        @Override
        protected void onPostExecute(String errorMsg) {
            setProgressBarIndeterminateVisibility(false);
            if (errorMsg != null) {
                Toast.makeText(getApplicationContext(), "Updating feeds failed" + " - " + errorMsg,
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    private class DumpMeasurementsTask extends AsyncTask<Void, Void, String> {

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Dumping measurements to sd card",
                    Toast.LENGTH_SHORT).show();
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(final Void... args) {

            // create directory
            File exportDir = new File(Environment.getExternalStorageDirectory(),
                    "rssreader_measurements");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            // create file, if it doesn't exist and open outchannel
            File file = new File(exportDir, "measurements.csv");
            String errorMsg = null;
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                errorMsg = e.getMessage();
            }

            // get measurements table and write it to the file
            FileOutputStream outStream;
            String measuretext;
            Cursor measurements = mDbHelper.getMeasurements();
            measurements.moveToFirst();

            try {
                outStream = new FileOutputStream(file);

                // write header for csv
                measuretext = DatabaseHelper.MEASUREMENT_ID + "," + DatabaseHelper.LIST_TYPE + ","
                        + DatabaseHelper.MEASUREMENT_ITEM + "," + DatabaseHelper.MEASUREMENT_TIME
                        + "\n";

                outStream.write(measuretext.getBytes());

                while (!measurements.isAfterLast()) {
                    measuretext = measurements.getString(measurements
                            .getColumnIndexOrThrow(DatabaseHelper.MEASUREMENT_ID))
                            + ",";
                    measuretext += measurements.getString(measurements
                            .getColumnIndexOrThrow(DatabaseHelper.LIST_TYPE))
                            + ",";
                    measuretext += measurements.getString(measurements
                            .getColumnIndexOrThrow(DatabaseHelper.MEASUREMENT_ITEM))
                            + ",";
                    measuretext += measurements.getString(measurements
                            .getColumnIndexOrThrow(DatabaseHelper.MEASUREMENT_TIME))
                            + "\n";
                    outStream.write(measuretext.getBytes());
                    measurements.moveToNext();
                }

                outStream.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                errorMsg = e.getMessage();
            }

            measurements.close();

            return errorMsg;
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String errorMsg) {
            if (errorMsg == null) {
                Toast.makeText(getApplicationContext(), "Dump successful", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), "Dump failed" + " - " + errorMsg,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void setFishEyeListener(final ListView listview, final Context context) {
        // TODO: put this in a parent class instead of using a static method
        listview.setOnTouchListener(new OnTouchListener() {
            float yTouchPosition;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // only display fisheye behaviour if it's a fisheye list
                if (isSimpleList(context)) {
                    return false;
                }
                yTouchPosition = event.getY();

                int height = 0;
                int childcount = listview.getChildCount();
                // reset all childs to default font-size
                for (int i = 0; i < childcount; i++) {
                    View child = listview.getChildAt(i);
                    ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_SP, NORMAL_FONTSIZE);
                }
                // set font-size for touched and surrounding
                for (int i = 0; i < childcount; i++) {
                    View child = listview.getChildAt(i);
                    int itemheight = child.getHeight();
                    if (yTouchPosition >= height && yTouchPosition < height + itemheight) {
                        // Log.d("onTouch", "pointer on view " +
                        // child.toString());
                        ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_SP,
                                EXPANDED_FONTSIZE);
                        int upperneighborid = i;
                        int lowerneighborid = i;
                        for (int step = 1; step < 3; step++) {
                            upperneighborid -= 1;
                            lowerneighborid += 1;
                            if (upperneighborid >= 0) {
                                ((TextView) listview.getChildAt(upperneighborid)).setTextSize(
                                        TypedValue.COMPLEX_UNIT_SP, EXPANDED_FONTSIZE - step * 7
                                                - 1);
                            }
                            if (lowerneighborid < childcount) {
                                ((TextView) listview.getChildAt(lowerneighborid)).setTextSize(
                                        TypedValue.COMPLEX_UNIT_SP, EXPANDED_FONTSIZE - step * 7
                                                - 1);
                            }
                        }
                        break;
                    }
                    height += itemheight;
                }
                return false;
            }
        });
    }

    /**
     * Returns true if listtype is simple or treeview list, false if it is
     * fisheye.
     * 
     * @return
     */
    public static boolean isSimpleList(Context context) {
        // TODO: put this in a parent class instead of using a static method
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int listtype = prefs.getInt(KEY_LISTTYPE, 0);
        // also say it's a simple list if listtype is treeview, because
        // simple/fisheye is shown as long as user has not selected treeview in
        // the options
        if (listtype != 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if external storage is mounted with read/write access.
     * 
     * @return
     */
    private boolean isExtStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_UPDATEFEEDS:
                ProgressDialog updateProgress = new ProgressDialog(this);
                updateProgress.setMessage("Updating feeds");
                return updateProgress;
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveUpdateTask(outState);
    }

    private void saveUpdateTask(Bundle outState) {
        final UpdateTask task = mUpdateTask;
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);

            outState.putBoolean(STATE_UPDATE_IN_PROGRESS, true);

            mUpdateTask = null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        restoreUpdateTask(state);
    }

    private void restoreUpdateTask(Bundle state) {
        if (state.getBoolean(STATE_UPDATE_IN_PROGRESS)) {
            mUpdateTask = (UpdateTask) new UpdateTask().execute();
        }
    }
    
}

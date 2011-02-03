
package org.far.twoduiproject;

import org.far.twoduiproject.measurement.MeasurementModule;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class NewsList extends ListActivity {

    private DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newslist);

        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        long categoryid = extras != null ? extras.getLong(DatabaseHelper.CATEGORY_ID) : null;

        fillData(categoryid);
        
    }

    private void fillData(long categoryid) {
        Cursor newsitems = mDbHelper.getItemsForCategory((int) categoryid);
        startManagingCursor(newsitems);

        String[] from;
        int[] to;
        int layout;

        // Create an array to specify the fields we want to display in the
        // list
        from = new String[] {
            DatabaseHelper.TITLE
        };

        // and an array of the fields we want to bind those fields to
        if (RSSNewsReader.isSimpleList(getApplicationContext())) {
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
                layout, newsitems, from, to);

        RSSNewsReader.setFishEyeListener(getListView(), getApplicationContext());

        setListAdapter(categoriesAdapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // stop a running measurement
        MeasurementModule.stopMeasurement();
    }
}


package org.far.twoduiproject;

import org.far.twoduiproject.measurement.MeasurementModule;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

/**
 * Demonstrates expandable lists backed by Cursors
 */
public class ExpandableList extends ExpandableListActivity {
    private int mGroupIdColumnIndex;

    private ExpandableListAdapter mAdapter;

    private DatabaseHelper mDbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());

        // Query for categories
        Cursor groupCursor = mDbHelper.getCategories();
        startManagingCursor(groupCursor);

        // Cache the ID column index
        mGroupIdColumnIndex = groupCursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_ID);

        // Set up our adapter
        mAdapter = new MyExpandableListAdapter(groupCursor, this,
                android.R.layout.simple_expandable_list_item_1, R.layout.simplelist_row,
                new String[] {
                    DatabaseHelper.CATEGORY_NAME
                }, new int[] {
                    android.R.id.text1
                }, new String[] {
                    DatabaseHelper.TITLE, DatabaseHelper.PUBDATE
                }, new int[] {
                    R.id.textViewSimpleListRowTitle, R.id.textViewSimpleListRowDate
                });
        setListAdapter(mAdapter);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {
        // // stop measurement
        // MeasurementModule.stopMeasurement(((TextView)
        // v).getText().toString());

        // open article in browser
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDbHelper.getItemLink(id)));
        startActivity(myIntent);
        return true;
    }

    @Override
    protected void onStop() {

        MeasurementModule.destroySession();

        super.onStop();
    }

    public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {

        public MyExpandableListAdapter(Cursor cursor, Context context, int groupLayout,
                int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom,
                int[] childrenTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childrenFrom,
                    childrenTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // Given the group, we return a cursor for all the children within
            // that group
            Cursor newsitems = mDbHelper.getItemsForCategory(groupCursor
                    .getInt(mGroupIdColumnIndex));

            // The returned Cursor MUST be managed by us, so we use Activity's
            // helper
            // functionality to manage it for us.
            startManagingCursor(newsitems);
            return newsitems;
        }

    }
}


package org.far.twoduiproject;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorTreeAdapter.ViewBinder;

/**
 * Demonstrates expandable lists backed by Cursors
 */
public class ExpandableList extends ExpandableListActivity {
    private int mGroupIdColumnIndex;

    private DatabaseHelper mDbHelper;

    private SimpleCursorTreeAdapter mAdapter;

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
        mAdapter = new SimpleCursorTreeAdapter(getApplicationContext(), groupCursor,
                android.R.layout.simple_expandable_list_item_1, new String[] {
                    DatabaseHelper.CATEGORY_NAME
                }, new int[] {
                    android.R.id.text1
                }, R.layout.simplelist_row, new String[] {
                        DatabaseHelper.TITLE, DatabaseHelper.PUBDATE
                }, new int[] {
                        R.id.textViewSimpleListRowTitle, R.id.textViewSimpleListRowDate
                }) {

            @Override
            protected Cursor getChildrenCursor(Cursor groupCursor) {
                // Given the group, we return a cursor for all the children
                // within that group
                Cursor newsitems = mDbHelper.getItemsForCategory(groupCursor
                        .getInt(mGroupIdColumnIndex));

                // The returned Cursor MUST be managed by us, so we use
                // Activity's helper functionality to manage it for us.
                startManagingCursor(newsitems);
                return newsitems;
            }
        };

        // Set a ViewBinder to parse the time to a relative format
        mAdapter.setViewBinder(new ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == cursor.getColumnIndex(DatabaseHelper.PUBDATE)) {
                    TextView v = (TextView) view;
                    v.setText(NewsList.parseDateToLocalRelative(cursor.getLong(cursor
                            .getColumnIndexOrThrow(DatabaseHelper.PUBDATE))));
                    return true;
                }
                return false;
            }
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
        super.onStop();
        // MeasurementModule.destroySession();
    }
}

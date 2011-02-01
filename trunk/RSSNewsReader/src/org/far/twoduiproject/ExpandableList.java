
package org.far.twoduiproject;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.widget.ExpandableListAdapter;
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

        // Cache the ID column index
        mGroupIdColumnIndex = groupCursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_ID);

        // Set up our adapter
        mAdapter = new MyExpandableListAdapter(groupCursor, this,
                android.R.layout.simple_expandable_list_item_1,
                android.R.layout.simple_expandable_list_item_1, new String[] {
                    DatabaseHelper.CATEGORY_NAME
                }, // Category name for group layouts
                new int[] {
                    android.R.id.text1
                }, new String[] {
                    DatabaseHelper.TITLE
                }, // News title for child layouts
                new int[] {
                    android.R.id.text1
                });
        setListAdapter(mAdapter);
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

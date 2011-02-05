
package org.far.twoduiproject;

import java.util.Calendar;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class NewsList extends ListActivity {

    private DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newslist);

        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            long categoryid = extras.getLong(DatabaseHelper.CATEGORY_ID);
            TextView title = (TextView)findViewById(R.id.textViewNewsListTitle);
            title.setText(mDbHelper.getCategoryName(categoryid));
            fillData(categoryid);
        }               

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((CustomListView) getListView()).setFirstRun();
    }

    private void fillData(long categoryid) {
        Cursor newsitems = mDbHelper.getItemsForCategory((int) categoryid);
        startManagingCursor(newsitems);

        String[] from;
        int[] to;
        int layout;

        boolean isSimpleList = RSSNewsReader.isSimpleList(getApplicationContext());
        if (isSimpleList) {
            from = new String[] {
                    DatabaseHelper.TITLE, DatabaseHelper.PUBDATE
            };
            to = new int[] {
                    R.id.textViewSimpleListRowTitle, R.id.textViewSimpleListRowDate
            };
            layout = R.layout.simplelist_row;
        } else {
            from = new String[] {
                DatabaseHelper.TITLE
            };
            to = new int[] {
                R.id.fisheye_item
            };
            layout = R.layout.fisheye_row;
        }

        SimpleCursorAdapter categoriesAdapter = new SimpleCursorAdapter(getApplicationContext(),
                layout, newsitems, from, to);

        if (isSimpleList) {
            categoriesAdapter.setViewBinder(new ViewBinder() {

                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if (columnIndex == cursor.getColumnIndexOrThrow(DatabaseHelper.PUBDATE)) {
                        TextView v = (TextView) view;
                        v.setText(parseDateToLocalRelative(cursor.getLong(cursor
                                .getColumnIndexOrThrow(DatabaseHelper.PUBDATE))));
                        return true;
                    }
                    return false;
                }
            });
        }

        RSSNewsReader.setFishEyeListener(getListView(), getApplicationContext());

        setListAdapter(categoriesAdapter);

        ((CustomListView) getListView()).setFirstRun();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // // stop a running measurement
        // MeasurementModule.stopMeasurement(((TextView)v).getText().toString());

        // open article in browser
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDbHelper.getItemLink(id)));
        startActivity(myIntent);
    }

    public static String parseDateToLocalRelative(long time) {
        Calendar calendar = Calendar.getInstance();
        return DateUtils.getRelativeTimeSpanString(time, calendar.getTimeInMillis(),
                DateUtils.HOUR_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
    }
}


package org.far.twoduiproject;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PreferenceActivity extends Activity {
    /** Called when the activity is first created. */

    public static final String PROVIDER_PREF = "providerpref";

    public static final String NAME_PROVIDER_PREF = "providerprefname";

    private DatabaseHelper mDbHelper = DatabaseHelper.getInstance(this);

    private ArrayList<String> categoryNameList = new ArrayList<String>();

    private ArrayList<Category> categoryList = new ArrayList<Category>();

    private int providerid;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference);

        // For CNN, provider id = 1, For BBC, provider id = 0 - if preference
        // doesn't exist - always pick cnn as default
        providerid = getIntent().getExtras().getInt(DatabaseHelper.PROVIDER_ID);

        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());

        String getCategoryQuery = "select preferences.pref_categoryid,categories.name,preferences.enabled from "
                + DatabaseHelper.PREFERENCE_TABLE
                + " inner join "
                + DatabaseHelper.CATEGORY_TABLE
                + " on "
                + DatabaseHelper.PREFERENCE_TABLE
                + "."
                + DatabaseHelper.PREF_CATEGORY_ID
                + "="
                + DatabaseHelper.CATEGORY_TABLE
                + "."
                + DatabaseHelper.CATEGORY_ID
                + " where " + DatabaseHelper.PREF_PROVIDERID + " = " + providerid + ";";

        Cursor preferenceCategory = mDbHelper.getCategories(getCategoryQuery);

        while (preferenceCategory.moveToNext()) {
            categoryNameList.add(preferenceCategory.getString(1));
            categoryList.add(new Category(preferenceCategory.getInt(0), preferenceCategory
                    .getString(1), preferenceCategory.getInt(2)));
        }

        preferenceCategory.close();

        final ListView preferenceListView = (ListView) findViewById(R.id.prefList);
        preferenceListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, categoryNameList));
        preferenceListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        preferenceListView.setOnItemClickListener(preferenceListViewListener);

        Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setEnabled(false);
        saveButton.setTag(preferenceListView);
        saveButton.setOnClickListener(saveButtonListener);

    }

    private OnItemClickListener preferenceListViewListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            Button saveButton = (Button) findViewById(R.id.save);
            saveButton.setEnabled(true);

        }

    };

    private OnClickListener saveButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String updateQueryEnabled = "";
            String updateQueryDisabled = "";
            String enabledIds = "";
            String disabledIds = "";
            ListView preferenceListView = (ListView) v.getTag();

            // call update method to update the isEnabled flag of the table for
            // each category

            SparseBooleanArray checked = preferenceListView.getCheckedItemPositions();

            for (int i = 0; i < categoryList.size(); i++) {
                if (checked.get(i)) {
                    categoryList.get(i).setEnabled(1);
                    enabledIds = enabledIds + String.valueOf(categoryList.get(i).getCategoryId());
                    enabledIds = enabledIds + ",";

                } else {
                    categoryList.get(i).setEnabled(0);

                    disabledIds = disabledIds + String.valueOf(categoryList.get(i).getCategoryId());
                    disabledIds = disabledIds + ",";

                }
            }

            if (enabledIds.length() != 0) {
                enabledIds = enabledIds.substring(0, enabledIds.length() - 1);
            }
            if (disabledIds.length() != 0) {
                disabledIds = disabledIds.substring(0, disabledIds.length() - 1);
            }

            updateQueryEnabled = "update " + DatabaseHelper.PREFERENCE_TABLE
                    + " set enabled = 1 where pref_categoryid in (" + enabledIds + ") and " + DatabaseHelper.PREF_PROVIDERID + "=" + providerid + ";";
            updateQueryDisabled = "update " + DatabaseHelper.PREFERENCE_TABLE
                    + " set enabled = 0 where pref_categoryid in (" + disabledIds + ") and " + DatabaseHelper.PREF_PROVIDERID + "=" + providerid + ";";

            mDbHelper.changeCategoryState(updateQueryEnabled, updateQueryDisabled);

        }

    };

}

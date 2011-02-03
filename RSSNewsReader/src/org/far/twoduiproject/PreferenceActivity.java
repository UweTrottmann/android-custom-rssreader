package org.far.twoduiproject;


import java.util.ArrayList;

import android.app.ListActivity;
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

public class PreferenceActivity extends ListActivity {
    /** Called when the activity is first created. */
	
	private DatabaseHelper mDbHelper = DatabaseHelper.getInstance(this);
	

    private ArrayList<String> categoryNameList = new ArrayList<String>();
    private ArrayList<Category> categoryList = new ArrayList<Category>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference);
        
        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());
        
        //For CNN, provider id = 1, For BBC, provider id = 2
        mDbHelper.getPreferencesWithProviderId(1);
        Cursor preferenceCursor = mDbHelper.getPreferencesWithProviderId(1);

        
        
        while(preferenceCursor.moveToNext()){
        	categoryNameList.add(preferenceCursor.getString(1));
        	categoryList.add(new Category(preferenceCursor.getInt(2),preferenceCursor.getString(3),preferenceCursor.getInt(4)));
        }
       
        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,categoryNameList));
        final ListView preferenceListView = getListView();
        preferenceListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        preferenceListView.setOnItemClickListener(preferenceListViewListener);
        
       
        
                
        Button saveButton = (Button)findViewById(R.id.save);
        saveButton.setEnabled(false);
        saveButton.setTag(preferenceListView);
        saveButton.setOnClickListener(saveButtonListener);
        
    }
    
    private OnItemClickListener preferenceListViewListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Button saveButton = (Button)findViewById(R.id.save);
			saveButton.setEnabled(true);
			
		}
    	
    };
    

	private OnClickListener saveButtonListener= new OnClickListener(){

    	
		@Override
		public void onClick(View v) {
			
			String updateQueryEnabled = "";
			String updateQueryDisabled ="";
			String enabledIds = "";
			String disabledIds = "";
			ListView preferenceListView = (ListView)v.getTag();
			//call update method to update the isEnabled flag of the table for each category
			
			SparseBooleanArray checked = preferenceListView.getCheckedItemPositions();

			for (int i =0; i<categoryList.size();i++){
				if (checked.get(i)){
					categoryList.get(i).setEnabled(1);
					enabledIds.concat(String.valueOf(i));
					enabledIds.concat(",");
				}
				else
					categoryList.get(i).setEnabled(0);
					disabledIds.concat(String.valueOf(i));
					disabledIds.concat(",");
			}
			

			enabledIds = enabledIds.substring(0, enabledIds.length()-1);
			disabledIds = disabledIds.substring(0,disabledIds.length()-1);
			
			
			updateQueryEnabled = "update " + DatabaseHelper.PREFERENCE_TABLE + " set enabled = 1 where pref_category_id in (" + enabledIds + ")" ;
			updateQueryDisabled = "update " + DatabaseHelper.PREFERENCE_TABLE + " set enabled = 0 where pref_category_id in (" + disabledIds + ")";
			
			mDbHelper.changeCategoryState(updateQueryEnabled, updateQueryDisabled);

			
		}



    	
    	
    };
    
    
}
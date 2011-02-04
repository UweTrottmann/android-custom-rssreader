package org.far.twoduiproject;


import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
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
	
	public static final String PROVIDER_PREF="providerpref";
	public static final String NAME_PROVIDER_PREF = "providerprefname";

	
	private DatabaseHelper mDbHelper = DatabaseHelper.getInstance(this);
	

    private ArrayList<String> categoryNameList = new ArrayList<String>();
    private ArrayList<Category> categoryList = new ArrayList<Category>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference);
        int providerid=0;
        
        SharedPreferences provider = getSharedPreferences(PROVIDER_PREF,Context.MODE_PRIVATE);
        String categoryIds ="";
        
        //For CNN, provider id = 1, For BBC, provider id = 2 - if preference doesn't exist - always pick cnn as default
        if (provider.getString(NAME_PROVIDER_PREF,"cnn")=="cnn")
        	providerid=1;
        else
        	providerid=2;
        
        
        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());
 
        
        String getCategoryQuery = "select preferences.pref_categoryid,categories.name,preferences.enabled from " + DatabaseHelper.PREFERENCE_TABLE + " inner join " + DatabaseHelper.CATEGORY_TABLE + " on "+DatabaseHelper.PREFERENCE_TABLE+"."+DatabaseHelper.PREF_CATEGORY_ID + "="+DatabaseHelper.CATEGORY_TABLE+"."+DatabaseHelper.CATEGORY_ID+" where preferences.Enabled = 1 and "+DatabaseHelper.PREF_PROVIDERID+" = " + providerid + ";";
        
        //String getCategoryQuery = "select pref_categoryid,"
        
       //String getCategoryQuery ="select pref_categoryid,feedpath,enabled from preferences;";
        
        
        Cursor preferenceCategory = mDbHelper.getCategories(getCategoryQuery);
        
        while(preferenceCategory.moveToNext()){
        	categoryNameList.add(preferenceCategory.getString(1));
        	categoryList.add(new Category(preferenceCategory.getInt(0),preferenceCategory.getString(1),preferenceCategory.getInt(2)));
        }
        

        
        final ListView preferenceListView = (ListView)findViewById(R.id.prefList);
        preferenceListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,categoryNameList));
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
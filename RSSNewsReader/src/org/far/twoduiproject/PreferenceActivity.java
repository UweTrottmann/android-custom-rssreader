package org.far.twoduiproject;


import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class PreferenceActivity extends ListActivity {
    /** Called when the activity is first created. */
	
	private DatabaseHelper mDbHelper;
	

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
        
        
        Button saveButton = (Button)findViewById(R.id.save);
        saveButton.setOnClickListener(saveButtonListener);
        
    }
    
    private OnClickListener saveButtonListener= new OnClickListener(){

    	String id="";
    	
		@Override
		public void onClick(View v) {
			
			
			

			
		}


    	
    	
    };
    
    
}
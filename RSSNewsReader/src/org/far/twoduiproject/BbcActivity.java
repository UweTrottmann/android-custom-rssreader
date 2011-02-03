package org.far.twoduiproject;


import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BbcActivity extends Activity {
    /** Called when the activity is first created. */
	

    private DatabaseHelper mDbHelper = DatabaseHelper.getInstance(this);
    private ArrayList<String> BbcNewsItems = new ArrayList<String>();
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbc);
    
        Cursor enabledCategoryIds = mDbHelper.getEnabledCategoryId(2);
        Cursor categoryItems=null;
        
        while (enabledCategoryIds.moveToNext()){
        	categoryItems = mDbHelper.getItemsForCategory(enabledCategoryIds.getInt(0));
        		while (categoryItems.moveToNext()){
        			BbcNewsItems.add(categoryItems.getString(0));
        	}
        }
        
        
        ListView CnnList = (ListView) findViewById(R.id.bbcnewsitems);
        
        
        CnnList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,BbcNewsItems) );
		CnnList.setOnItemClickListener(bbcListItemListener);
        
        
    }
    
    private OnItemClickListener bbcListItemListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
		}
    	
    };
}
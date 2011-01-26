package org.far.twoduiproject;

import java.io.IOException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;

public class RSSNewsReader extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /*
         * All of the following is for testing purposes only, will be replaced with actual frontend (listviews, etc.)
        */
        FeedParser parser = new FeedParser(getApplicationContext());
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        dbHelper.clear();
        dbHelper.addCategory("Technology", 123);
        
        try {
			parser.parseAtomStream(getResources().getAssets().open("bbc_business_atom2.xml"), 123);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
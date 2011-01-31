package org.far.twoduiproject;


import org.far.twoduiproject.BbcActivity;
import org.far.twoduiproject.CnnActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MyNewsReaderSettings extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        
        ImageView cnnlogo = (ImageView)findViewById(R.id.cnn);
        ImageView bbclogo = (ImageView)findViewById(R.id.bbc);
        
        
 
        cnnlogo.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				startActivity(new Intent(MyNewsReaderSettings.this, CnnActivity.class ));
			
			}
        
        });
        
        bbclogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MyNewsReaderSettings.this,BbcActivity.class));
				
			}
        	
        	
        	
        });
        
        
        
    }
}
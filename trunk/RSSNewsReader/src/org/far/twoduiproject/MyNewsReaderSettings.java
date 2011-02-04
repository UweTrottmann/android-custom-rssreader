package org.far.twoduiproject;


import org.far.twoduiproject.BbcActivity;
import org.far.twoduiproject.CnnActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MyNewsReaderSettings extends Activity {
    /** Called when the activity is first created. */
	
	public static final String PROVIDER_PREF="providerpref";
	public static final String NAME_PROVIDER_PREF = "providerprefname";
	SharedPreferences provider;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        provider = getSharedPreferences(PROVIDER_PREF,Context.MODE_PRIVATE);
        ImageView cnnlogo = (ImageView)findViewById(R.id.cnn);
        ImageView bbclogo = (ImageView)findViewById(R.id.bbc);
        
        
 
        cnnlogo.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				Editor editor = provider.edit();
				editor.putString(NAME_PROVIDER_PREF,"cnn");
				editor.commit();
				
				startActivity(new Intent(MyNewsReaderSettings.this, PreferenceActivity.class ));
			
			}
        
        });
        
        bbclogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Editor editor = provider.edit();
				editor.putString(NAME_PROVIDER_PREF, "bbc");
				editor.commit();
				startActivity(new Intent(MyNewsReaderSettings.this,PreferenceActivity.class));
				
			}
        	
        	
        	
        });
        
        
        
    }
}

package org.far.twoduiproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MyNewsReaderSettings extends Activity {
    /** Called when the activity is first created. */

    public static final String PROVIDER_PREF = "providerpref";

    public static final String NAME_PROVIDER_PREF = "providerprefname";

    SharedPreferences provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        provider = getSharedPreferences(PROVIDER_PREF, Context.MODE_PRIVATE);
        ImageView cnnlogo = (ImageView) findViewById(R.id.cnn);
        ImageView bbclogo = (ImageView) findViewById(R.id.bbc);

        cnnlogo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyNewsReaderSettings.this, PreferenceActivity.class);
                i.putExtra(DatabaseHelper.PROVIDER_ID, 1);
                startActivity(i);

            }

        });

        bbclogo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyNewsReaderSettings.this, PreferenceActivity.class);
                i.putExtra(DatabaseHelper.PROVIDER_ID, 0);
                startActivity(i);

            }

        });

    }
}

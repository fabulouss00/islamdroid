package org.islamdroid.athan;

import org.islamdroid.athan.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends Activity {

	private TextView m_TextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        m_TextView = (TextView) findViewById(R.id.tview);
        
	}

}

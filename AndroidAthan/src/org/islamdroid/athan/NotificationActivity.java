package org.islamdroid.athan;

import org.islamdroid.athan.R;
import org.islamdroid.athan.service.AthanService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends Activity {

	private TextView m_TextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_activity_layout);
        String sPrayerNotificationMessage = getIntent().getStringExtra(AthanService.PrayerNotificationMessageKey);
        m_TextView = (TextView) findViewById(R.id.nttview);
        m_TextView.setText(sPrayerNotificationMessage);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
        String sPrayerNotificationMessage = intent.getStringExtra(AthanService.PrayerNotificationMessageKey);
        m_TextView.setText(sPrayerNotificationMessage);
	}

}

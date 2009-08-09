package org.islamdroid.athan.util;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.islamdroid.athan.R;

public class EditPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}

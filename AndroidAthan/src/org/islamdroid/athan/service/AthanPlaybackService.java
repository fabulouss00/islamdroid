package org.islamdroid.athan.service;

import org.islamdroid.athan.util.CONSTANTS;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.islamdroid.athan.R;

public class AthanPlaybackService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setForeground(true);
	}

	private void playAthan() {
		Log.d(CONSTANTS.LOG_TAG, "Inside AthanPlaybackService playAthan");
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Resources res = getResources();
		if (sp.getBoolean(res.getStringArray(R.array.AthanNameEntryValues)[4], false))
	    	return;
		MediaPlayer mp = null;
		if (sp.getBoolean(res.getStringArray(R.array.AthanNameEntryValues)[0], false))
	    	mp = MediaPlayer.create(this, R.raw.AthanMakkah);
		else if (sp.getBoolean(res.getStringArray(R.array.AthanNameEntryValues)[1], false))
	    	mp = MediaPlayer.create(this, R.raw.AthanMadina);
		else if (sp.getBoolean(res.getStringArray(R.array.AthanNameEntryValues)[2], false))
	    	mp = MediaPlayer.create(this, R.raw.AthanAlaqsa);
		else if (sp.getBoolean(res.getStringArray(R.array.AthanNameEntryValues)[3], false))
	    	mp = MediaPlayer.create(this, R.raw.AthanEgypt);
    	if (null != mp) {
    		try {
    			Log.d(CONSTANTS.LOG_TAG, "Playing athan started");
    			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
    					public void onCompletion(MediaPlayer mp) {
    		    			Log.d(CONSTANTS.LOG_TAG, "Playing athan finished");
    						if (sp.getBoolean(getString(R.string.PreferenceDuaKey), true)) {
        		    			Log.d(CONSTANTS.LOG_TAG, "Will play Dua");
								mp = null;
								mp = MediaPlayer.create(AthanPlaybackService.this, R.raw.Dua);
								mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
    			    					public void onCompletion(MediaPlayer mp) {
    			    						stopSelf();
    			    					}
									}
								);
    						}
    						else
    							stopSelf();
    					}
    				}
    			);
    			mp.start();
    		} catch (IllegalStateException ise) {
    			Log.e(CONSTANTS.LOG_TAG, ise.getMessage());
    		//} catch (IOException ioe) {
    			//Log.e(CONSTANTS.LOG_TAG, ioe.getMessage());
    		}
    	}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		playAthan();
	}
}

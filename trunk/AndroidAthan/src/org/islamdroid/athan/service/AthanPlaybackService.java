package org.islamdroid.athan.service;

import org.islamdroid.athan.R;
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

public class AthanPlaybackService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		setForeground(true);
	}

	private void playAthan() {
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Resources res = getResources();
		if (sp.getBoolean(res.getStringArray(R.array.AthanNameEntries)[4], false))
	    	return;
		MediaPlayer mp = null;
		if (sp.getBoolean(res.getStringArray(R.array.AthanNameEntries)[0], false)) {
			Log.d(CONSTANTS.LOG_TAG, "Makkah");
	    	mp = MediaPlayer.create(this, R.raw.AthanMakkah);
		}
		else if (sp.getBoolean(res.getStringArray(R.array.AthanNameEntries)[1], false)) {
			Log.d(CONSTANTS.LOG_TAG, "Madina");
	    	mp = MediaPlayer.create(this, R.raw.AthanMadina);
		}
		else if (sp.getBoolean(res.getStringArray(R.array.AthanNameEntries)[2], false)) {
			Log.d(CONSTANTS.LOG_TAG, "Al-Aqsa");
	    	mp = MediaPlayer.create(this, R.raw.AthanAlaqsa);
		}
		else if (sp.getBoolean(res.getStringArray(R.array.AthanNameEntries)[3], false)) {
			Log.d(CONSTANTS.LOG_TAG, "Egypt");
	    	mp = MediaPlayer.create(this, R.raw.AthanEgypt);
		}
		else
			mp = MediaPlayer.create(this, R.raw.AthanMakkah);
    	if (null != mp) {
    		try {
    			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
    					public void onCompletion(MediaPlayer mp) {
    						if (sp.getBoolean(getString(R.string.PreferenceDuaKey), true)) {
        		    			try {
    								mp = null;
    								mp = MediaPlayer.create(AthanPlaybackService.this, R.raw.Dua);
									mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					    					public void onCompletion(MediaPlayer mp) {
					    						stopSelf();
					    					}
										}
									);
									mp.start();
        		    			} catch (Exception e) {
        		    			}
    						}
    						else
    							stopSelf();
    					}
    				}
    			);
    			mp.start();
    		} catch (IllegalStateException ise) {
    			Log.e(CONSTANTS.LOG_TAG, ise.getMessage());
    		} catch (Exception e) {
    			Log.e(CONSTANTS.LOG_TAG, e.getMessage());
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

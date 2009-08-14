package org.islamdroid.athan.service;

import java.util.Calendar;

import org.islamdroid.athan.NotificationActivity;
import org.islamdroid.athan.location.AthanLocationManager;
import org.islamdroid.athan.location.ILocationChangeListener;
import org.islamdroid.athan.prayertimes.PrayerTime;
import org.islamdroid.athan.prayertimes.PrayerTimeManager;
import org.islamdroid.athan.prayertimes.PrayerTimes;
import org.islamdroid.athan.qiblah.QiblahAngle;
import org.islamdroid.athan.qiblah.QiblahDirectionCalculator;
import org.islamdroid.athan.util.CONSTANTS;
import org.islamdroid.athan.util.Util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.islamdroid.athan.R;
import org.islamdroid.athan.service.IAthanService;

public class AthanService extends Service implements ILocationChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
	
	public static final String PrayerNotificationMessageKey = "PrayerNotificationMessage";
	private final int ATHAN_NOTIFY_ID = 0xF0000000;
	// Member attributes
	private AthanLocationManager m_AthanLocationManager;
	private boolean m_bLocationSet;
	private double m_dLatitude;
	private double m_dLongitude;
	private PrayerTimeManager m_PrayerTimeManager;
	private AlarmManager m_AlarmManager;
	private AlarmHandler m_AlarmHandler;
	private PendingIntent m_PendingIntent;
	private Intent m_CalculatePrayerTimesIntent;
	private Intent m_HandlePrayerTimeIntent;
	private ChangeHandler m_ChangeHandler;
	private String m_sPrayerNameExtraTag;
	private String m_sPrayerTimeExtraTag;
	
	public AthanService() {
		m_sPrayerNameExtraTag = "PrayerName";
		m_sPrayerTimeExtraTag = "PrayerTime";
	}

	private final IAthanService.Stub m_Binder = new IAthanService.Stub() {
	    public PrayerTimes getPrayerTimes() {
	    	return AthanService.this.getPrayerTimes();
	        //return m_PrayerTimeManager.getPrayerTimes();
	    }
	    public String [] getPrayerTimeStrings() {
	    	return AthanService.this.getPrayerTimeStrings();
	    	//return m_PrayerTimeManager.getPrayerTimeStrings();
	    }
	    public PrayerTime getNextPrayerTime() {
	    	return AthanService.this.getNextPrayerTime();
	    }
		public PrayerTimes getPrayerTimesOfDate(int day, int month, int year) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, day);
			c.set(Calendar.MONTH, month);
			c.set(Calendar.YEAR, year);
			return AthanService.this.getPrayerTimes(c);
		}
		public QiblahAngle getQiblahAngle() {
			return AthanService.this.getQiblahAngle();
		}
	};
	
	@Override
	public void onCreate() {
		setForeground(true);
		initialize();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(CONSTANTS.LOG_TAG, "AthanService onBind " + intent.getAction());
		//if (IAthanService.class.getName().equals(intent.getAction()))
			return m_Binder;
		//return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(CONSTANTS.LOG_TAG, "AthanService started");
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.v(CONSTANTS.LOG_TAG, "AthanService stopped");
		super.onDestroy();
	}

	@Override
	public void locationChanged(Location location) {
		m_dLatitude = location.getLatitude();// * 1E6;
		m_dLongitude = location.getLongitude();// * 1E6;
		m_bLocationSet = true;
		reset();
		//playAthan();
		//notifyAthan("Maghrib", Calendar.getInstance().getTimeInMillis());
	}

    @Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.v(CONSTANTS.LOG_TAG, "AthanService pref changed: " + key);
	}

	private void initialize() {
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    	m_PrayerTimeManager = new PrayerTimeManager();
    	m_PendingIntent = null;
    	m_CalculatePrayerTimesIntent = new Intent(getString(R.string.CalculatePrayerTimes));
    	m_HandlePrayerTimeIntent = new Intent(getString(R.string.HandlePrayerTime));
    	m_AlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    	// Add alarm handler
    	m_AlarmHandler = new AlarmHandler();
		registerReceiver(m_AlarmHandler, new IntentFilter(getString(R.string.CalculatePrayerTimes)));
		registerReceiver(m_AlarmHandler, new IntentFilter(getString(R.string.HandlePrayerTime)));
		// Register time and time-zone change listener
		m_ChangeHandler = new ChangeHandler();
		registerReceiver(m_ChangeHandler, new IntentFilter(Intent.ACTION_TIME_CHANGED));
		registerReceiver(m_ChangeHandler, new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED));
		// Add location change listener
		m_bLocationSet = false;
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		m_AthanLocationManager = new AthanLocationManager(locationManager);
		m_AthanLocationManager.addLocationChangeListener(this);
    }

	private void reset() {
		m_AlarmManager.cancel(m_PendingIntent);
		calculatePrayerTimes();
	}
	
	private void testAlarmManager() {
		Intent intent = new Intent(getString(R.string.HandlePrayerTime));
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(c.getTimeInMillis() + 5000);
		this.registerReceiver(m_AlarmHandler, new IntentFilter(getString(R.string.HandlePrayerTime)));
		m_AlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
	}
	
	public PrayerTimes getPrayerTimes() {
		return m_PrayerTimeManager.getPrayerTimes();
	}
	
	public PrayerTimes getPrayerTimes(Calendar day) {
		return (m_bLocationSet) ? m_PrayerTimeManager.getPrayerTimes(day, m_dLatitude, m_dLongitude) : null;
	}
	
	public String [] getPrayerTimeStrings() {
		return m_PrayerTimeManager.getPrayerTimeStrings();
	}
	
	public PrayerTime getNextPrayerTime() {
		return m_PrayerTimeManager.getNextPrayerTime();
	}
	
	private void calculatePrayerTimes() {
		m_PrayerTimeManager.calculatePrayerTimes(m_dLatitude, m_dLongitude);
		String sText = "Lat = " + m_dLatitude + " Long = " + m_dLongitude + "\n";
		PrayerTime pt = m_PrayerTimeManager.getNextPrayerTime();
		if (pt == null) {
			sText += "No next prayer";
			m_PendingIntent = PendingIntent.getBroadcast(this, 0, m_CalculatePrayerTimesIntent, PendingIntent.FLAG_ONE_SHOT);
			Calendar c = Util.getNextDay(Calendar.getInstance());
			m_AlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), m_PendingIntent);
		}
		else {
			//sText += pt.toString();
			m_HandlePrayerTimeIntent.putExtra(m_sPrayerNameExtraTag, pt.getPrayerName());
			m_HandlePrayerTimeIntent.putExtra(m_sPrayerTimeExtraTag, pt.getPrayerTime().getTimeInMillis());
			m_PendingIntent = PendingIntent.getBroadcast(this, 0, m_HandlePrayerTimeIntent, PendingIntent.FLAG_ONE_SHOT);
			sText += m_PendingIntent;
			m_AlarmManager.set(AlarmManager.RTC_WAKEUP, pt.getPrayerTime().getTimeInMillis(), m_PendingIntent);
		}
		Log.v(CONSTANTS.LOG_TAG, sText);
	}
	
	private class AlarmHandler extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v(CONSTANTS.LOG_TAG, "I am from AlarmHandler inside AthanService");
			String sAction = intent.getAction();
			if (getString(R.string.CalculatePrayerTimes) == sAction) {
				Toast.makeText(context, "Got alarm to calculate next prayer", Toast.LENGTH_LONG).show();
				calculatePrayerTimes();
			}
			else if (getString(R.string.HandlePrayerTime) == sAction) {
				handlePrayerTime(intent.getStringExtra(m_sPrayerNameExtraTag),
						intent.getLongExtra(m_sPrayerTimeExtraTag, 0));
			}
			Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show(); 
		}
	}
	
	private void handlePrayerTime(String sPrayerName, long time) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Resources res = getResources();
		if (sp.getBoolean(res.getStringArray(R.array.AthanNotificationEntries)[0], true))
			playAthan();
		else if (sp.getBoolean(res.getStringArray(R.array.AthanNotificationEntries)[1], false))
			notifyAthan(sPrayerName, time);
	}
	
	private void notifyAthan(String sPrayerName, long time) {
		Log.d(CONSTANTS.LOG_TAG, "I am in vibrationAlert");
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(ATHAN_NOTIFY_ID);
		int icon = R.drawable.icon;
		CharSequence tickerText = "Prayer time for " + sPrayerName;
		long when = System.currentTimeMillis() + 2000;

		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = "Athan notification";
		//CharSequence contentText = "Prayer time for " + sPrayer;
		Intent notificationIntent = new Intent(this, NotificationActivity.class);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		String sNotificationMessage = sPrayerName + " at " + c.get(Calendar.HOUR_OF_DAY)
			+ ':' + c.get(Calendar.MINUTE) + ':' + c.get(Calendar.SECOND);
		notificationIntent.putExtra(PrayerNotificationMessageKey, sNotificationMessage);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, tickerText, contentIntent);
		notification.defaults |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		long [] vibrate = { 0, 100, 200, 300 };
		notification.vibrate = vibrate;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.ledARGB = 0xff00ff00;
		notification.ledOnMS = 300;
		notification.ledOffMS = 1000;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		nm.notify(ATHAN_NOTIFY_ID, notification);
	}
	
	public QiblahAngle getQiblahAngle() {
		return (m_bLocationSet) ? QiblahDirectionCalculator.getQiblahDirection(m_dLatitude, m_dLongitude) : null;
	}
	
	private void playAthan() {
    	Intent ServiceIntent = new Intent();
    	ServiceIntent.setClass(this, AthanPlaybackService.class);
    	startService(ServiceIntent);
	}

	private class ChangeHandler extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v(CONSTANTS.LOG_TAG, "I am from ChangeHandler inside AthanService");
			final String sAction = intent.getAction();
			if (Intent.ACTION_TIME_CHANGED == sAction || Intent.ACTION_TIMEZONE_CHANGED == sAction) {
				Toast.makeText(context, "Got time change to calculate next prayer", Toast.LENGTH_LONG).show();
				calculatePrayerTimes();
			}
			Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show(); 
		}
	}
}

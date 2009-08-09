package org.islamdroid.athan;

import java.util.Calendar;

import org.islamdroid.athan.prayertimes.PrayerTime;
import org.islamdroid.athan.prayertimes.PrayerTimes;
import org.islamdroid.athan.qiblah.QiblahAngle;
import org.islamdroid.athan.service.AthanService;
import org.islamdroid.athan.util.CONSTANTS;
import org.islamdroid.athan.util.EditPreferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import org.islamdroid.athan.R;
import org.islamdroid.athan.service.IAthanService;

public class AndroidAthan extends Activity implements OnClickListener {
	private TextView m_TextView;
	private Button m_btnEditPreferences;
	private Button m_btnPrayerTimes;
	private Button m_btnNextPrayerTime;
	private Button m_btnPrayerTimesOnDate;
	private Button m_btnQiblahAngle;
	// Menu ids
	private final int MENU_PREFERENCES = 0;
	// Service
	private IAthanService m_AthanService = null;
	private boolean m_bServiceBound = false;

	private int m_Year;
    private int m_Month;
    private int m_Day;

    static final int DATE_DIALOG_ID = 0;

	/**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection m_Connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            m_AthanService = IAthanService.Stub.asInterface(service);
            m_btnPrayerTimes.setEnabled(true);
        }

        public void onServiceDisconnected(ComponentName className) {
            m_AthanService = null;
            m_btnPrayerTimes.setEnabled(false);
        }
    };

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener m_DateSetListener =
        new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                m_Year = year;
                m_Month = monthOfYear;
                m_Day = dayOfMonth;
        		PrayerTimes pt = getPrayerTimesOnDate();
        		if (null != pt)
            		showPrayerTimesActivity(pt);
            }
        };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        m_TextView = (TextView) findViewById(R.id.tview);
        m_btnEditPreferences = (Button) findViewById(R.id.BTNEditPreferences);
        m_btnEditPreferences.setOnClickListener(this);
        m_btnPrayerTimes = (Button) findViewById(R.id.BTNViewPrayerTimes);
        m_btnPrayerTimes.setOnClickListener(this);
        m_btnPrayerTimes.setEnabled(false);
        m_btnNextPrayerTime = (Button) findViewById(R.id.BTNNextPrayerTime);
        m_btnNextPrayerTime.setOnClickListener(this);
        m_btnPrayerTimesOnDate = (Button) findViewById(R.id.BTNViewPrayerTimeOnDate);
        m_btnPrayerTimesOnDate.setOnClickListener(this);
        m_btnQiblahAngle = (Button) findViewById(R.id.BTNQiblahAngle);
        m_btnQiblahAngle.setOnClickListener(this);
        initialize();
    	playBismillah();
    	test();    	
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this, m_DateSetListener, m_Year, m_Month, m_Day);
        }
        return null;
    }

    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_PREFERENCES, Menu.NONE, "Preferences");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PREFERENCES:
			showPreferenceActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showPreferenceActivity() {
		startActivity(new Intent(this, EditPreferences.class));
	}

	private boolean initialize() {
    	try {
        	Intent ServiceIntent = new Intent();
        	ServiceIntent.setClass(this, AthanService.class);
        	if (null == startService(ServiceIntent)) {
        		Log.e(CONSTANTS.LOG_TAG, "Athan service could not be started");
        		showAlertDialog("Error", "Athan service could not be started");
        		return false;
        	}
    		// get the current date
            final Calendar c = Calendar.getInstance();
            m_Year = c.get(Calendar.YEAR);
            m_Month = c.get(Calendar.MONTH);
            m_Day = c.get(Calendar.DAY_OF_MONTH);
        	m_bServiceBound = bindService(new Intent(AndroidAthan.this, AthanService.class), m_Connection, Context.BIND_AUTO_CREATE);
        	Log.d(CONSTANTS.LOG_TAG, "Service bound: " + m_bServiceBound);
        	return true;
    	} catch (Exception e) {
    		Log.e(CONSTANTS.LOG_TAG, "Could not initialize: " + e.getMessage());
    	}
    	return false;
    }
    
    @Override
	public void onClick(View v) {
    	if (m_btnPrayerTimes == v) {
    		PrayerTimes pt = getPrayerTimes();
    		if (null != pt)
        		showPrayerTimesActivity(pt);
    	}
    	else if (m_btnEditPreferences == v)
			showPreferenceActivity();
    	else if (m_btnNextPrayerTime == v)
    		showNextPrayerTime();
    	else if (m_btnPrayerTimesOnDate == v) {
    		showPrayerTimesOnDate();
    	}
    	else if (m_btnQiblahAngle == v)
    		showQiblahAngle();
	}
    
    private void showQiblahAngle() {
    	String sMessage;
		if (m_bServiceBound) {
			try {
				QiblahAngle qa = m_AthanService.getQiblahAngle();
				if (null == qa)
					sMessage = "Angle can not be calculated as location could not be determined";
				else
					sMessage = qa.toString();
			} catch (RemoteException re) {
				sMessage = "Could not retrieve qiblah angle";
			}
		}
		else
			sMessage = "Could not retrieve qiblah angle";
		showAlertDialog("Qiblah Angle", sMessage);
    }
    
    private void showPrayerTimesOnDate() {
    	showDialog(DATE_DIALOG_ID);
    }
    
    private void showNextPrayerTime() {
    	String sMessage;
		if (m_bServiceBound) {
			try {
				PrayerTime pt = m_AthanService.getNextPrayerTime();
				if (null == pt)
					sMessage = "No more prayer today";
				else
					sMessage = pt.getPrayerName() + " " + pt.getPrayerTime().get(Calendar.HOUR_OF_DAY) +
						':' + pt.getPrayerTime().get(Calendar.MINUTE);
			} catch (RemoteException re) {
				sMessage = "Could not retrieve prayer time";
			}
		}
		else
			sMessage = "Could not retrieve prayer time";
		showAlertDialog("Next prayer time", sMessage);
    }
    
    private void showAlertDialog(String sTitle, String sMessage) {
		new AlertDialog.Builder(this).setTitle(sTitle).setMessage(sMessage)
		.setNeutralButton("Ok",	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
    }
    
    private PrayerTimes getPrayerTimes() {
		if (m_bServiceBound) {
	    	try {
		    	PrayerTimes pt = m_AthanService.getPrayerTimes();
		    	if (null == pt)
		    		showAlertDialog("Preyer times", "Prayer times could not be calculated as location information is unavailable");
		    	return pt;
	    	} catch (RemoteException re) {
	    		showAlertDialog("Preyer times", re.getMessage());
	    		Log.e(CONSTANTS.LOG_TAG, "Could not get prayer times from athan service: " + re.getMessage());
	    	}
		}
		else
			showAlertDialog("Preyer times", "Service is not bound");
		return null;
    }

    private PrayerTimes getPrayerTimesOnDate() {
		if (m_bServiceBound) {
	    	try {
		    	PrayerTimes pt = m_AthanService.getPrayerTimesOfDate(m_Day, m_Month, m_Year);
		    	if (null == pt)
		    		showAlertDialog("Preyer times", "Prayer times could not be calculated as location information is unavailable");
		    	return pt;
	    	} catch (RemoteException re) {
	    		showAlertDialog("Preyer times", re.getMessage());
	    		Log.e(CONSTANTS.LOG_TAG, "Could not get prayer times from athan service: " + re.getMessage());
	    	}
		}
		else
			showAlertDialog("Preyer times", "Service is not bound");
		return null;
    }
    
    private void showPrayerTimesActivity(PrayerTimes pt) {
	    	Intent i = new Intent(getApplicationContext(), PrayerTimesView.class);
	    	i.putExtra(getString(R.string.PrayerTimesExtraTag), pt);
	    	startActivity(i);
	}
    
	private void test() {
    	m_TextView.setText("Hello Android Athan");
    	//Intent ServiceIntent = new Intent();
    	//ServiceIntent.setClass(this, AthanService.class);
    	//startService(ServiceIntent);
    }
	
	private void playBismillah() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		if (sp.getBoolean(getString(R.string.BismillahPreferenceKey), true)) {
	    	MediaPlayer mp = MediaPlayer.create(this, R.raw.Bismillah);
	    	if (null != mp) {
	    		try {
	    			mp.start();
	    		} catch (IllegalStateException ise) {
	    		}
	    	}
		}
	}
}
package org.islamdroid.athan.location;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.islamdroid.athan.util.CONSTANTS;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;


/**
 * A class to get location from system and monitor location changes
 * @author russoue
 * Command to set location in emulator: geo fix -96.7565499 32.9812956 (telnet localhost 5554)
 */
public class AthanLocationManager implements LocationListener {
	// Member attributes
	private List<ILocationChangeListener> m_LocationChangeListeners;
	// Android's location manager
	private LocationManager m_LocationManager;
	// Best location provider
	private String m_sLocationProvider;
	/**
	 * The constructor
	 */
	public AthanLocationManager(LocationManager p_LocationManager) {
		Log.d(CONSTANTS.LOG_TAG, "I am in AthanLocationManager constructor");
		m_LocationChangeListeners = new Vector<ILocationChangeListener> ();
		m_LocationManager = p_LocationManager;
		start();
		//Double lat = gpsLocation.getLatitude() * 1E6;
		//Double lon = gpsLocation.getLongitude() * 1E6; 
	}
	/**
	 * Subscribe to location change notification
	 * @param LocationChangeListener The listener class
	 */
	public void addLocationChangeListener(ILocationChangeListener LocationChangeListener) {
		Log.d(CONSTANTS.LOG_TAG, "I am in AthanLocationManager subscribe method");
		m_LocationChangeListeners.add(LocationChangeListener);
	}
	/**
	 * Must be called to start location service
	 */
	private void start() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		m_sLocationProvider = m_LocationManager.getBestProvider(criteria, true);
		m_LocationManager.requestLocationUpdates(m_sLocationProvider, 1000, 10, this);
		if (!m_LocationManager.isProviderEnabled(m_sLocationProvider))
			Log.d(CONSTANTS.LOG_TAG, m_sLocationProvider + " provider is not enabled");
/*		LocationProvider lP = m_LocationManager.getProvider("gps");
		Location gpsLocation = m_LocationManager.getLastKnownLocation(lP.getName());
		if (null == gpsLocation)
			Log.d("Athan", "Got NULL location!");
		else
			notifyLocationChanges(gpsLocation);*/		
	}
	/**
	 * Notify location change to the subscribers
	 * @param p_Location The new location
	 */
	private void notifyLocationChanges(Location p_Location) {
		Log.d(CONSTANTS.LOG_TAG, "I am in AthanLocationManager notify method");
		Iterator<ILocationChangeListener> iter = m_LocationChangeListeners.iterator();
		while (iter.hasNext())
			//((ILocationChangeListener) iter.next()).locationChanged(p_Location);
			iter.next().locationChanged(p_Location);
	}
	
	public void onLocationChanged(Location p_Location) {
        if (p_Location == null) {
            Log.d(CONSTANTS.LOG_TAG, "location changed to null");
        } else {
            Log.d(CONSTANTS.LOG_TAG, p_Location.toString());
            notifyLocationChanges(p_Location);
        }
    }

	public void onProviderDisabled(String provider) {
		Log.d(CONSTANTS.LOG_TAG, "Location provider: " + provider + " is disabled now");		
	}

	public void onProviderEnabled(String provider) {
		Log.d(CONSTANTS.LOG_TAG, "Location provider: " + provider + " is enabled now");		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		switch (status) {
		case LocationProvider.AVAILABLE:
			Log.d(CONSTANTS.LOG_TAG, "Provider: " + provider + " is available now");		
			break;
		case LocationProvider.OUT_OF_SERVICE:
			Log.d(CONSTANTS.LOG_TAG, "Provider: " + provider + " is out of service");		
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Log.d(CONSTANTS.LOG_TAG, "Provider: " + provider + " is temporarily unavailable");		
			break;
		}
	}
}

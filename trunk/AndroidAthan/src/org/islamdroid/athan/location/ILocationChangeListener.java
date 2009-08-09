package org.islamdroid.athan.location;

import android.location.Location;

/**
 * An interface to implement for classes interested in location change
 * @author russoue
 *
 */
public interface ILocationChangeListener {
	/**
	 * This method is called when location is changed significantly
	 * @param p_Location The new location
	 * @see Location
	 */
	public void locationChanged(Location p_Location);
}

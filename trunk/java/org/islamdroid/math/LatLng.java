package org.islamdroid.math;

/**
 * A class to represent a point on a spherical surface. A point consist of two
 * coordinates, latitude and longitude. Latitudes range from -90 degrees to 
 * 90 degrees. The north pole lies at latitude 90 degrees, the equator at 0 
 * degrees and the south pole at -90 degrees.
 * 
 * Longitudes range from -180 degrees to 180 degrees. The prime meridian lies on
 * longitude 0. The longitudes west of the prime meridian are negative and the
 * longitudes east of the prime meridian point are positive.
 * 
 * @author Hassan Rom <putera.afandy@gmail.com>
 */
public class LatLng {
  
  private double latitude;
  private double longitude;
 
  /**
   * A <code>LatLng</code> constructor.
   * @param latitude in degrees.
   * @param longitude in degrees.
   */
  public LatLng(double latitude, double longitude) {
  	this.latitude = latitude;
  	this.longitude = longitude;
  }
 
  /**
   * Returns the latitude in degrees.
   * @return latitude in degrees.
   */
  public double latitudeInDegrees() {
  	return latitude;
  }
 
  /**
   * Returns the longitude in degrees.
   * @return longitude in degrees.
   */
  public double longitudeInDegrees() {
  	return longitude;
  }
 
  /**
   * Returns the latitude in radians.
   * @return latitude in radians.
   */
  public double latitudeInRadians() {
  	return Math.toRadians(latitude);
  }
 
  /**
   * Returns the longitude in radians.
   * @return longitude in radians.
   */
  public double longitudeInRadians() {
  	return Math.toRadians(longitude);
  }
 
  @Override
  public String toString() {
  	return "Latitude: " + latitude + " Longitude: " + longitude;
  }
  
  @Override
  public boolean equals(Object o) {
  	return (o instanceof LatLng && 
  			((LatLng) o).latitude == latitude &&
  			((LatLng) o).longitude == longitude);
  }
}

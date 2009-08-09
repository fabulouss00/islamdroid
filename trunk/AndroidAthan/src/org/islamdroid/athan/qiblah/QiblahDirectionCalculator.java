package org.islamdroid.athan.qiblah;

public class QiblahDirectionCalculator {
	
	public static QiblahAngle getQiblahDirection(double dLatitude, double dLongitude) {	// Returns angle anti-clockwise
		dLatitude = Math.toRadians(90.0 - dLatitude);		// dLatitude no longer holds the target's latitude, it is used as a temporary variable
		dLongitude = Math.toRadians(dLongitude - 39.8247);	// dLongitude no longer holds the target's longitude, it is used as a temporary variable

		return new QiblahAngle(Math.toDegrees(Math.atan(Math.sin(dLongitude) / (Math.sin(dLatitude) / 2.548948832007665 - Math.cos(dLatitude) * Math.cos(dLongitude)))));
	}
}

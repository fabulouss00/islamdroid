package org.islamdroid.math;

/**
 * Provides utility functions for doing operations on a great circle.
 * 
 * @author Hassan Rom <putera.afandy@gmail.com>
 */
public class GreatCircle {

	private static final double PI_OVER_2 = Math.PI / 2;

	/** Prevents from instantiation of this class. */
	private GreatCircle() {
	}

	/**
	 * Calculates the direction of the shortest path from <code>point1</code> to 
	 * <code>point2</code> (along their great circle). 
	 * <p> 
	 * The direction returned is in radians and with respect to the true north.
	 * 
	 * @param point1 the starting point.
	 * @param point2 the destination point.
	 * @return the direction from <code>point1</code> to <code>point2</code> as 
	 * specified above.
	 */
	public static double direction(LatLng point1, LatLng point2) {
		double dLng = point2.longitudeInRadians() - point1.longitudeInRadians();
		return Math.atan2(Math.sin(dLng), 
				Math.cos(point1.latitudeInRadians()) * Math.tan(point2.latitudeInRadians()) -
				Math.sin(point1.latitudeInRadians()) * Math.cos(dLng));
	}

	/**
	 * Calculates the shortest distance from <code>point1</code> to 
	 * <code>point2</code> on along the great circle of radius <code>radius</code>.
	 * 
	 * @param point1 the starting point.
	 * @param point2 the destination point.
	 * @param radius the radius of the great circle.
	 * @return
	 */
	public static double distance(LatLng point1, LatLng point2, double radius) {
		double phi1 = PI_OVER_2 - point1.latitudeInRadians();
		double phi2 = PI_OVER_2 - point2.latitudeInRadians();
		double dTheta = point2.longitudeInRadians() - point1.longitudeInRadians();
		
		/* Use law of cosines for spherical surfaces to solve for the distance */
		return Math.acos(Math.cos(phi1) * Math.cos(phi2) + 
				Math.sin(phi1) * Math.sin(phi2) * Math.cos(dTheta)) * radius;
	}

	/**
	 * Calculates the destination point given the starting point, the direction, 
	 * the distance to travel and the radius of the great circle.
	 * 
	 * @param point the starting point.
	 * @param direction the direction to move.
	 * @param distance the distance to travel.
	 * @param radius the radius of the great circle.
	 * @return the destination point.
	 */
	public static LatLng destination(LatLng point, double direction, 
			double distance, double radius) {
		
		/* Normalize distance */
		distance = distance / radius;
			
		/* 
		 * Solve for destination's phi using the law of cosines for spherical
		 * surfaces.
		 */
		double phi1 = PI_OVER_2 - point.latitudeInRadians();
		double phi2 = Math.acos(Math.cos(phi1) * Math.cos(distance) + 
				Math.sin(phi1) * Math.sin(distance) * Math.cos(direction));
		
		/*
		 * Solve for destination's theta using the law of cosines for spherical 
		 * surfaces.
		 */
		double numerator = Math.cos(distance) - Math.cos(phi1) * Math.cos(phi2);
		double denominator = Math.sin(phi1) * Math.sin(phi2);
		double dTheta = Math.acos(numerator / denominator);
		double theta2 = direction < 0 ? (point.longitudeInRadians() - dTheta) :
			(point.longitudeInRadians() + dTheta);
		
		/* Convert destination's coordinates to latitude and longitude */
		double lat2 = Math.toDegrees(PI_OVER_2 - phi2);
		double lng2 = Math.toDegrees(theta2);
		return new LatLng(lat2, lng2);
	}

	/**
	 * Calculates the waypoint <code>ratio</code> way in between 
	 * <code>point1</code> and <code>point2</code>. 
	 * 
	 * @param point1 the starting point.
	 * @param point2 the destination point.
	 * @param ratio the factor to be multiplied by the distance between the 
	 * starting point and the destination point.
	 * @return The waypoint.
	 */
	public static LatLng waypoint(LatLng point1, LatLng point2, double ratio) {
		/* Calculate the ratio distance between point1 and point2 */
		double distance = distance(point1, point2, 1) * ratio;
		
		/* Calculate the direction of the shortest path from point1 to point2 */
		double direction = direction(point1, point2);
		
		return destination(point1, direction, distance, 1);
	}

	/**
	 * Calculates <code>n</code> equidistant waypoints in between 
	 * <code>point1</code> and <code>point2</code>.
	 * 
	 * @param point1 the starting point.
	 * @param point2 the destination point.
	 * @param n the number of waypoints to compute.
	 * @return an array of <code>n</code> waypoints.
	 */
	public static LatLng[] NWaypoints(LatLng point1, LatLng point2, int n) {
		LatLng[] waypoints = new LatLng[n];
		
		/* Calculate the waypoints */
		double dRatio = (double) 1 / (n + 1);
		double ratio = dRatio;
		for (int i = 0; i < n; i++) {
			waypoints[i] = waypoint(point1, point2, ratio);
			ratio += dRatio;
		}
		return waypoints;
	}
}

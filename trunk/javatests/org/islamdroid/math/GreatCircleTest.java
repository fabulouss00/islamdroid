package org.islamdroid.math;

import junit.framework.TestCase;

/**
 * A unit test for <code>GreatCircle</code>.
 * 
 * @author Hassan Rom <putera.afandy@gmail.com>
 */
public class GreatCircleTest extends TestCase {

	private static final double EARTH_RADIUS = 6372.795477598;
	
	LatLng kaabah = new LatLng(21.42252, 39.82621);
	LatLng home = new LatLng(30.6186, -96.3361);

	public void testDirection() {
		double dir = GreatCircle.direction(home, kaabah);
		assertTrue(Math.abs(44.49 - Math.toDegrees(dir)) < 0.01);
	}

	public void testDistance() {
		double distance = GreatCircle.distance(kaabah, home, EARTH_RADIUS);
		assertTrue(Math.abs(12576.0 - distance) < 1);
	}

	public void testDestination() {
		double dir = GreatCircle.direction(home, kaabah);
		double distance = GreatCircle.distance(home, kaabah, EARTH_RADIUS);
		LatLng dest = GreatCircle.destination(home, dir, distance, EARTH_RADIUS);
		
		assertTrue(Math.abs(dest.latitudeInDegrees() - kaabah.latitudeInDegrees()) 
				< 0.00001);
		assertTrue(Math.abs(dest.longitudeInDegrees() - kaabah.longitudeInDegrees())
				< 0.00001);
	}
	
	private static LatLng testWaypointHelper(LatLng point1, LatLng point2, 
			double ratio) {
		LatLng waypoint = GreatCircle.waypoint(point1, point2, ratio);
		
		double fromPoint1 = GreatCircle.distance(point1, waypoint, 1);
		double fromPoint2 = GreatCircle.distance(waypoint, point2, 1);
	
		assertTrue(Math.abs((fromPoint1/(fromPoint1 + fromPoint2)) - ratio) < 0.001);
		
		/* 
		 * Check the direction: see if the direction from the waypoint to point1 is 
		 * the opposite of the direction from the waypoint to point2 
		 */
		double dir1 = GreatCircle.direction(waypoint, point1);
		double dir2 = GreatCircle.direction(waypoint, point2);
		
		/* Convert the directions into positives */
		if (dir1 < 0) {
			dir1 += (Math.PI * 2);
		}
		
		if (dir2 < 0) {
			dir2 += (Math.PI * 2);
		}
		assertTrue((Math.abs(dir1 - dir2) - Math.PI) < 0.00001);
		
		return waypoint;
	}

	public void testWaypoint() {
		testWaypointHelper(home, kaabah, 0.5);
	}

	public void testNWaypoints() {
		int n = 3;
		LatLng waypoints[] = GreatCircle.NWaypoints(home, kaabah, n);
		assertEquals(n, waypoints.length);
		for (int i = 1; i <= n; i++) {
			double ratio = (double) i / (n + 1);
			LatLng waypoint = testWaypointHelper(home, kaabah, ratio);
			assertTrue(waypoint.equals(waypoints[i-1]));
		}
	}
}
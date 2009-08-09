package org.islamdroid.athan.util;

import java.util.Calendar;

public class Util {
	
	public static Calendar getNextDay(Calendar c) {
		c.add(Calendar.DAY_OF_YEAR, 1);
		c.add(Calendar.HOUR_OF_DAY, 0);
		c.add(Calendar.MINUTE, 0);
		c.add(Calendar.SECOND, 0);
		
		return c;
	}
}

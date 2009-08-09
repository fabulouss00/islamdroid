package org.islamdroid.athan.prayertimes;

import java.util.Calendar;

public class PrayerTimeCalculator {
	// Calculation Methods
	public static final int JAFARI = 0;
	public static final int KARACHI = 1;
	public static final int ISNA = 2;
	public static final int MWL = 3;
	public static final int MAKKAH = 4;
	public static final int EGYPT = 5;
	public static final int TEHRAN = 6;
	public static final int CUSTOM = 7;
	// Juristic Methods
	public static final int SHAFII     = 0;    // Shafii (standard)
	public static final int HANAFI     = 1;    // Hanafi
	// Adjusting Methods for Higher Latitudes
	public static final int NONE       = 0;    // No adjustment
	public static final int MID_NIGHT   = 1;    // middle of night
	public static final int ONE_SEVENTH = 2;    // 1/7th of night
	public static final int ANGLE_BASED = 3;    // angle/60th of night
	private double m_dLatitude;
	private double m_dLongitude;
	private int m_iTimeZone;
	private double m_dJulianDate;
	private int m_iCalcMethod;
	private int m_iAsrJuristic;
	private int m_iDhuhrMinutes;		// minutes after mid-day for Dhuhr
	private int m_iAdjustHighLats;	// adjusting method for higher latitudes
	private MethodParams m_MethodParams[];
	
	public PrayerTimeCalculator() {
		m_iCalcMethod   = ISNA;		// caculation method
		m_iAsrJuristic  = SHAFII;		// Juristic method for Asr
		m_iDhuhrMinutes = 0;		// minutes after mid-day for Dhuhr
		m_iAdjustHighLats = MID_NIGHT;	// adjusting method for higher latitudes
		m_MethodParams = new MethodParams[8];
		m_MethodParams[JAFARI] = new MethodParams(16, false, 4, false, 14);
		m_MethodParams[KARACHI] = new MethodParams(18, true, 0, false, 18);
		m_MethodParams[ISNA] = new MethodParams(15, true, 0, false, 15);
		m_MethodParams[MWL] = new MethodParams(18, true, 0, false, 17);
		m_MethodParams[MAKKAH] = new MethodParams(19, true, 0, true, 90);
		m_MethodParams[EGYPT] = new MethodParams(19.5, true, 0, false, 17.5);
		m_MethodParams[TEHRAN] = new MethodParams(17.7, false, 4.5, false, 15);
		m_MethodParams[CUSTOM] = new MethodParams(18, true, 0, false, 17);
	}
	
	public int getAsrJuristic() {
		return m_iAsrJuristic;
	}

	public void setAsrJuristic(int asrJuristic) {
		m_iAsrJuristic = asrJuristic;
	}

	public int getDhuhrMinutes() {
		return m_iDhuhrMinutes;
	}

	public void setDhuhrMinutes(int dhuhrMinutes) {
		m_iDhuhrMinutes = dhuhrMinutes;
	}

	public int getAdjustHighLats() {
		return m_iAdjustHighLats;
	}

	public void setAdjustHighLats(int adjustHighLats) {
		m_iAdjustHighLats = adjustHighLats;
	}
	
	public PrayerTimes getPrayerTimes(Calendar day, double dLatitude, double dLongitude) {
		return getPrayerTimes(day.get(Calendar.YEAR), day.get(Calendar.MONTH) + 1, day.get(Calendar.DAY_OF_MONTH), dLatitude, dLongitude,
				day.getTimeZone().getOffset(day.getTimeInMillis()) / 3600000);
	}

	private PrayerTimes getPrayerTimes(int year, int month, int day, double dLatitude, double dLongitude, int iTimeZone)
	{
		System.out.println("Got time zone: " + iTimeZone);
		iTimeZone = -5;
		m_dLatitude = dLatitude;
		m_dLongitude = dLongitude; 
		m_iTimeZone = iTimeZone; 
		m_dJulianDate = julianDate(year, month, day) - dLongitude / (15 * 24);
		return computeDayTimes();
	}

	// calculate julian date from a calendar date
	private double julianDate(int year, int month, int day)
	{
		if (month <= 2) {
			year -= 1;
			month += 12;
		}
		//double A = Math.floor(year / 100);
		//A = 2 - A + Math.floor(A / 4);
		int A = year / 100;
		A = 2 - A + A / 4;

		return Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + A - 1524.5;
		//return JD;
	}

	// compute prayer times at given julian date
	private PrayerTimes computeDayTimes()
	{
		//var times = new Array(5, 6, 12, 13, 18, 18, 18); //default times
		PrayerTimesInDouble times = new PrayerTimesInDouble();
		//for (var i=1; i<=this.numIterations; i++)   
		times = computeTimes(times);
		times = adjustTimes(times);
		
		return getPrayerTimesFromDouble(times);
	}

	// convert times array to given time format
	private PrayerTimes getPrayerTimesFromDouble(PrayerTimesInDouble times)
	{
		PrayerTimes pt = new PrayerTimes(); 

		setTime24FromDouble(pt.getFajr(), times.getFajr());
		setTime24FromDouble(pt.getSunrise(), times.getSunrise());
		setTime24FromDouble(pt.getDhuhr(), times.getDhuhr());
		setTime24FromDouble(pt.getAsr(), times.getAsr());
		setTime24FromDouble(pt.getSunset(), times.getSunset());
		setTime24FromDouble(pt.getMaghrib(), times.getMaghrib());
		setTime24FromDouble(pt.getIsha(), times.getIsha());

		return pt;
	}

	// convert float hours to 24h format
	private void setTime24FromDouble(Calendar c, double time)
	{
		time = fixhour(time + 0.5 / 60.0);  // add 0.5 minutes to round
		//int hours = (int) Math.floor(time);
		int hours = (int) time;
		c.set(Calendar.HOUR_OF_DAY, hours); 
		//c.set(Calendar.MINUTE, (int) Math.floor((time - hours) * 60));
		c.set(Calendar.MINUTE, (int) ((time - hours) * 60));
		c.set(Calendar.SECOND, 0);
	}

	// adjust times in a prayer time array
	private PrayerTimesInDouble adjustTimes(PrayerTimesInDouble times)
	{
		times.setFajr(times.getFajr() + m_iTimeZone - m_dLongitude / 15.0);
		times.setSunrise(times.getSunrise() + m_iTimeZone - m_dLongitude / 15.0);
		times.setDhuhr(times.getDhuhr() + m_iTimeZone - m_dLongitude / 15.0);
		times.setAsr(times.getAsr() + m_iTimeZone - m_dLongitude / 15.0);
		times.setSunset(times.getSunset() + m_iTimeZone - m_dLongitude / 15.0);
		times.setMaghrib(times.getMaghrib() + m_iTimeZone - m_dLongitude / 15.0);
		times.setIsha(times.getIsha() + m_iTimeZone - m_dLongitude / 15.0);
		times.setDhuhr(times.getDhuhr() + m_iDhuhrMinutes / 60.0); //Dhuhr
		if (m_MethodParams[m_iCalcMethod].getMaghribSelector()) // Maghrib
			times.setMaghrib(times.getSunset() + m_MethodParams[m_iCalcMethod].getMaghribParameterValue() / 60.0);
		if (m_MethodParams[m_iCalcMethod].getIshaSelector()) // Isha
			times.setIsha(times.getMaghrib()+ m_MethodParams[m_iCalcMethod].getIshaParameterValue() / 60.0);

		if (NONE != m_iAdjustHighLats)
			times = adjustHighLatTimes(times);
		return times;
	}

	// adjust Fajr, Isha and Maghrib for locations in higher latitudes
	private PrayerTimesInDouble adjustHighLatTimes(PrayerTimesInDouble times)
	{
		double nightTime = timeDiff(times.getSunset(), times.getSunrise()); // sunset to sunrise

		// Adjust Fajr
		double FajrDiff = nightPortion(m_MethodParams[m_iCalcMethod].getFajrAngle()) * nightTime;
		if (timeDiff(times.getFajr(), times.getSunrise()) > FajrDiff) 
			times.setFajr(times.getSunrise() - FajrDiff);

		// Adjust Isha
		double IshaAngle = (m_MethodParams[m_iCalcMethod].getIshaSelector() == false) ? m_MethodParams[m_iCalcMethod].getIshaParameterValue() : 18;
		double IshaDiff = nightPortion(IshaAngle) * nightTime;
		if (timeDiff(times.getSunset(), times.getIsha()) > IshaDiff) 
			times.setIsha(times.getSunset() + IshaDiff);

		// Adjust Maghrib
		double MaghribAngle = (m_MethodParams[m_iCalcMethod].getMaghribSelector() == false) ? m_MethodParams[m_iCalcMethod].getMaghribParameterValue() : 4;
		double MaghribDiff = nightPortion(MaghribAngle) * nightTime;
		if (timeDiff(times.getSunset(), times.getMaghrib()) > MaghribDiff) 
			times.setMaghrib(times.getSunset() + MaghribDiff);
		
		return times;
	}

	// compute prayer times at given julian date
	private PrayerTimesInDouble computeTimes(PrayerTimesInDouble times)
	{
		times = dayPortion(times);

		times.setFajr(computeTime(180.0 - m_MethodParams[m_iCalcMethod].getFajrAngle(), times.getFajr()));
		times.setSunrise(computeTime(180.0 - 0.833, times.getSunrise()));
		times.setDhuhr(computeMidDay(times.getDhuhr()));
		times.setAsr(computeAsr(1 + m_iAsrJuristic, times.getAsr()));
		times.setSunset(computeTime(0.833, times.getSunset()));
		times.setMaghrib(computeTime(m_MethodParams[m_iCalcMethod].getMaghribParameterValue(), times.getMaghrib()));
		times.setIsha(computeTime(m_MethodParams[m_iCalcMethod].getIshaParameterValue(), times.getIsha()));

		return times;
	}

	private double computeAsr(int step, double t)  // Shafii: step=1, Hanafi: step=2
	{
		double D = sunDeclination(m_dJulianDate + t);
		D = -darccot(step + dtan(Math.abs(m_dLatitude - D)));
		
		return computeTime(D, t);
	}

	// compute time for a given angle G
	private double computeTime(double G, double t)
	{
		double D = sunDeclination(m_dJulianDate + t);
		double Z = computeMidDay(t);
		D = 1 / 15.0 * darccos((-dsin(G)- dsin(D)* dsin(m_dLatitude)) / 
				(dcos(D)* dcos(m_dLatitude)));
		return Z + ((G > 90.0) ? -D : D);
	}

	// compute mid-day (Dhuhr, Zawal) time
	private double computeMidDay(double t)
	{
		return fixhour(12 - equationOfTime(m_dJulianDate + t));
	}

	// compute equation of time
	private double equationOfTime(double jd)
	{
		return sunPosition(jd).getEquationOfTime();
	}

	// compute declination angle of sun
	private double sunDeclination(double jd)
	{
		return sunPosition(jd).getSunDeclination();
	}

	// compute declination angle of sun and equation of time
	private SDEOT sunPosition(double jd)
	{
		double D = jd - 2451545.0;
		double g = fixangle(357.529 + 0.98560028 * D);
		double q = fixangle(280.459 + 0.98564736 * D);
		double L = fixangle(q + 1.915 * dsin(g) + 0.020 * dsin(2 * g));

		//double R = 1.00014 - 0.01671* dcos(g) - 0.00014* dcos(2*g);
		g = 23.439 - 0.00000036 * D;

		D = darcsin(dsin(g) * dsin(L));
		g = darctan2(dcos(g) * dsin(L), dcos(L)) / 15.0;
		L = q / 15.0 - fixhour(g);

		return new SDEOT(D, L);
	}

	// set the calculation method 
	public void setCalcMethod(int methodID)
	{
		m_iCalcMethod = methodID;
	}

	// the night portion used for adjusting times in higher latitudes
	private double nightPortion(double angle)
	{
		if (m_iAdjustHighLats == ANGLE_BASED)
			return 1 / 60.0 * angle;
		if (m_iAdjustHighLats == MID_NIGHT)
			return 0.5;
		if (m_iAdjustHighLats == ONE_SEVENTH)
			return 0.142857;
		return 0;
	}

	// compute the difference between two times 
	private double timeDiff(double time1, double time2)
	{
		return fixhour(time2 - time1);
	}

	// convert hours to day portions 
	private PrayerTimesInDouble dayPortion(PrayerTimesInDouble times)
	{
		times.setFajr(times.getFajr() / 24);
		times.setSunrise(times.getSunrise() / 24);
		times.setDhuhr(times.getDhuhr() / 24);
		times.setAsr(times.getAsr() / 24);
		times.setSunset(times.getSunset() / 24);
		times.setMaghrib(times.getMaghrib() / 24);
		times.setIsha(times.getIsha() / 24);

		return times;
	}
	
	// Trigonometric methods
	
	// degree sin
	private double dsin(double d)
	{
	    return Math.sin(dtr(d));
	}

	// degree cos
	private double dcos(double d)
	{
	    return Math.cos(dtr(d));
	}

	// degree tan
	private double dtan(double d)
	{
	    return Math.tan(dtr(d));
	}

	// degree arcsin
	private double darcsin(double x)
	{
	    return rtd(Math.asin(x));
	}

	// degree arccos
	private double darccos(double x)
	{
	    return rtd(Math.acos(x));
	}

	// degree arctan
/*	private double darctan(double x)
	{
	    return rtd(Math.atan(x));
	}*/

	// degree arctan2
	private double darctan2(double y, double x)
	{
	    return rtd(Math.atan2(y, x));
	}

	// degree arccot
	private double darccot(double x)
	{
	    return rtd(Math.atan(1/x));
	}

	// degree to radian
	private double dtr(double d)
	{
	    return (d * Math.PI) / 180.0;
	}

	// radian to degree
	private double rtd(double r)
	{
	    return (r * 180.0) / Math.PI;
	}

	// range reduce angle in degrees.
	private double fixangle(double a)
	{
		a = a - 360.0 * (Math.floor(a / 360.0));
		if (a < 0)
			a += 360.0;

		return a;
	}

	// range reduce hours to 0..23
	private double fixhour(double a)
	{
		a = a - 24.0 * (Math.floor(a / 24.0));
		if (a < 0)
			a += 24.0;

		return a;
	}

	// Inner classes

	public static class PrayerTimesInDouble {
		private double m_dFajr;
		private double m_dSunrise;
		private double m_dDhuhr;
		private double m_dAsr;
		private double m_dSunset;
		private double m_dMaghrib;
		private double m_dIsha;
		
		public PrayerTimesInDouble() {
			// Default Fajr Time
			m_dFajr = 5;
			// Default Sunrise Time
			m_dSunrise = 6;
			// Default Dhuhr Time
			m_dDhuhr = 12;
			// Default Asr Time
			m_dAsr = 13;
			// Default Sunset Time
			m_dSunset = 18;
			// Default Maghrib Time
			m_dMaghrib = 18;
			// Default Isha Time
			m_dIsha = 18;
		}
		
		public double getFajr() {
			return m_dFajr;
		}

		public void setFajr(double fajr) {
			m_dFajr = fajr;
		}

		public double getSunrise() {
			return m_dSunrise;
		}

		public void setSunrise(double sunrise) {
			m_dSunrise = sunrise;
		}

		public double getDhuhr() {
			return m_dDhuhr;
		}

		public void setDhuhr(double dhuhr) {
			m_dDhuhr = dhuhr;
		}

		public double getAsr() {
			return m_dAsr;
		}

		public void setAsr(double asr) {
			m_dAsr = asr;
		}

		public double getSunset() {
			return m_dSunset;
		}

		public void setSunset(double sunset) {
			m_dSunset = sunset;
		}

		public double getMaghrib() {
			return m_dMaghrib;
		}

		public void setMaghrib(double maghrib) {
			m_dMaghrib = maghrib;
		}

		public double getIsha() {
			return m_dIsha;
		}

		public void setIsha(double isha) {
			m_dIsha = isha;
		}
	}
	
	private class MethodParams {
		double m_dFajrAngle;
		boolean m_bMaghribSelector;
		double m_dMaghribParameterValue;
		boolean m_bIshaSelector;
		double m_dIshaParameterValue;
		
		public MethodParams(double dFajrAngle, boolean bMaghribSelector, double dMaghribParameterValue,
				boolean bIshaSelector, double dIshaParameterValue) {
			m_dFajrAngle = dFajrAngle;
			m_bMaghribSelector = bMaghribSelector;
			m_dMaghribParameterValue = dMaghribParameterValue;
			m_bIshaSelector = bIshaSelector;
			m_dIshaParameterValue = dIshaParameterValue;
		}
		
		public double getFajrAngle() {
			return m_dFajrAngle;
		}
		public void setFajrAngle(double fajrAngle) {
			m_dFajrAngle = fajrAngle;
		}
		public boolean getMaghribSelector() {
			return m_bMaghribSelector;
		}
		public void setMaghribSelector(boolean maghribSelector) {
			m_bMaghribSelector = maghribSelector;
		}
		public double getMaghribParameterValue() {
			return m_dMaghribParameterValue;
		}
		public void setMaghribParameterValue(double maghribParameterValue) {
			m_dMaghribParameterValue = maghribParameterValue;
		}
		public boolean getIshaSelector() {
			return m_bIshaSelector;
		}
		public void setIshaSelector(boolean ishaSelector) {
			m_bIshaSelector = ishaSelector;
		}
		public double getIshaParameterValue() {
			return m_dIshaParameterValue;
		}
		public void setIshaParameterValue(double ishaParameterValue) {
			m_dIshaParameterValue = ishaParameterValue;
		}
	}
	
	private class SDEOT {
		
		private double m_SunDeclination;
		private double m_EquationOfTime;

		public SDEOT(double sunDeclination, double equationOfTime) {
			m_SunDeclination = sunDeclination;
			m_EquationOfTime = equationOfTime;
		}
		
		public double getSunDeclination() {
			return m_SunDeclination;
		}
		
		public void setSunDeclination(double sunDeclination) {
			m_SunDeclination = sunDeclination;
		}
		
		public double getEquationOfTime() {
			return m_EquationOfTime;
		}
		
		public void setEquationOfTime(double equationOfTime) {
			m_EquationOfTime = equationOfTime;
		}
	}
	
	// Debug methods
	private void printPrayerTimesInDouble(PrayerTimesInDouble times) {
		System.out.println("Fajr: " + times.getFajr());
		System.out.println("Sunrise: " + times.getSunrise());
		System.out.println("Dhuhr: " + times.getDhuhr());
		System.out.println("Asr: " + times.getAsr());
		System.out.println("Sunset: " + times.getSunset());
		System.out.println("Maghrib: " + times.getMaghrib());
		System.out.println("Isha: " + times.getIsha());
	}
}

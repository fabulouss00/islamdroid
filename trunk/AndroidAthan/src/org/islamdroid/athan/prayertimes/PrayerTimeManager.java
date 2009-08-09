package org.islamdroid.athan.prayertimes;

import java.util.Calendar;

import org.islamdroid.athan.util.CONSTANTS;


public class PrayerTimeManager {
	private PrayerTimes m_PrayerTimes;
	private PrayerTimeCalculator m_PrayerTimeCalculator;
	
	/**
	 * The constructor
	 */
	public PrayerTimeManager() {
		m_PrayerTimeCalculator = new PrayerTimeCalculator();
		m_PrayerTimes = null;
	}
	
	public void calculatePrayerTimes(double dLatitude, double dLongitude) {
		m_PrayerTimes = null;
		m_PrayerTimes = m_PrayerTimeCalculator.getPrayerTimes(Calendar.getInstance(), dLatitude, dLongitude);
		System.out.println(m_PrayerTimes);
	}

	/**
	 * This method returns null if no prayer time has been calculated ever
	 * @return an instance of PrayerTimes if prayer times were ever calculated, otherwise null
	 */
	public PrayerTimes getPrayerTimes() {
		return m_PrayerTimes;
	}
	
	public PrayerTimes getPrayerTimes(Calendar day, double dLatitude, double dLongitude) {
		return m_PrayerTimeCalculator.getPrayerTimes(day, dLatitude, dLongitude);
	}
	
	public String [] getPrayerTimeStrings() {
		String [] sPrayerTimes = new String[7];
		sPrayerTimes[0] = m_PrayerTimes.getFajr().get(Calendar.HOUR_OF_DAY) + ":" + m_PrayerTimes.getFajr().get(Calendar.MINUTE);
		sPrayerTimes[1] = m_PrayerTimes.getSunrise().get(Calendar.HOUR_OF_DAY) + ":" + m_PrayerTimes.getSunrise().get(Calendar.MINUTE);
		sPrayerTimes[2] = m_PrayerTimes.getDhuhr().get(Calendar.HOUR_OF_DAY) + ":" + m_PrayerTimes.getDhuhr().get(Calendar.MINUTE);
		sPrayerTimes[3] = m_PrayerTimes.getAsr().get(Calendar.HOUR_OF_DAY) + ":" + m_PrayerTimes.getAsr().get(Calendar.MINUTE);
		sPrayerTimes[4] = m_PrayerTimes.getSunset().get(Calendar.HOUR_OF_DAY) + ":" + m_PrayerTimes.getSunset().get(Calendar.MINUTE);
		sPrayerTimes[5] = m_PrayerTimes.getMaghrib().get(Calendar.HOUR_OF_DAY) + ":" + m_PrayerTimes.getMaghrib().get(Calendar.MINUTE);
		sPrayerTimes[6] = m_PrayerTimes.getIsha().get(Calendar.HOUR_OF_DAY) + ":" + m_PrayerTimes.getIsha().get(Calendar.MINUTE);
		
		return sPrayerTimes;
	}
	
	public PrayerTime getNextPrayerTime() {
		if (null == m_PrayerTimes)
			return null;
		Calendar now = Calendar.getInstance();
		//now.set(Calendar.HOUR_OF_DAY, 23);
		System.out.println("Now: " + now.get(Calendar.MONTH) +
				"/" + now.get(Calendar.DAY_OF_MONTH) +
				"/"	+ now.get(Calendar.YEAR) +
				" " + now.get(Calendar.HOUR_OF_DAY) +
				":"	+ now.get(Calendar.MINUTE) +
				":" + now.get(Calendar.SECOND));
		Calendar ptime = m_PrayerTimes.getFajr();
		if (now.before(ptime))
			return new PrayerTime(CONSTANTS.PRAYERS.FAJR, ptime);
		
		ptime = m_PrayerTimes.getDhuhr();
		if (now.before(ptime))
			return new PrayerTime(CONSTANTS.PRAYERS.DHUHR, ptime);
		
		ptime = m_PrayerTimes.getAsr();
		if (now.before(ptime))
			return new PrayerTime(CONSTANTS.PRAYERS.ASR, ptime);
		
		ptime = m_PrayerTimes.getMaghrib();
		if (now.before(ptime))
			return new PrayerTime(CONSTANTS.PRAYERS.MAGHRIB, ptime);
		
		ptime = m_PrayerTimes.getIsha();
		if (now.before(ptime))
			return new PrayerTime(CONSTANTS.PRAYERS.ISHA, ptime);
		
		return null;
	}
}

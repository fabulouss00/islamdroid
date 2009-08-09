package org.islamdroid.athan.service;

import org.islamdroid.athan.prayertimes.PrayerTime;
import org.islamdroid.athan.prayertimes.PrayerTimes;
import org.islamdroid.athan.qiblah.QiblahAngle;

interface IAthanService {
	PrayerTimes getPrayerTimes();
	PrayerTimes getPrayerTimesOfDate(int day, int month, int year);
	PrayerTime getNextPrayerTime();
	QiblahAngle getQiblahAngle();
	String [] getPrayerTimeStrings();
}
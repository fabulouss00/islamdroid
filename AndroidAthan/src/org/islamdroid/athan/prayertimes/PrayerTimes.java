package org.islamdroid.athan.prayertimes;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class PrayerTimes implements Parcelable {
	private Calendar m_Fajr;
	private Calendar m_Sunrise;
	private Calendar m_Dhuhr;
	private Calendar m_Asr;
	private Calendar m_Sunset;
	private Calendar m_Maghrib;
	private Calendar m_Isha;
	
	public PrayerTimes() {
		m_Fajr = Calendar.getInstance();
		m_Sunrise = Calendar.getInstance();
		m_Dhuhr = Calendar.getInstance();
		m_Asr = Calendar.getInstance();
		m_Sunset = Calendar.getInstance();
		m_Maghrib = Calendar.getInstance();
		m_Isha = Calendar.getInstance();
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(m_Fajr);
		dest.writeSerializable(m_Sunrise);
		dest.writeSerializable(m_Dhuhr);
		dest.writeSerializable(m_Asr);
		dest.writeSerializable(m_Sunset);
		dest.writeSerializable(m_Maghrib);
		dest.writeSerializable(m_Isha);		
	}
	
	public static final Parcelable.Creator<PrayerTimes> CREATOR = new Parcelable.Creator<PrayerTimes>() {
		public PrayerTimes createFromParcel(Parcel in) {
		    return new PrayerTimes(in);
		}
		
		public PrayerTimes[] newArray(int size) {
		    return new PrayerTimes[size];
		}
	};
	
	private PrayerTimes(Parcel in) {
		m_Fajr = (Calendar) in.readSerializable();
		m_Sunrise = (Calendar) in.readSerializable();
		m_Dhuhr = (Calendar) in.readSerializable();
		m_Asr = (Calendar) in.readSerializable();
		m_Sunset = (Calendar) in.readSerializable();
		m_Maghrib = (Calendar) in.readSerializable();
		m_Isha = (Calendar) in.readSerializable();
	}

	public Calendar getFajr() {
		return m_Fajr;
	}
	public void setFajr(Calendar fajr) {
		m_Fajr = fajr;
	}
	public Calendar getSunrise() {
		return m_Sunrise;
	}
	public void setSunrise(Calendar sunrise) {
		m_Sunrise = sunrise;
	}
	public Calendar getDhuhr() {
		return m_Dhuhr;
	}
	public void setDhuhr(Calendar dhuhr) {
		m_Dhuhr = dhuhr;
	}
	public Calendar getAsr() {
		return m_Asr;
	}
	public void setAsr(Calendar asr) {
		m_Asr = asr;
	}
	public Calendar getSunset() {
		return m_Sunset;
	}
	public void setSunset(Calendar sunset) {
		m_Sunset = sunset;
	}
	public Calendar getMaghrib() {
		return m_Maghrib;
	}
	public void setMaghrib(Calendar maghrib) {
		m_Maghrib = maghrib;
	}
	public Calendar getIsha() {
		return m_Isha;
	}
	public void setIsha(Calendar isha) {
		m_Isha = isha;
	}	
	public String toString() {
		return "Fajr = " + m_Fajr.get(Calendar.HOUR_OF_DAY) + ':' + m_Fajr.get(Calendar.MINUTE) + "\n" +
			"Sunrise = " + m_Sunrise.get(Calendar.HOUR_OF_DAY) + ':' + m_Sunrise.get(Calendar.MINUTE) + "\n" +
			"Dhuhr = " + m_Dhuhr.get(Calendar.HOUR_OF_DAY) + ':' + m_Dhuhr.get(Calendar.MINUTE) + "\n" +
			"Asr = " + m_Asr.get(Calendar.HOUR_OF_DAY) + ':' + m_Asr.get(Calendar.MINUTE) + "\n" +
			"Sunset = " + m_Sunset.get(Calendar.HOUR_OF_DAY) + ':' + m_Sunset.get(Calendar.MINUTE) + "\n" +
			"Maghrib = " + m_Maghrib.get(Calendar.HOUR_OF_DAY) + ':' + m_Maghrib.get(Calendar.MINUTE) + "\n" +
			"Isha = " + m_Isha.get(Calendar.HOUR_OF_DAY) + ':' + m_Isha.get(Calendar.MINUTE);

	}
}

package org.islamdroid.athan.prayertimes;

import java.util.Calendar;

import org.islamdroid.athan.util.CONSTANTS;

import android.os.Parcel;
import android.os.Parcelable;


public class PrayerTime implements Parcelable {
	private int m_iPrayer;
	private Calendar m_PrayerTime;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(m_iPrayer);
		dest.writeSerializable(m_PrayerTime);
	}
	
	public static final Parcelable.Creator<PrayerTime> CREATOR = new Parcelable.Creator<PrayerTime>() {
		public PrayerTime createFromParcel(Parcel in) {
		    return new PrayerTime(in);
		}
		
		public PrayerTime[] newArray(int size) {
		    return new PrayerTime[size];
		}
	};

	private PrayerTime(Parcel in) {
		m_iPrayer = in.readInt();
		m_PrayerTime = (Calendar) in.readSerializable();
	}

	public PrayerTime(int iPrayer, Calendar prayerTime) {
		m_iPrayer = iPrayer;
		m_PrayerTime = prayerTime;
	}

	public int getPrayer() {
		return m_iPrayer;
	}

	public Calendar getPrayerTime() {
		return m_PrayerTime;
	}
	
	public String getPrayerName() {
		return CONSTANTS.PRAYER_NAMES[m_iPrayer];
	}
	
	public String toString() {
		return getPrayerName() + ": " + m_PrayerTime;
	}
}

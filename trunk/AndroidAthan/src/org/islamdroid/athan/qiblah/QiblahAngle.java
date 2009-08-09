package org.islamdroid.athan.qiblah;

import android.os.Parcel;
import android.os.Parcelable;

public class QiblahAngle implements Parcelable {

	private Angle m_Angle;
	private boolean m_bClockwise;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(m_Angle);
		if (m_bClockwise)
			dest.writeInt(1);
		else
			dest.writeInt(0);
	}
	
	public static final Parcelable.Creator<QiblahAngle> CREATOR = new Parcelable.Creator<QiblahAngle>() {
		public QiblahAngle createFromParcel(Parcel in) {
		    return new QiblahAngle(in);
		}
		
		public QiblahAngle[] newArray(int size) {
		    return new QiblahAngle[size];
		}
	};

	private QiblahAngle(Parcel in) {
		m_Angle = (Angle) in.readSerializable();
		m_bClockwise = (1 == in.readInt());
	}

	public QiblahAngle(double dAngle) {	// Gets anti-clockwise angle
		if (dAngle < 0) {
			dAngle = -dAngle;
			m_bClockwise = true;
		}
		else
			m_bClockwise = false;
		m_Angle = new Angle(dAngle);
	}
	
	public Angle getAngle() {
		return m_Angle;
	}

	public boolean isClockwise() {
		return m_bClockwise;
	}

	public String toString() {
		return m_Angle.toString() + ((m_bClockwise) ? " clockwise" : " anti-clockwise") + " from North";
	}
}

package org.islamdroid.athan.qiblah;

import java.io.Serializable;

public class Angle implements Serializable {
	private static final long serialVersionUID = 0;
	private int m_iDegree;
	private int m_iMinute;
	private int m_iSecond;
	
	public Angle(double dAngle) { // In degree
		m_iDegree = (int) dAngle;
		dAngle -= (double) m_iDegree;
		dAngle *= 60;
		m_iMinute = (int) dAngle;
		dAngle -= (double) m_iMinute;
		dAngle *= 60;
		m_iSecond = (int) dAngle;
	}
	
	public String toString() {
		return "" + m_iDegree + ':' + m_iMinute + ':' + m_iSecond;
	}

	public int getDegree() {
		return m_iDegree;
	}

	public int getMinute() {
		return m_iMinute;
	}

	public int getSecond() {
		return m_iSecond;
	}
}

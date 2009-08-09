package org.islamdroid.athan.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.islamdroid.athan.location.CityLocation;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.res.AssetManager;


public class AthanAssetManager {
	// Member variables
	private AssetManager m_AssetManager;
	private String [] m_sCountries;
	
	public AthanAssetManager(AssetManager p_AssetManager) throws IOException, XmlPullParserException {
		m_AssetManager = p_AssetManager;
		loadCountries();
	}
	
	private void loadCountries() throws IOException, XmlPullParserException {
		InputStream is = m_AssetManager.open("location/countries.xml", AssetManager.ACCESS_STREAMING);
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(is, null);
		Vector<String> vCountries = new Vector<String> ();
		int iEventType = xpp.getEventType();
		while (XmlPullParser.END_DOCUMENT != iEventType) {
			if (XmlPullParser.START_TAG == iEventType && xpp.getName().equals("country"))
				vCountries.add(xpp.getAttributeValue(0));
			iEventType = xpp.next();
		}
		m_sCountries = new String[vCountries.size()];
		m_sCountries = vCountries.toArray(m_sCountries);
	}
	
	public CityLocation [] getCities(String sCountry) throws IOException, XmlPullParserException {
		Vector<CityLocation> vCityLocations = new Vector<CityLocation> ();
		CityLocation cl = null;
		InputStream is = m_AssetManager.open("location/country/" + sCountry.replace(' ', '_').toLowerCase() + ".xml", AssetManager.ACCESS_STREAMING);
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(is, null);
		int iEventType = xpp.getEventType();
		boolean bLatitude = false;
		boolean bLongitude = false;
		while (XmlPullParser.END_DOCUMENT != iEventType) {
			if (XmlPullParser.START_TAG == iEventType) {
				if (xpp.getName().equals("city")) {
					if (null != cl)
						vCityLocations.add(cl);
					cl = new CityLocation();
					cl.m_sName = xpp.getAttributeValue(0);
				}
				else if (xpp.getName().equals("latitude"))
					bLatitude = true;
				else if (xpp.getName().equals("longitude"))
					bLongitude = true;
			}
			else if (XmlPullParser.TEXT == iEventType) {
				if (bLatitude) {
					cl.m_dLatitude = Double.parseDouble(xpp.getText());
					bLatitude = false;
				}
				else if (bLongitude) {
					cl.m_dLongitude = Double.parseDouble(xpp.getText());
					bLongitude = false;
				}				
			}
			iEventType = xpp.next();
		}
		CityLocation [] ret = new CityLocation[vCityLocations.size()];
		is.close();
		return vCityLocations.toArray(ret);
	}
	
	public String [] getCountries() {
		return m_sCountries;
	}
}

package org.islamdroid.athan;

import java.util.Calendar;

import org.islamdroid.athan.prayertimes.PrayerTimes;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import org.islamdroid.athan.R;

public class PrayerTimesView extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PrayerTimes pt = getIntent().getParcelableExtra(getString(R.string.PrayerTimesExtraTag));
		// String [] sPrayerTimes =
		// getIntent().getStringArrayExtra(getString(R.string.PrayerTimesExtraTag));
		String[] sValues = new String[7];
		Calendar c = pt.getFajr();
		//sValues[0] = "Fajr " + c.get(Calendar.HOUR_OF_DAY) + ':' + c.get(Calendar.MINUTE);
		sValues[0] = String.format("%1$-8s %2$02d:%3$02d", "Fajr", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		c = pt.getSunrise();
		sValues[1] = String.format("%1$-8s %2$02d:%3$02d", "Sunrise", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		c = pt.getDhuhr();
		sValues[2] = String.format("%1$-8s %2$02d:%3$02d", "Dhuhr", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		c = pt.getAsr();
		sValues[3] = String.format("%1$-8s %2$02d:%3$02d", "Asr", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		c = pt.getSunset();
		sValues[4] = String.format("%1$-8s %2$02d:%3$02d", "Sunset", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		c = pt.getMaghrib();
		sValues[5] = String.format("%1$-8s %2$02d:%3$02d", "Maghrib", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		c = pt.getIsha();
		sValues[6] = String.format("%1$-8s %2$02d:%3$02d", "Isha", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sValues));
		getListView().setTextFilterEnabled(true);
	}
}

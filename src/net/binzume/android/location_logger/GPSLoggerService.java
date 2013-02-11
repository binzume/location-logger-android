package net.binzume.android.location_logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

/**
 * ロギングする
 * 
 * @author kawahira
 */
public class GPSLoggerService extends IntentService implements LocationListener {

	public static final String ACTION_START = "net.binzume.android.location_logger.start";
	public static final String ACTION_STOP = "net.binzume.android.location_logger.stop";
	public static final String ACTION_TIMER = "net.binzume.android.location_logger.timer";
	public static final String ACTION_LOCATION = "net.binzume.android.location_logger.location";

	private static final String LOG_FILE = "location.log";

	private static final long INTERVAL_MILLISEC = 10L * 60 * 1000;

	public GPSLoggerService() {
		super("gps_logger");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// AlarmManagerで定期的にチェックしていたが，LocationManagerにpendingIntent設定するだけでもよさげ

		Log.d("GPSLoggerService", "onHandleIntent a:" + intent.getAction());
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (ACTION_START.equals(intent.getAction()) || ACTION_TIMER.equals(intent.getAction())) {
			writeLog("start");
			Intent i = new Intent(getApplicationContext(), GPSLoggerService.class);
			i.setAction(ACTION_LOCATION);
			PendingIntent pendignIntent = PendingIntent.getService(getApplicationContext(), 0, i, 0);

			locationManager.removeUpdates(pendignIntent);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, INTERVAL_MILLISEC, 1000, pendignIntent);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_MILLISEC, 1000, pendignIntent);

			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			i.setAction(ACTION_TIMER);
			am.cancel(pendignIntent);
			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL_MILLISEC, pendignIntent);
		} else if (ACTION_LOCATION.equals(intent.getAction())) {
			Intent i = new Intent(getApplicationContext(), GPSLoggerService.class);
			i.setAction(ACTION_LOCATION);
			PendingIntent pendignIntent = PendingIntent.getService(getApplicationContext(), 0, i, 0);

			locationManager.removeUpdates(pendignIntent);

			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				writeLog(location);
			}
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				writeLog(location);
			}
		} else if (ACTION_STOP.equals(intent.getAction())) {
			writeLog("stop");
			Intent i = new Intent(getApplicationContext(), GPSLoggerService.class);
			i.setAction(ACTION_LOCATION);
			PendingIntent pendignIntent = PendingIntent.getService(getApplicationContext(), 0, i, 0);

			locationManager.removeUpdates(pendignIntent);

			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			i.setAction(ACTION_TIMER);
			am.cancel(pendignIntent);
		}

	}

	private void writeLog(Location location) {
		writeLog("" + location.getProvider() + ": " + location.getTime() + "," + location.getLatitude() + "," + location.getLongitude() + ","
				+ location.getAltitude());
	}

	private void writeLog(String log) {
		Log.d("GPSLoggerService", "writeLog:" + log);

		File f = new File(Environment.getExternalStorageDirectory() + "/" + LOG_FILE);
		try {
			FileWriter filewriter = new FileWriter(f, true);
			filewriter.write(log + "\n");
			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onLocationChanged(Location location) {
		//writeLog(location);

	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}

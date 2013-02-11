package net.binzume.android.location_logger;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(), GPSLoggerService.class);
				intent.setAction(GPSLoggerService.ACTION_START);
				startService(intent);

			}
		});

		findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(), GPSLoggerService.class);
				intent.setAction(GPSLoggerService.ACTION_STOP);
				startService(intent);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

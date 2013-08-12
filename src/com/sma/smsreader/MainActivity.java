package com.sma.smsreader;

import java.util.Calendar;

import com.sma.smsreader.R;
import com.sma.smsreader.ServiceExample;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void ToggleService(View view) {

		boolean on = ((ToggleButton) view).isChecked();
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, ServiceExample.class);
		// Intent intent=new Intent("com.example.smsinformer.ServiceExample");
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

		if (on) {
			// Enable vibrate
			// startService(new Intent(this, ServiceExample.class));
			// this.startService(intent);
			// inetnt=new Intent(this,ServiceExample.class);
			Log.v("SmsInformer", "Starting service");
			// startService(inetnt);
			Calendar cal = Calendar.getInstance();
			// Start every 60 seconds

			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
					60 * 1000, pintent);

		} else {
			// inetnt=new Intent(this,ServiceExample.class);
			Log.v("SmsInformer", "Stopping service");
			// stopService(inetnt);
			alarm.cancel(pintent);
		}

	}
}

package com.sma.smsreader;

import java.util.ArrayList;
import net.everythingandroid.smspopup.provider.SmsMmsMessage;
import net.everythingandroid.smspopup.util.SmsPopupUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServiceExample extends IntentService {

	private static final String PREFS_NAME = "SmsReader";

	String[] allowed = { "+79651249856",// Bond
			"+79160774414",// Havtorin
			"+79258790574",// sensor
			"+79651802197",// sensor 1
			"+79263860444",// vlad
	};

	public ServiceExample(String name) {
		super(name);
		Log.v("Service create", "creating service");
	}

	public ServiceExample() {
		super(null);
		Log.v("Service create", "creating service");
	}

	/*
	 * public ServiceExample() {
	 * super("com.example.smsinformer.ServiceExample"); Log.v("Service create",
	 * "creating service"); }
	 */
	// CentreonMessages messages = new CentreonMessages();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
		/*
		 * Log.e("Service run", "Stopping service (harakiri)"); AlarmManager
		 * alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		 * Intent intent = new Intent(this, ServiceExample.class); PendingIntent
		 * pintent = PendingIntent .getService(this, 0, intent, 0);
		 * alarm.cancel(pintent); }
		 */
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Service Destroy", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Toast.makeText(this, "Service LowMemory", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Toast.makeText(this, "Service start", Toast.LENGTH_LONG).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.v("Service", "Starting");
		Toast.makeText(this, "task perform in service", Toast.LENGTH_LONG)
				.show();
		ThreadDemo td = new ThreadDemo(this);
		td.start();
		return super.onStartCommand(intent, flags, startId);
	}

	private class ThreadDemo extends Thread {
		Context serviceContext;

		public ThreadDemo(Context context) {
			serviceContext = context;
		}

		@Override
		public void run() {
			super.run();
			Log.v("Thread service", "Running service thread");
			// boolean s = ReadMessages();
			long LastSentAlarmId = GetLastSentAlarmId();
			long LastSetSensorId = GetLastSetSensorId();
			ArrayList<SmsMmsMessage> Messages = SmsPopupUtils.getMessages(
					serviceContext, 3);
			long LastSmsId = Messages.get(0).getMessageId();

			Log.e("SmsReader", "Last received message ID is " + LastSmsId);

			if (LastSentAlarmId > LastSmsId) {
				Log.e("SmsReader Error",
						"SMS id was reset. Setting LastSentAlarmId to last message");
				SetLastSentAlarmId(LastSmsId);
			}

			if (LastSetSensorId > LastSmsId) {
				Log.e("SmsReader Error",
						"SMS id was reset. Setting LastSetSensorId to last message");
				SetLastSetSensorId(LastSmsId);
			}
			if (LastSmsId == 0) {
				Log.e("SmsReader Error",
						"LastSmsId was zero. Setting it to last message to avoid spam");
				SetLastSentAlarmId(LastSmsId);
				SetLastSetSensorId(LastSmsId);
			}

			for (int i = Messages.size() - 1; i >= 0; i--)// begin from the
															// oldest
			{
				long messageId = Messages.get(i).getMessageId();
				String from = Messages.get(i).getAddress();
				// Log.e("Message id", String.valueOf(messageId));
				// Log.e("Message time",
				// String.valueOf(Messages.get(i).getTimestamp()));
				// Log.e("Message sender", Messages.get(i).getAddress());
				// Log.e("Message text", Messages.get(i).getMessageBody());
				boolean found = false;
				for (int z = 0; z < allowed.length; z++) {
					if (allowed[z].compareTo(from) == 0)
						found = true;
				}

				if (!found)
					continue;// not our guy

				Log.e("Sms Reader", "Our guy's message!");
				String m = Messages.get(i).getMessageBody();

				if (m.indexOf("Пожарная тревога") == -1
						|| LastSentAlarmId >= messageId) {
					Log.e("SmsReader",
							"Сообщение не про пожар или старое! Игнорирую.");
					//continue;
				} else {

					boolean res = setAlarm(m, messageId);
					if (!res)
						break;
				}

				if (m.indexOf("add_sensor") == -1
						|| LastSetSensorId >= messageId) {
					Log.e("SmsReader",
							"Сообщение не про добавку сенсора или старое! Игнорирую.");
					//continue;
				} else {

					boolean res = addSensor(m, messageId);
					if (!res)
						break;

				}
			}
		}

	}

	private boolean addSensor(String json, long messageId) {
		AddSensorMessage m = new AddSensorMessage();
		m.set(json);
		boolean r = m.send();
		if (r)
			this.SetLastSetSensorId(messageId);
		return r;
	}

	boolean setAlarm(String json, long messageId) {

		SetAlarmMessage m = new SetAlarmMessage();
		m.set(json);
		boolean r = m.send();
		if (r)
			this.SetLastSentAlarmId(messageId);
		return r;
	}

	long GetLastSentAlarmId() {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getLong("LastSentAlarmId", 0);
	}

	void SetLastSentAlarmId(long d) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong("LastSentAlarmId", d);
		editor.commit();
	}

	long GetLastSetSensorId() {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getLong("LastSetSensorId", 0);
	}

	void SetLastSetSensorId(long d) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong("LastSetSensorId", d);
		editor.commit();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v("Thread service", "handling intent");
	}

}
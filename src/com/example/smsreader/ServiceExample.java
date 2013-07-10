package com.example.smsreader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
			long LastSentId = GetLastSentId();
			ArrayList<SmsMmsMessage> Messages = SmsPopupUtils.getMessages(
					serviceContext, 1);
			long LastSmsId = Messages.get(0).getMessageId();

			Log.e("SmsReader", "Last received message ID is " + LastSmsId);

			if (LastSentId > LastSmsId) {
				Log.e("SmsReader Error",
						"SMS id was reset. Setting it to last message");
				SetLastSentId(LastSmsId);
			}
			if (LastSmsId == 0) {
				Log.e("SmsReader Error",
						"LastSmsId was zero. Setting it to last message to avoid spam");
				SetLastSentId(LastSmsId);
			}

			for (int i = Messages.size() - 1; i >= 0; i--)// begin from the
															// oldest
			{
				long messageId = Messages.get(i).getMessageId();
				Log.e("Message id", String.valueOf(messageId));
				Log.e("Message time",
						String.valueOf(Messages.get(i).getTimestamp()));
				Log.e("Message sender", Messages.get(i).getAddress());
				Log.e("Message text", Messages.get(i).getMessageBody());
				String source = "+79651249856";// only from Bond work phone
				if (Messages.get(i).getAddress().compareTo(source) == 0) {
					Log.e("Sms Reader", "Our guy's message!");
					if (LastSentId >= messageId) {
						Log.e("SmsReader", "Old message");
					} else {
						Log.e("SmsReader", "New message! Need to pass it!");
						// TODO: parse message text and send it to server. If
						// all ok - update last sent id

						/**
						 * /api.php?mode=create_event
						 * 
						 * @param id_sensor
						 * @param state
						 * @param time
						 * @return json {status:'',data:''}
						 */

						String url = "http://forest.eias.ru/api.php?mode=create_event&id_sensor=1&state=1&time=121212";
						HttpClient httpclient = new DefaultHttpClient();
						try {
							HttpResponse response = httpclient
									.execute(new HttpGet(url));
							StatusLine statusLine = response.getStatusLine();
							if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
								ByteArrayOutputStream out = new ByteArrayOutputStream();
								response.getEntity().writeTo(out);
								out.close();
								String responseString = out.toString();
								Log.e("SmsReader", responseString);
								// TODO: check that it is really ok
								Log.e("SmsReader", "Setting LastSentId "
										+ messageId);
								SetLastSentId(messageId);
								// ..more logic
							} else {
								// Closes the connection.
								response.getEntity().getContent().close();
								//throw new IOException(
								//		statusLine.getReasonPhrase());
								Log.e("SmsREader",statusLine.getReasonPhrase());
								break;
							}
						} catch (IOException e) {
							Log.e("SmsReader",
									"Something fucked up http request...");
							break;
							// TODO Handle problems..
						}

					}
				}
			}
		}
	}

	long GetLastSentId() {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getLong("LastSentId", 0);
	}

	void SetLastSentId(long d) {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong("LastSentId", d);
		editor.commit();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v("Thread service", "handling intent");
	}

}
package com.example.smsreader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

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
				// Log.e("Message id", String.valueOf(messageId));
				// Log.e("Message time",
				// String.valueOf(Messages.get(i).getTimestamp()));
				// Log.e("Message sender", Messages.get(i).getAddress());
				// Log.e("Message text", Messages.get(i).getMessageBody());
				String source = "+79651249856";// only from Bond work phone
				String source2 = "+79160774414";// Havtorin
				if (Messages.get(i).getAddress().compareTo(source) == 0 || Messages.get(i).getAddress().compareTo(source2) == 0) {
					Log.e("Sms Reader", "Our guy's message!");
					if (LastSentId >= messageId) {
						Log.e("SmsReader", "Old message");
					} else {
						Log.e("SmsReader", "New message! Need to pass it!");
						
						String m=Messages.get(i).getMessageBody();
						if(m.indexOf("Пожарная тревога")==-1)
						{
							Log.e("SmsReader","Сообщение не про пожар! Игонирирую.");
							break;
						}
						String s=m.substring(m.indexOf(",")+2, m.indexOf(":"));
						String sensor_id=String.valueOf(Integer.valueOf(s));
						Date dt = new java.util.Date();
						String time=String.valueOf(dt.getTime()/1000);
						String state="1";
						//TODO: get time from message text
						/**
						 * /api.php?mode=create_event
						 * 
						 * @param id_sensor
						 * @param state
						 * @param time
						 * @return json {status:'',data:''}
						 */

						String url = "http://forest.eias.ru/api.php?mode=create_event";
						HttpParams params = new BasicHttpParams();
						params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
						HttpClient httpclient = new DefaultHttpClient(params);
						HttpPost httpPost = new HttpPost(url);

						List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(
								4);
						//nameValuePair.add(new BasicNameValuePair("mode",
						//		"create_event"));
						nameValuePair.add(new BasicNameValuePair("id_sensor",
								sensor_id));
						nameValuePair.add(new BasicNameValuePair("state", state));
						nameValuePair.add(new BasicNameValuePair("time",time));
						// Url Encoding the POST parameters
						try {
							httpPost.setEntity(new UrlEncodedFormEntity(
									nameValuePair));
						} catch (UnsupportedEncodingException e) {
							// writing error to Log
							e.printStackTrace();
						}
						try {
							HttpResponse response = httpclient
									.execute(httpPost);
							StatusLine statusLine = response.getStatusLine();
							if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
								ByteArrayOutputStream out = new ByteArrayOutputStream();
								response.getEntity().writeTo(out);
								out.close();
								String responseString = out.toString();
								Log.e("SmsReader OK (200), response: ",
										responseString);
								// TODO: check that it is really ok. for success string, for example.
								Log.e("SmsReader", "Setting LastSentId "
										+ messageId);
								SetLastSentId(messageId);
								// ..more logic
							} else {
								// Closes the connection.
								response.getEntity().getContent().close();
								// throw new IOException(
								// statusLine.getReasonPhrase());
								Log.e("SmsReader", "Request failed: "
										+ statusLine.getReasonPhrase());
								break;
							}
						}
						catch (ClientProtocolException e) {
							//e.printStackTrace();
						     Log.e("SmsReader",e.toString());
					    }
						catch (IOException e) {
							Log.e("SmsReader",
									"Something fucked up http request...");
						     Log.e("SmsReader",e.toString());
							break;
						}
						catch (Exception e)
						{
						     Log.e("SmsReader",e.toString());
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
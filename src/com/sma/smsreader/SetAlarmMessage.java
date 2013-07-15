package com.sma.smsreader;

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

import android.util.Log;

public class SetAlarmMessage {

	String time,state,sensor_id;
	public void set(String json) {
		// TODO Auto-generated method stub

		String s = json.substring(json.indexOf("тревога") + 7, json.indexOf(":"));
		s = s.replace(",", "");
		sensor_id = s.trim().trim();// String.valueOf(Integer.valueOf(s));
		Date dt = new java.util.Date();
		time = String.valueOf(dt.getTime() / 1000);
		state = "1";
		// TODO: get time from message text
		/**
		 * /api.php?mode=create_event
		 * 
		 * @param id_sensor
		 * @param state
		 * @param time
		 * @return json {status:'',data:''}
		 */

	}

	public boolean send() {

		String url = "http://forest.eias.ru/api.php?mode=create_event";
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		HttpClient httpclient = new DefaultHttpClient(params);
		HttpPost httpPost = new HttpPost(url);

		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
		// nameValuePair.add(new BasicNameValuePair("mode",
		// "create_event"));
		nameValuePair.add(new BasicNameValuePair("id_sensor", sensor_id));
		nameValuePair.add(new BasicNameValuePair("state", state));
		nameValuePair.add(new BasicNameValuePair("time", time));
		// Url Encoding the POST parameters
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}
		try {
			HttpResponse response = httpclient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();
				Log.e("SmsReader OK (200), response: ", responseString);
				return true;
				// ..more logic
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				// throw new IOException(
				// statusLine.getReasonPhrase());
				Log.e("SmsReader",
						"Request failed: " + statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			// e.printStackTrace();
			Log.e("SmsReader", e.toString());
		} catch (IOException e) {
			Log.e("SmsReader", "Something fucked up http request...");
			Log.e("SmsReader", e.toString());
		} catch (Exception e) {
			Log.e("SmsReader", e.toString());
		}
		return false;
	}

}

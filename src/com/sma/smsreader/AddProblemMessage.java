package com.sma.smsreader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class AddProblemMessage {
	String id, name, latitude, longitude, altitude, setup_time, comments,
			re_id, host_id;

	boolean set(String json) {

		this.name = "";
		this.comments = "";
		try {
			JSONObject jObject = new JSONObject(json);
			this.setup_time = jObject.getString("time");
			this.longitude = jObject.getString("long");
			this.latitude = jObject.getString("lat");
			this.altitude = jObject.getString("alt");
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean send() {

		/**
		 * /api.php?mode=create_problem
		 * 
		 * @param name
		 * @param altitude
		 * @param longitude
		 * @param latitude
		 * @param setup_time
		 * @param comments
		 * @param type
		 * @return json {status:'',data:''}
		 */

		String url = "http://forest.eias.ru/api.php?mode=create_problem";
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		HttpClient httpclient = new DefaultHttpClient(params);
		HttpPost httpPost = new HttpPost(url);

		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
		// nameValuePair.add(new BasicNameValuePair("mode",
		// "create_event"));
		nameValuePair.add(new BasicNameValuePair("id", this.id));
		nameValuePair.add(new BasicNameValuePair("name", this.name));
		nameValuePair.add(new BasicNameValuePair("latitude", this.latitude));
		nameValuePair.add(new BasicNameValuePair("longitude", this.longitude));
		nameValuePair.add(new BasicNameValuePair("altitude", this.altitude));
		nameValuePair
				.add(new BasicNameValuePair("setup_time", this.setup_time));
		nameValuePair.add(new BasicNameValuePair("comments", this.comments));
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
				// TODO: check that it is really ok. for success string, for
				// example.
				return true;
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
		return true;

	}
}
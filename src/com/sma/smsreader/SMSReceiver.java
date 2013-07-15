package com.sma.smsreader;

//import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.os.Bundle;
//import android.telephony.gsm.SmsMessage;
import com.sma.smsreader.ServiceExample;

public class SMSReceiver extends BroadcastReceiver {

	String number;
	String text;

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent intent2 = new Intent(context, ServiceExample.class);
		// Intent intent=new Intent("com.example.smsinformer.ServiceExample");
		// PendingIntent pintent = PendingIntent.getService(context, 0, intent2,
		// 0);
		context.startService(intent2);

		/*
		 * Bundle bundle = intent.getExtras();
		 * 
		 * Object messages[] = (Object[]) bundle.get("pdus");
		 * 
		 * SmsMessage sms[] = new SmsMessage[messages.length];
		 * 
		 * for (int n = 0; n < messages.length; n++) { sms[n] =
		 * SmsMessage.createFromPdu((byte[]) messages[n]);
		 * 
		 * }
		 * 
		 * number = sms[0].getOriginatingAddress(); text =
		 * sms[0].getMessageBody(); long id=sms[0].
		 */

		/*
		 * Intent i = new Intent(Intent.ACTION_SEND);
		 * i.setType("message/rfc822"); i.putExtra(Intent.EXTRA_EMAIL , new
		 * String[]{someEmail}); i.putExtra(Intent.EXTRA_SUBJECT,
		 * "subject of email"); i.putExtra(Intent.EXTRA_TEXT , text + " " +
		 * number);
		 */
	}
}
package com.voice.java.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.voice.jarvis.JARVISActivity;
import com.voice.jarvis.MyApplication;

public class BootUpReceiver extends BroadcastReceiver{
	String deviceSpeed;
	static int i=1;
	@Override
	public void onReceive(Context context, Intent intent) {
		/*new CommandService(context);
		Intent service = new Intent(context, CommandService.class);
	    context.startService(service);
*/

		if(intent.getStringExtra("hasSpeed").equalsIgnoreCase("true")){
			deviceSpeed = intent.getStringExtra("deviceSpeed");
			//JARVISActivity.setSpeedText(String.valueOf(deviceSpeed)+i++);

		}
	}

}

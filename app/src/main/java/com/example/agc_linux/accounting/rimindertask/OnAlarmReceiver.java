package com.example.agc_linux.accounting.rimindertask;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.util.Log;

import com.example.agc_linux.accounting.StaticConfig;

public class OnAlarmReceiver extends BroadcastReceiver {

	private static final String TAG = ComponentInfo.class.getCanonicalName(); 
	
	
	@Override	
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received wake up from alarm manager.");
		
		int rowid = intent.getExtras().getInt(StaticConfig.KEY_UNIQUE);
		
		WakeReminderIntentService.acquireStaticLock(context);
		
		Intent i = new Intent(context, ReminderService.class); 
		i.putExtra(StaticConfig.KEY_UNIQUE, rowid);
		context.startService(i);
		 
	}
}

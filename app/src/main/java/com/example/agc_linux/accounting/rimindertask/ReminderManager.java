package com.example.agc_linux.accounting.rimindertask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.agc_linux.accounting.StaticConfig;

import java.util.Calendar;

public class ReminderManager {

	private Context mContext; 
	private AlarmManager mAlarmManager;
	
	public ReminderManager(Context context) {
		mContext = context; 
		mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void setReminder(int taskId, Calendar when) {
		Log.e("test","====>>>"+taskId);
        Intent i = new Intent(mContext, OnAlarmReceiver.class);
        i.putExtra(StaticConfig.KEY_UNIQUE,taskId);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, PendingIntent.FLAG_ONE_SHOT); 
        
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
	}
}

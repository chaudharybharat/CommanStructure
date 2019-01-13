package com.example.agc_linux.accounting.rimindertask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.database.Cursor;
import android.util.Log;

import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.model.Customer;
import com.example.agc_linux.accounting.model.CustomerTranscation;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OnBootReceiver extends BroadcastReceiver {
	
	private static final String TAG = ComponentInfo.class.getCanonicalName();  
	
	@Override
	public void onReceive(Context context, Intent intent) {
		ReminderManager reminderMgr = new ReminderManager(context);
			
		FlowCursor query= SQLite.select().from(CustomerTranscation.class).query();
		Cursor cursor = query.getWrappedCursor();;
		
		if(cursor != null) {
			cursor.moveToFirst();

			int rowIdColumnIndex = cursor.getColumnIndex("uniqeId");
			int dateTimeColumnIndex = cursor.getColumnIndex("paymentTermdate");
			
			while(cursor.isAfterLast() == false) {

				Log.d(TAG, "Adding alarm from boot.");
				Log.d(TAG, "Row Id Column Index - " + rowIdColumnIndex);
				Log.d(TAG, "Date Time Column Index - " + dateTimeColumnIndex);
				
				int rowId = cursor.getInt(rowIdColumnIndex);
				String dateTime = cursor.getString(dateTimeColumnIndex); 

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat format = new SimpleDateFormat(StaticConfig.DATE_FORMATE);
				
				try {
					java.util.Date date = format.parse(dateTime);
					cal.setTime(date);
					
					reminderMgr.setReminder(rowId, cal); 
				} catch (java.text.ParseException e) {
					Log.e("OnBootReceiver", e.getMessage(), e);
				}
				
				cursor.moveToNext(); 
			}
			cursor.close() ;	
		}

	}
}


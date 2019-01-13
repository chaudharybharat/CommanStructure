package com.example.agc_linux.accounting.rimindertask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;

import com.example.agc_linux.accounting.MainActivity;
import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.model.Customer;
import com.example.agc_linux.accounting.model.CustomerTranscation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReminderService extends WakeReminderIntentService {

	public ReminderService() {
		super("ReminderService");
			}

	@Override
	void doReminderWork(Intent intent) {
		int rowId = intent.getExtras().getInt(StaticConfig.KEY_UNIQUE);

        //customNotification(rowId);
		/*NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
						
		Intent notificationIntent = new Intent(this, MainActivity.class);

		notificationIntent.putExtra(StaticConfig.KEY_UNIQUE, rowId);
		
		PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		Notification myNotication;

		Notification.Builder builder = new Notification.Builder(this);

		builder.setAutoCancel(false);
		builder.setTicker("this is ticker text");
		builder.setContentTitle("WhatsApp Notification");
		builder.setContentText("You have a new message");
		builder.setSmallIcon(R.mipmap.ic_launcher);
		builder.setContentIntent(pi);
		builder.setAutoCancel(true);
		builder.setSubText("This is subtext...");   //API level 16
		builder.setNumber(100);
		builder.build();
		
		// An issue could occur if user ever enters over 2,147,483,647 tasks. (Max int value). 
		myNotication = builder.getNotification();
		mgr.notify(11, myNotication);*/
		
		
	}
	public void customNotification(int rowId) {
        String customer_name="";
        String customer_mobile="";
        String amount="";
        String payment_date="";
        String detail="";


        CustomerTranscation  customerTranscation=CustomerTranscation.getTranscation(rowId);
		payment_date=customerTranscation.getPaymentTermdate();

		SimpleDateFormat sdf = new SimpleDateFormat(StaticConfig.DATE_FORMATE);
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		String date_today = sdf.format(date);

		Log.e("test","==>"+date_today);
		Log.e("test","payment date==>"+payment_date);

			if (payment_date.equalsIgnoreCase(date_today)) {
				if(customerTranscation!=null) {
					Customer customer=Customer.getCustomer(customerTranscation.getCustomer_id());
					if(customer!=null){
						Log.e("test","Customer name"+customer.getName());
						customer_name=customer.getName();
					}
					amount=customerTranscation.getAmount();
					detail=customerTranscation.getDescraption();
				}

				// Using RemoteViews to bind custom layouts into Notification
				RemoteViews remoteViews = new RemoteViews(getPackageName(),
						R.layout.customnotification);

				RemoteViews remoteViewssmall = new RemoteViews(getPackageName(),
						R.layout.customnotification);
				Intent intent = new Intent(this,MainActivity.class);
				intent.putExtra(StaticConfig.DATE,payment_date);


				remoteViews.setTextViewText(R.id.tv_customer_name,customer_name);
				remoteViews.setTextViewText(R.id.tv_amount,amount);
				remoteViews.setTextViewText(R.id.tv_detail,detail);

				PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
						// Set Icon
						.setSmallIcon(R.drawable.cash)
						// Set Ticker Message
						// Dismiss Notification
						.setAutoCancel(true)
						.setContent(remoteViewssmall)
						//.setCustomBigContentView(remoteViews)
						// Set PendingIntent into Notification
						.setContentIntent(pIntent);
				// Set RemoteViews into Notification


				// Locate and set the Text into customnotificationtext.xml TextViews
				//notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				// Create Notification Manager
				NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				// Build Notification with Notification Manager
				notificationmanager.notify(rowId, builder.build());

			}




	}
}

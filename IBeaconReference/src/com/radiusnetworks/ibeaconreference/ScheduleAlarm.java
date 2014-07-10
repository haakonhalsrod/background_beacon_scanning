package com.radiusnetworks.ibeaconreference;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ScheduleAlarm extends IntentService {

	public ScheduleAlarm() {
		super("ScheduleAlarm");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		scheduleAlarm();
	}
	
	public void scheduleAlarm() {
	    // Construct an intent that will execute the AlarmReceiver
	    Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
	    // Create a PendingIntent to be triggered when the alarm goes off
	    final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
	        intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    // Setup periodic alarm every 5 seconds
	    long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
	    int intervalMillis = 60000; // 5 minutes
	    AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
	    alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);
	  }
}

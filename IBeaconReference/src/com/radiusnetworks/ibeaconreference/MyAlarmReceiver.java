package com.radiusnetworks.ibeaconreference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyAlarmReceiver extends BroadcastReceiver {

	public static final int REQUEST_CODE = 12345;
	public static final String ACTION = "com.radiusnetworks.ibeaconreference.ScheduleAlarm";
	//Hva gjør ACTION?

    @Override
    public void onReceive(Context context, Intent intent) {
    	// Start Service On Boot Start Up
		Intent service = new Intent(context, MonitoringService.class);
		context.startService(service);
    }

}
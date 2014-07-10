package com.radiusnetworks.ibeaconreference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyAlarmReceiver extends BroadcastReceiver {

	public static final int REQUEST_CODE = 12345;
	public static final String ACTION = "com.radiusnetworks.ibeaconreference.alarm";
	//Hva gjør ACTION?

    @Override
    public void onReceive(Context context, Intent intent) {
           // TODO: This method is called when the BroadcastReceiver is receiving

           // Start Service On Boot Start Up
    	try {
			Thread.sleep(50000);
			Intent service = new Intent(context, MonitoringService.class);
	    	context.startService(service);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          
           //Start App On Boot Start Up
//           Intent App = new Intent(context, MonitoringActivity.class);
//           App.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//           context.startActivity(App);

    }

}
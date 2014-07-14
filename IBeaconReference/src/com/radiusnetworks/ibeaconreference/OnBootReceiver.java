package com.radiusnetworks.ibeaconreference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
 
/**
 * BroadCastReceiver for android.intent.action.BOOT_COMPLETED
 * passes all responsibility to TaskButlerService.
 * @author Dhimitraq Jorgji
 *
 */
public class OnBootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
    	try {
			Thread.sleep(30000);
			Intent i = new Intent(context, ScheduleAlarm.class);
			context.startService(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

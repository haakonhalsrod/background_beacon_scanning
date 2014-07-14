package com.radiusnetworks.ibeaconreference;

import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.Region;

import android.os.Bundle;
import android.os.RemoteException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * 
 * @author dyoung
 * @author Matt Tyler
 */
public class MonitoringActivity extends Activity implements IBeaconConsumer  {
	protected static final String TAG = "MonitoringActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitoring);
//		verifyBluetooth();
//	    iBeaconManager.bind(this);			
//	    
//		//initializing simulated iBeacons
//		//IBeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
//		//((TimedBeaconSimulator) IBeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();
		}
	
	public void onRangingClicked(View view) {
		Intent myIntent = new Intent(this, MonitoringService.class);
		this.startService(myIntent);
	}
	public void onBackgroundClicked(View view) {
		Intent myIntent = new Intent(this, ScheduleAlarm.class);
		this.startService(myIntent);
	}

	private void verifyBluetooth() {

		try {
			if (!IBeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");			
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
			            System.exit(0);					
					}					
				});
				builder.show();
			}			
		}
		catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");			
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
		            System.exit(0);					
				}
				
			});
			builder.show();
			
		}
		
	}	

    private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);

    @Override 
    protected void onDestroy() {
        super.onDestroy();
        iBeaconManager.unBind(this);
    }
    @Override 
    protected void onPause() {
    	super.onPause();
    	if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, true);    		
    }
    @Override 
    protected void onResume() {
    	super.onResume();
    	if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, false);    		
    }    
    
    private void logToDisplay(final String line) {
    	runOnUiThread(new Runnable() {
    	    public void run() {
    	    	EditText editText = (EditText)MonitoringActivity.this
    					.findViewById(R.id.monitoringText);
       	    	editText.append(line+"\n");            	    	    		
    	    }
    	});
    }
    @Override
    public void onIBeaconServiceConnect() {
        iBeaconManager.setMonitorNotifier(new MonitorNotifier() {
        @Override
        public void didEnterRegion(Region region) {
          logToDisplay("I just saw an iBeacon named "+ region.getUniqueId() +" for the first time!" );       
        }

        @Override
        public void didExitRegion(Region region) {
        	logToDisplay("I no longer see an iBeacon named "+ region.getUniqueId());
        }

        @Override
        public void didDetermineStateForRegion(int state, Region region) {
        	logToDisplay("I have just switched from seeing/not seeing iBeacons: "+state);     
        }


        });

        try {
        	iBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        	
        	//Sample Simulated iBeacons
        	//iBeaconManager.startMonitoringBeaconsInRegion(new Region("test1","DF7E1C79-43E9-44FF-886F-1D1F7DA6997A".toLowerCase(), 1, 1));
        	//iBeaconManager.startMonitoringBeaconsInRegion(new Region("test2","DF7E1C79-43E9-44FF-886F-1D1F7DA6997B".toLowerCase(), 1, 2));
        	//iBeaconManager.startMonitoringBeaconsInRegion(new Region("test3","DF7E1C79-43E9-44FF-886F-1D1F7DA6997C".toLowerCase(), 1, 3));
        	//iBeaconManager.startMonitoringBeaconsInRegion(new Region("test4","DF7E1C79-43E9-44FF-886F-1D1F7DA6997D".toLowerCase(), 1, 4));
        } catch (RemoteException e) {   }
    }
	
}

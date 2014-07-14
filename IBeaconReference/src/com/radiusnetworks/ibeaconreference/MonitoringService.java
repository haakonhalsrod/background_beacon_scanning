package com.radiusnetworks.ibeaconreference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.IntentService;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

public class MonitoringService extends IntentService implements IBeaconConsumer{
	
	public MonitoringService() {
			super("MonitoringService");
			// TODO Auto-generated constructor stub
		}

	protected static final String TAG = "MonitoringActivity";
	
	//added things
	//MQTT stuff
	//String serverUri = "tcp://messaging.quickstart.internetofthings.ibmcloud.com:1883";
	String serverUri = "tcp://broker.mqtt-dashboard.com:1883";
	String clientId = "quickstart:fbb80f053444";
	MemoryPersistence persistence = new MemoryPersistence();
	String topic = "iot-1/d/fbb80f053444/evt/iotsensor/json";
	
	//bookkeeping and target stuff
	MqttAndroidClient androidClient;
	List<IBeacon> alertedBeacons = new ArrayList<IBeacon>(); //beacons that have at least once been near or immediate; these will not cause a new alert
	List<IBeacon> beaconsInRange = new ArrayList<IBeacon>(); //beacons in range
	String targetUuid = "e2c56db5-dffb-48d2-b060-d0f5a71096e0";
	int targetMajor = 5;
	int targetMinor = 5;
	String targetName = "iPhoneBeacon";
	String macAddress;
	
    static class RangeComparator implements Comparator<IBeacon>{
    	@Override
    	public int compare(IBeacon b1, IBeacon b2){
    		double r1 = b1.getAccuracy();
    		double r2 = b2.getAccuracy();
    		if (r1 == r2)
    			return 0;
    		else if (r1>r2)
    			return 1;
    		else
    			return -1;
    	}
    }
    
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Service started");
		//super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_monitoring); //sets up layout as given in activity_monitoring.xml
		verifyBluetooth();
	    iBeaconManager.bind(this);
	    try {
	    	//try to connect
	    	MqttConnectOptions conOpt = new MqttConnectOptions();
	    	conOpt.setKeepAliveInterval(240000);
	    	Context context = this;
	    	androidClient = new MqttAndroidClient(context, serverUri, clientId, persistence);	
//	    	androidClient.setTraceEnabled(true);
//			conOpt.setWill(client.getTopic(topic),"Crash".getBytes(),1,true);
	    	androidClient.connect(conOpt); //
	    	Log.v("onCreate", "We might have logged on with the Mqtt client!");	   
	    	//make sure we can get the macAddress; this must be done onCreate
	    	WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    	WifiInfo info = manager.getConnectionInfo();
	    	macAddress = info.getMacAddress();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	    try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		//initializing simulated iBeacons
		//IBeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
		//((TimedBeaconSimulator) IBeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();
	}
	
	@Override
	public void onCreate() { 
		Log.d(TAG, "onCreate");
		super.onCreate();
//		//setContentView(R.layout.activity_monitoring); //sets up layout as given in activity_monitoring.xml
//		verifyBluetooth();
//	    iBeaconManager.bind(this);
//	    try {
//	    	//try to connect
//	    	MqttConnectOptions conOpt = new MqttConnectOptions();
//	    	conOpt.setKeepAliveInterval(240000);
//	    	Context context = this;
//	    	androidClient = new MqttAndroidClient(context, serverUri, clientId, persistence);	
////	    	androidClient.setTraceEnabled(true);
////			conOpt.setWill(client.getTopic(topic),"Crash".getBytes(),1,true);
//	    	androidClient.connect(conOpt); //causes error
//	    	Log.v("onCreate", "We might have logged on with the Mqtt client!");	   
//	    	//make sure we can get the macAddress; this must be done onCreate
//	    	WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//	    	WifiInfo info = manager.getConnectionInfo();
//	    	macAddress = info.getMacAddress();
//		} catch (MqttException e) {
//			e.printStackTrace();
//		}
	    
		//initializing simulated iBeacons
		//IBeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
		//((TimedBeaconSimulator) IBeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();
	}
	
//	public void onResetClicked(View view){ //implement this into layout
//		alertedBeacons = new ArrayList<IBeacon>();
//		beaconsInRange = new ArrayList<IBeacon>();
//	}
	
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
						//finish();
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
					//finish();
		            System.exit(0);					
				}
				
			});
			builder.show();
			
		}
		
	}	

    private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);
 
    public void sortBeaconsAscendingByRange(List<IBeacon> beacons){
    	if (beacons.size()>1){
    		//sort by ascending range
    		Collections.sort(beaconsInRange, Collections.reverseOrder(new MonitoringService.RangeComparator())); //looks Greek but should work
    	}
    }
    
    public Boolean addBeaconToList(List<IBeacon> beaconList, IBeacon beacon) //add beacon to beacon list if it is not already in list; return TRUE if beacon added
    {
    	Boolean insert = true;
    	for (IBeacon kevinBeacon : beaconList){
    		if (kevinBeacon.equals(beacon))
			{
    			insert = false;
    			break;
			}
    	}
    	if (insert)
    	{
    		beaconList.add(beacon);
    	}
    	return insert;
    }
    
    ////////////////////////////////
    //////SEND UPON DETECTION///////
    ////////////////////////////////
    
    public void sendNearestBeacon(Collection<IBeacon> listOfBeacons){
    	List<IBeacon> iBeaconList = new ArrayList<IBeacon>(listOfBeacons);	//stupid hack because I'm stupid
    	sortBeaconsAscendingByRange(iBeaconList); //sort
    	sendBeacon2Platform(iBeaconList.get(0));
    }
    
    public void sendSignalDetection(Region region, double range){
    	try 
    	{
    		//first things first
    		if (region.getMajor()==null || region.getMinor()==null || region.getProximityUuid()==null)
    		{}
    		else{
				//get stuff
				String myMajor = region.getMajor().toString();
				String myMinor = region.getMinor().toString();
				String myUuid = region.getProximityUuid();
				
				//make message in JSON format for easier parsing in platform
				String payload = "{ \"iBeaconUuid\": " + myUuid + ",\n\"iBeaconMajor\": " + myMajor + ",\n\"iBeaconMinor\": "
						+ myMinor + ",\n\"MAC\": " + macAddress + ",\n\"Range\": " + String.format("%.2f",range) + "}";
				//parse message
				//Log.v("sendSignal","Created raw message: " + JsonString);
				MqttMessage message = new MqttMessage();	
				message.setQos(0);
				message.setRetained(true);
				message.setPayload(payload.getBytes());
				Log.v("sendSignal","Attempting to publish message to broker");
				if(!androidClient.isConnected()) {
    				MqttConnectOptions conOpt = new MqttConnectOptions();
    		    	conOpt.setKeepAliveInterval(240000);
    		    	androidClient.connect(conOpt);
    			}
				androidClient.publish(topic, message);
				Log.v("sendSignal","Message (hopefully) published to broker");
    		} 
		}
    	catch (MqttSecurityException e) {
    		e.printStackTrace();
    	} 
    	catch (MqttException e) {
    		e.printStackTrace();
		}
	}
    
    public void sendBeacon2Platform(IBeacon bacon){ //cutting edge awesome send function
    	if ((Integer)bacon.getMajor()==null || (Integer)bacon.getMinor()==null || bacon.getProximityUuid()==null)
		{
    		//put something here...
    		Log.v("sendSignal","Picked up an unhandled null/wildcard beacon identity.");
		}
		else{
			try 
	    	{
				//get stuff
				String myMajor = ((Integer)bacon.getMajor()).toString();
				String myMinor = ((Integer)bacon.getMinor()).toString();
				String myUuid = bacon.getProximityUuid();
				//make message in JSON format for easier parsing in platform
				String payload = "{ \"iBeaconUuid: \"" + myUuid + ",\n\"iBeaconMajor: \"" + myMajor + ",\n\"iBeaconMinor: \""
						+ myMinor + ",\n\"MAC: \"" + macAddress + ",\n\"Range: \"" + String.format("%.2f",bacon.getAccuracy()) + "}";
				//parse and send message
				MqttMessage message = new MqttMessage();	
				message.setQos(0);
				message.setRetained(false);
				message.setPayload(payload.getBytes());
				Log.v("sendSignal","Attempting to publish message to broker");
				if(!androidClient.isConnected()) {
					MqttConnectOptions conOpt = new MqttConnectOptions();
			    	conOpt.setKeepAliveInterval(240000);
			    	androidClient.connect(conOpt);
				}
				androidClient.publish(topic, message);
				Log.v("sendSignal","Message (hopefully) published to broker");
//				logToDisplay("Message sent: Major " + myMajor + ", Minor: " + myMinor);
	    	}
	    	catch (MqttSecurityException e) {
	    		e.printStackTrace();
	    	} 
	    	catch (MqttException e) {
	    		e.printStackTrace();
	    	}
		}
    }


    @Override
	public void onDestroy() {
        super.onDestroy();
        iBeaconManager.unBind(this);
        /*alertedBeacons = new ArrayList<IBeacon>();
        try {
        	if(androidClient != null && androidClient.isConnected()) {
	        	androidClient.disconnect();
	        	androidClient.close();
        	}
		} catch (MqttException e) {
			e.printStackTrace();
		}*/
    }
    
//    private void logToDisplay(final String line) {
//    	runOnUiThread(new Runnable() {
//    	    public void run() {
//    	    	EditText editText = (EditText)MonitoringActivity.this
//    					.findViewById(R.id.monitoringText);
//       	    	editText.append(line+"\n");            	    	    		
//    	    }
//    	});
//    }
    
    public void sendAllNearOrImmediate(Collection<IBeacon> iBeacons, Region region, Boolean onlyOnce){
    	if (iBeacons.size() > 0)
		{
			for(IBeacon kevinBeacon : iBeacons){
				//fetch proximity information
				int proxy = kevinBeacon.getProximity();
				if((proxy==1 || proxy==2) && !(alertedBeacons.contains(kevinBeacon))) //near or immediate, and not in already-"alerted" beacons
					{
					//alert platform
//					logToDisplay("NEAR iBeacon detected, sending MQTT...");
					Log.d("NearAdvert", "NEAR iBeacon detected, sending MQTT....");
					sendBeacon2Platform(kevinBeacon);
//					logToDisplay("Message sent!");
					Log.d("NearAdvert","Near/Immediate message sent!");
					if (onlyOnce){
						alertedBeacons.add(kevinBeacon);
					}
					try {
						iBeaconManager.stopRangingBeaconsInRegion(region);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					}
			}
		}
    }
    
    @Override
    public void onIBeaconServiceConnect() {
        iBeaconManager.setMonitorNotifier(new MonitorNotifier() {
        	
        	
	        @Override
	        public void didEnterRegion(Region region) 
	        {
//	        	logToDisplay("Discovered iBeacon region: "+ region.getUniqueId());
	        	iBeaconManager.setRangeNotifier(new RangeNotifier() 
				{
					@Override
					public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons,
							Region region)
						{
							////choose one! 
							//sendAllNearOrImmediate(iBeacons, region, true); //last argument is whether to send MQTT only once for each thing
							if (iBeacons.size()>0){
								sendNearestBeacon(iBeacons);
							}
						}
				});
	        	try 
				{	
	        		iBeaconManager.startRangingBeaconsInRegion(region);
				} 
				catch (RemoteException e) {}
	        }
	
	        @Override
	        public void didExitRegion(Region region) {
//	        	logToDisplay("Lost iBeacon region: "+ region.getUniqueId());
	        	//stop ranging
	        	try 
				{
					iBeaconManager.stopRangingBeaconsInRegion(region);
				} 
				catch (RemoteException e) {}
	        }
	
	        @Override
	        public void didDetermineStateForRegion(int state, Region region) {
//	        	logToDisplay("I have just switched from seeing/not seeing iBeacons: "+state);     
	        	//add stopRanging or startRanging
	        }
	        
        });

        try {
//        	Region targetRegion = new Region(targetName, targetUuid, targetMajor, targetMinor);
        	Region targetRegion = new Region(targetName, targetUuid, null, null); //null is wildcard
        	iBeaconManager.startMonitoringBeaconsInRegion(targetRegion);
        } catch (RemoteException e) {   }
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}

package com.radiusnetworks.ibeaconreference;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class TestService extends IntentService {

	public TestService(String name) {
		super("TestService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("TEST", "Testservice kjører");
	}

}

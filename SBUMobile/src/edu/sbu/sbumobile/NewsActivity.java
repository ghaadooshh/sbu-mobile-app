package edu.sbu.sbumobile;

import android.content.Intent;
import android.os.Bundle;

public class NewsActivity extends BaseActivity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		
		// Substitute for the start/stop menu system in Yamba; updater service started directly.
		startService(new Intent(this, UpdaterService.class));		
	}

}

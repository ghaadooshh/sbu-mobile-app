package edu.sbu.sbumobile;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class BaseActivity extends Activity {
	MobileApplication app;
	private static final String TAG = MobileApplication.class.getSimpleName();

	ProgressBar mProgress;
	LinearLayout ProgressView;
	String ProgressDismiss = "INVISIBLE";

	IncomingReceiver ProgressReceiver;
	IntentFilter ProgressFilter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (MobileApplication) getApplication();
		

		ProgressReceiver = new IncomingReceiver();
		ProgressFilter = new IntentFilter();
		ProgressFilter.addAction(MobileApplication.UPDATE_PROGRESS);
		ProgressFilter.addAction(MobileApplication.SHOW_PROGRESS);
		ProgressFilter.addAction(MobileApplication.HIDE_PROGRESS);
	    
		registerReceiver(ProgressReceiver, ProgressFilter);
	}
	
	public class IncomingReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MobileApplication.HIDE_PROGRESS)) {
				System.out.println("Hiding Progress");
				if (ProgressDismiss == "INVISIBLE")
					ProgressView.setVisibility(LinearLayout.INVISIBLE);
				else
					ProgressView.setVisibility(LinearLayout.GONE);
			} else if (action.equals(MobileApplication.SHOW_PROGRESS)) {
				System.out.println("Showing Progress");
				mProgress.setMax(app.ProgressMax);
				mProgress.setProgress(0);
				app.CalendarProgress = 0;
				ProgressView.setVisibility(LinearLayout.VISIBLE);
			} else if (action.equals(MobileApplication.UPDATE_PROGRESS)) {
				int progress = intent.getIntExtra("progress", app.CalendarProgress);
				System.out.println("Updating Progress "+ progress);
				mProgress.setProgress(progress);
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// UNregister the receiver
		unregisterReceiver(ProgressReceiver); 
	}
	//Called once - first menu click
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	//Called every menu click
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "Menu Item Selected");
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		}
		return true;
	}
	/* For toggling menu items
	 * Ex:Start/Stop
	 * 
	//Called every time menu is opened
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return true;
	}
	*/
}
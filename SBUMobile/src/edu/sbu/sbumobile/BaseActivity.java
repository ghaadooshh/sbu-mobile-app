package edu.sbu.sbumobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	MobileApplication app;
	private static final String TAG = MobileApplication.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (MobileApplication) getApplication();
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
package edu.sbu.sbumobile;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SBUMobileActivity extends BaseActivity implements OnClickListener {
	AlertDialog ad;
	ProgressBar mProgress;
	LinearLayout ProgressView;
	IncomingReceiver receiver;
	IntentFilter filter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Buttons
		final ImageView newsButton = (ImageView) findViewById(R.id.newsbutton);
		newsButton.setOnClickListener(this);

		final ImageView eventsButton = (ImageView) findViewById(R.id.eventsbutton);
		eventsButton.setOnClickListener(this);

		final ImageView sportsButton = (ImageView) findViewById(R.id.sportsbutton);
		sportsButton.setOnClickListener(this);
		
		//Alert for no Internet connection
		ad = new AlertDialog.Builder(this).create();
		ad.setCancelable(false);
		ad.setMessage("No internet connection is available.");
		ad.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		//Progress Bar for calendar
	    receiver = new IncomingReceiver();
	    filter = new IntentFilter();
	    filter.addAction(MobileApplication.UPDATE_PROGRESS);
	    filter.addAction(MobileApplication.SHOW_PROGRESS);
	    filter.addAction(MobileApplication.HIDE_PROGRESS);
	    
        mProgress = (ProgressBar) findViewById(R.id.progressbar);
		mProgress.setMax(12);
		ProgressView = (LinearLayout) findViewById(R.id.CalendarProgress);

		registerReceiver(receiver, filter);
		if(app.calendarLoading) {
			System.out.println("onStart Showing Progress");
			ProgressView.setVisibility(LinearLayout.VISIBLE);
		} else {
			System.out.println("onStart Hiding Progress");
			ProgressView.setVisibility(LinearLayout.INVISIBLE);
		}
	}

	public class IncomingReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MobileApplication.HIDE_PROGRESS)) {
				System.out.println("Hiding Progress");
				ProgressView.setVisibility(LinearLayout.INVISIBLE);
			} else if (action.equals(MobileApplication.SHOW_PROGRESS)) {
				System.out.println("Showing Progress");
				mProgress.setProgress(0);
				ProgressView.setVisibility(LinearLayout.VISIBLE);
			} else if (action.equals(MobileApplication.UPDATE_PROGRESS)) {
				System.out.println("Updating Progress");
				mProgress.incrementProgressBy(1);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// UNregister the receiver
		unregisterReceiver(receiver); 
	}
	
	public boolean isOnline() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	@Override
	public void onClick(View v) {
		if (isOnline()) {
			switch (v.getId()) {
			case (R.id.newsbutton):
					Toast.makeText(getApplicationContext(), R.string.loading,
							Toast.LENGTH_SHORT).show();
					startActivity(new Intent(this, NewsActivity.class));
				break;
			case (R.id.eventsbutton):
					startActivity(new Intent(this, EventsActivity.class));
				break;
			case (R.id.sportsbutton):
					Toast.makeText(getApplicationContext(), R.string.loading,
							Toast.LENGTH_SHORT).show();
					startActivity(new Intent(this, SportsActivity.class));
				break;
			}
		} else {
			ad.show();
		}
		
	}
}
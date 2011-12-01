package edu.sbu.sbumobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class SBUMobileActivity extends BaseActivity implements OnClickListener {
	AlertDialog ad;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final ImageView newsButton = (ImageView) findViewById(R.id.newsbutton);
		newsButton.setOnClickListener(this);

		final ImageView eventsButton = (ImageView) findViewById(R.id.eventsbutton);
		eventsButton.setOnClickListener(this);

		final ImageView sportsButton = (ImageView) findViewById(R.id.sportsbutton);
		sportsButton.setOnClickListener(this);
		
		ad = new AlertDialog.Builder(this).create();
		ad.setCancelable(false);
		ad.setMessage("No internet connection is available.");
		ad.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
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
package edu.sbu.sbumobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class SBUMobileActivity extends BaseActivity implements OnClickListener {
	private AlertDialog ad;
	private ImageView bigSeal;
	private boolean sealBackground = true;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Buttons
		final Button newsButton = (Button) findViewById(R.id.newsbutton);
		newsButton.setOnClickListener(this);

		final Button eventsButton = (Button) findViewById(R.id.eventsbutton);
		eventsButton.setOnClickListener(this);

		final Button sportsButton = (Button) findViewById(R.id.sportsbutton);
		sportsButton.setOnClickListener(this);
		
		final Button mapButton = (Button) findViewById(R.id.mapbutton);
		mapButton.setOnClickListener(this);
		
		bigSeal = (ImageView) findViewById(R.id.bigseal);
		bigSeal.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				if(sealBackground) {
					bigSeal.setImageResource(R.drawable.seal_notepaper);
					sealBackground = false;
				} else {
					bigSeal.setImageResource(R.drawable.seal_normal);
					sealBackground = true;
				}
				return false;
			}
		});
		
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
		mProgress = (ProgressBar) findViewById(R.id.LoadingProgressBar);
		ProgressView = (LinearLayout) findViewById(R.id.LoadingLayout);

		if(app.calendarLoading) {
			System.out.println("onStart Showing Progress");
			ProgressView.setVisibility(LinearLayout.VISIBLE);
		} else {
			System.out.println("onStart Hiding Progress");
			ProgressView.setVisibility(LinearLayout.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		if (app.isOnline()) {
			switch (v.getId()) {
			case (R.id.newsbutton):
					startActivity(new Intent(this, NewsActivity.class));
				break;
			case (R.id.eventsbutton):
					startActivity(new Intent(this, EventsActivity.class));
				break;
			case (R.id.sportsbutton):
					startActivity(new Intent(this, SportsActivity.class));
				break;
			case (R.id.mapbutton):
					startActivity(new Intent(this, SBUCampusActivity.class));
				break;
			}
		} else {
			ad.show();
		}
		
	}
	
}
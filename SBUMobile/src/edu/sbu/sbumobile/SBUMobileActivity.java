package edu.sbu.sbumobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SBUMobileActivity extends BaseActivity implements OnClickListener {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);		
	
		final ImageView newsButton = (ImageView) findViewById(R.id.newsbutton);
		newsButton.setOnClickListener(this);
		
		final ImageView eventsButton = (ImageView) findViewById(R.id.eventsbutton);
		eventsButton.setOnClickListener(this);
		
		final ImageView alertsButton = (ImageView) findViewById(R.id.alertsbutton);
		alertsButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case(R.id.newsbutton):
				startActivity(new Intent(this, NewsActivity.class));
			break;
			case(R.id.eventsbutton):
				startActivity(new Intent(this, EventsActivity.class));
			break;
			case(R.id.alertsbutton):
				startActivity(new Intent(this, AlertsActivity.class));
			break;
		}
	}
}
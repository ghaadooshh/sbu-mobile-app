package edu.sbu.sbumobile;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SBUMobileActivity extends BaseActivity {
	//implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);		
	
		final ImageView newsButton = (ImageView) findViewById(R.id.newsbutton);
		newsButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            // Perform action on click
	        	System.out.println("Clicked");
//	    		Toast.makeText(context, "News clicked", 10000);
	        }
	    });
	}
}
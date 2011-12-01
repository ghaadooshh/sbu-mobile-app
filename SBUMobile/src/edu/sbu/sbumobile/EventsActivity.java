package edu.sbu.sbumobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import edu.sbu.sbumobile.MobileApplication.CalendarEntry;


public class EventsActivity extends BaseActivity {
	private ListView listView;
	private UserItemAdapter adapter;
	IncomingReceiver receiver;
	IntentFilter filter;
	AlertDialog ad;
	  
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);

	    receiver = new IncomingReceiver();
	    filter = new IntentFilter(MobileApplication.SEND_CALENDER);
	    
		if((!app.calendarLoading) && (!app.calendar.isEmpty())) {
			setView();
		}
		if(!app.calendarLoading && app.calendar.isEmpty()) {
			app.DownloadCalendar();
		}
		
		ad = new AlertDialog.Builder(EventsActivity.this).create();
		ad.setCanceledOnTouchOutside(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Register the receiver
		super.registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// UNregister the receiver
		unregisterReceiver(receiver); 
	}
	
	public class IncomingReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("IncomingReceiver - onReceived");
			setView();
		}
	}

	public void setView() {
        listView = (ListView) findViewById(R.id.EventsListView);
        adapter = new UserItemAdapter(getApplicationContext(), R.layout.calitem, app.calendar);

		listView.setAdapter(adapter.adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long duration)
				{
					Map<String,String> item = (Map<String, String>) adapter.adapter.getItem(position);
					item.get("calTitle");
					item.get("calDate");
					item.get("calAuthor");
					String time = item.get("calTime");
					if (item.get("calTime") == null)
						time = "All Day";
					String details = time + "\n" + item.get("calAuthor");

					ad.setTitle(item.get("calTitle"));
					ad.setMessage(details);
					ad.show();

				}
		});
	}

	
	public class UserItemAdapter extends ArrayAdapter<CalendarEntry> {
		private ArrayList<CalendarEntry> calendar;
		public SeparatedListAdapter adapter;
	    public final static String CAL_TITLE = "calTitle";  
	    public final static String CAL_DATE = "calDate";  
	    public final static String CAL_AUTHOR = "calAuthor";  
	    public final static String CAL_TIME = "calTime"; 
	    
	    public Map<String,?> createItem(String title, String date, String author, String time) {  
	        Map<String,String> item = new HashMap<String,String>();  
	        item.put(CAL_TITLE, title);  
	        item.put(CAL_DATE, date);  
	        item.put(CAL_AUTHOR, author); 
	        item.put(CAL_TIME, time); 
	        return item;  
	    }  
	    
		public UserItemAdapter (Context context, int textViewResourceId, ArrayList<CalendarEntry> calendar) {
			super(context, textViewResourceId, calendar);

			this.calendar = calendar;
			this.adapter = new SeparatedListAdapter(context);
			
			//thisDay is a container for all the events for a single day
			ArrayList<CalendarEntry> thisDay = new ArrayList<CalendarEntry>();
			for (int i=0; i < this.calendar.size()-1; i++) {
	        	CalendarEntry current = this.calendar.get(i);
	        	CalendarEntry next = this.calendar.get(i+1);
	        	
	        	//If events are the same add both and keep going
	        	if(current.sortDate.compareTo(next.sortDate) == 0) {
	        		if (!thisDay.contains(current))
	            		thisDay.add(current);
	        		if (!thisDay.contains(next))
	        			thisDay.add(next);
	        	//All the events for that day are saved
	        	//Or there is only one event for that day, so add that one day
	        	} else {
	        		if (!thisDay.contains(current))
	            		thisDay.add(current);
	        		
	        		//events contains the titles for each event
	                List<Map<String,?>> events = new LinkedList<Map<String,?>>();  
	                for (CalendarEntry entry : thisDay)
	                	events.add(createItem(entry.title, entry.startDate, entry.author, entry.fullTime));

	                //Add section to adapter and clear thisDay for next events
	        		this.adapter.addSection(thisDay.get(0).startDate, new SimpleAdapter(context, events, R.layout.calitem,  
	                        new String[] { CAL_TITLE, CAL_DATE, CAL_AUTHOR, CAL_TIME },
	                        new int[] { R.id.calTitle, R.id.calDate, R.id.calAuthor, R.id.calTime })); 
	        		thisDay.clear();
	        	}
			}//for

		}//constructor
	}//class

	// Called only once first time menu is clicked on
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.eventsmenu, menu);
		return true;
	}

	// Called every time user clicks on a menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.itemRefresh:
			app.DownloadCalendar();
			break;
		}
		return true;
	}
}



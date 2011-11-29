package edu.sbu.sbumobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class EventsActivity extends BaseActivity {
	private ListView listView;
	private UserItemAdapter adapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);

		getSBUCalendars();
//		ArrayList<CalendarEntry> calendar = app.calendar;
//		app.calendar = calendar;
		
        listView = (ListView) findViewById(R.id.EventsListView);
        adapter = new UserItemAdapter(this, R.layout.calitem, app.calendar);
        
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
					Toast.makeText(getApplicationContext(), time, Toast.LENGTH_SHORT).show();
				}
		});
	}
	
	//Called once - first menu click
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.eventsmenu, menu);
		return true;
	}
	
	//Called every menu click
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.itemRefresh:
			break;
		}
		return true;
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

	private void getSBUCalendars() {
		if(app.calendar.isEmpty()) {
			System.out.println("Loading Calendar 1/11");
			app.calendar = getCalendar(app.calendar, "bju1h24pdb4qkh3flljbv2c374"); //SBU
			System.out.println("Loading Calendar 2/11");
			app.calendar = getCalendar(app.calendar, "6lio3us2rcug7v4hou9u4nhqes"); //SBU-SGA
			System.out.println("Loading Calendar 3/11");
			app.calendar = getCalendar(app.calendar, "c4ofc4j0efrl0btftglmu4of9s"); //SBU-UAC
			System.out.println("Loading Calendar 4/11");
			app.calendar = getCalendar(app.calendar, "th7almtgpen847q8af96fc6dd0"); //SBUacademics
			System.out.println("Loading Calendar 5/11");
			app.calendar = getCalendar(app.calendar, "5pg4h86s5mka7k1ooqdtmg2gl8"); //SBUathletics
			System.out.println("Loading Calendar 6/11");
			app.calendar = getCalendar(app.calendar, "o9cvggpgdmlnqv210arv0s54so"); //SBUChapel
			System.out.println("Loading Calendar 7/11");
			app.calendar = getCalendar(app.calendar, "27ac4a7hv3qfcn8s4ac0tppt8s"); //SBUclubs/organizations
			System.out.println("Loading Calendar 8/11");
			app.calendar = getCalendar(app.calendar, "otm002ck939ft163n57nfdbbno"); //SBUintramuralSports
			System.out.println("Loading Calendar 9/11");
			app.calendar = getCalendar(app.calendar, "0srvkmajlvoo5d21lgoo9htdc8"); //SBUperformingArts
			System.out.println("Loading Calendar 10/11");
			app.calendar = getCalendar(app.calendar, "90pdj27la6p3ismpk2h6871ok0"); //SBUresidenceLife
			System.out.println("Loading Calendar 11/11");
			app.calendar = getCalendar(app.calendar, "kjn15va1uanr5huju3ds6fua8c"); //Summer Camps
		}
	}
	
	private ArrayList<CalendarEntry> getCalendar (ArrayList<CalendarEntry> calendar, String calId) {
		String calUrl = "http://www.google.com/calendar/feeds/"+ calId + "%40group.calendar.google.com/"+
						"public/full?alt=json&orderby=starttime"+/*&max-results=15*/"&singleevents=true"+
						"&sortorder=ascending&futureevents=true&ctz=America%2FChicago";
		//Set up HTTP client and get response from URL
		HttpClient client = new  DefaultHttpClient();
		HttpGet get = new HttpGet(calUrl);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = null;
		try{
			responseBody = client.execute(get, responseHandler);
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		//Parse response in JSON Object
		JSONObject jsonObject = null;
		JSONParser parser = new JSONParser();
		try {
			jsonObject=(JSONObject) parser.parse(responseBody);
		}catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("responseBody: " + responseBody);
			Toast.makeText(getApplicationContext(), "Error getting calendar", Toast.LENGTH_SHORT).show();
			return calendar;
		}
		JSONObject feed = (JSONObject) jsonObject.get("feed");
		
		//Get calendar entries from JSONObject, 
		//Iterate through entries and add wanted info to new ArrayList
		ArrayList<?> entry = (ArrayList<?>) feed.get("entry");
		
		//If no entries return calendar
		if (entry == null)
			return calendar;
		
		for(Object i : entry) {
			JSONObject obj = (JSONObject) i;
		  //title
			String title = (String)((JSONObject) obj.get("title")).get("$t");
//			System.out.println("Title: " + title);
		  //author
			JSONObject firstAuthor = (JSONObject) ((ArrayList<?>) obj.get("author")).get(0);
			String author = (String) ((JSONObject) firstAuthor.get("name")).get("$t");
		  //where
			String where = (String) ((JSONObject) ((ArrayList<?>) obj.get("gd$where")).get(0)).get("valueString");
		  //when
			JSONObject when = (JSONObject) ((ArrayList<?>) obj.get("gd$when")).get(0);
			//FormatDate splits dates and times and formats them correctly
			FormatDate start = new FormatDate((String) when.get("startTime"));
			FormatDate end = new FormatDate((String) when.get("endTime"));
			
			String fullTime = null;
			if (!start.allDay)
				fullTime = start.time + " - " + end.time;
	
			CalendarEntry newEntry = new CalendarEntry(title, author, where, start.sortDate, start.date,
												end.date, start.allDay, start.time, end.time, fullTime);
		  //Add entry to calendar
			calendar.add(newEntry);
		}
		
		//Sort entries
		Collections.sort(calendar, new Comparator<CalendarEntry>() {
			public int compare(CalendarEntry one, CalendarEntry two){
				
				return (one.sortDate).compareTo(two.sortDate);
				
			}
		});

		return calendar;
	}
	
	public class CalendarEntry {
		public String title;
		public String author;
		public String where;
		public Date sortDate;
		public String startDate;
		public String endDate;
		public boolean allDay;
		public String startTime;
		public String endTime;
		public String fullTime;
		
		public CalendarEntry(
				String title,
				String author,
				String where,
				Date sortDate,
				String startDate,
				String endDate,
				boolean allDay,
				String startTime,
				String endTime,
				String fullTime) {
			this.title = title;
			this.author = author;
			this.where = where;
			this.sortDate = sortDate;
			this.startDate = startDate;
			this.endDate = endDate;
			this.allDay = allDay;
			this.startTime = startTime;
			this.endTime = endTime;
			this.fullTime = fullTime;
		}
	}

	public class FormatDate {
		public String date;
		public String time;
		public boolean allDay;
		public Date sortDate;
		
		public FormatDate(String date) {
			//Not All Day Event
			if (date.contains("T")) {
				this.allDay = false;
				
				//Split Date and Time
				this.date = date.substring(0, 10);
				this.time = date.substring(11, 19);
				
				//Extract 24 hour to convert to 12 hour
				int hour = Integer.parseInt(this.time.substring(0, 2));
				String ampm = "am";
				if (hour == 12) {
					ampm = "pm";
				} else if (hour > 12){
					ampm = "pm";
					hour = hour - 12;
				}
				this.time = Integer.toString(hour) + this.time.substring(2, 5) + ampm;
			//All Day Event
			} else {
				this.allDay = true;
				this.time = null;
			}	
			
			//Convert Date for sorting
			SimpleDateFormat formatter= new SimpleDateFormat ("yyyy-MM-dd");
			Date sortDate = new Date();
			try {
				sortDate = formatter.parse(date);
			} catch (ParseException e) {
				System.out.println("Error Parsing");
				e.printStackTrace();
			}
			this.sortDate = sortDate;

			//Set Date for display
			Date now = new Date();
			if (now.getYear() == sortDate.getYear())
				formatter.applyPattern("EEEE, MMMM d");
			else
				formatter.applyPattern("EEEE, MMMM d, yyyy");
			this.date = formatter.format(sortDate);
		}
	}
}



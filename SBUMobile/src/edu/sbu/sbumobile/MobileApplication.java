package edu.sbu.sbumobile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MobileApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = MobileApplication.class.getSimpleName();
	private SharedPreferences prefs;
	public ArrayList<CalendarEntry> calendar = new ArrayList<CalendarEntry>();
	public boolean calendarLoading = false;
	public static final String SEND_CALENDER = "edu.sbu.sbumobile.SEND_CALENDER";
	public Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		Log.i(TAG, "Application Started");
		mContext = this;
		DownloadCalendar();

	}

	/*
	 * Prefs can be pulled by: String username =
	 * this.prefs.getString("username", null); ^ ^ from xml default value
	 * 
	 * Not sure yet what prefs we have or how they will need to be handled.
	 * Could use onSharedPreferenceChanged() below or use PrefsActivity. See
	 * TicTacToe preferences.java for examples.
	 */
	@Override
	public synchronized void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences, String key) {
		Log.i(TAG, "onSharedPreferenceChanged");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "Application Terminated");
	}
	
	public void DownloadCalendar() {
		Toast.makeText(getApplicationContext(), "Loading Calendar", Toast.LENGTH_LONG).show();
		this.calendarLoading = true;
		calendar.clear();
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute(new String[] { "bju1h24pdb4qkh3flljbv2c374",
									"6lio3us2rcug7v4hou9u4nhqes",
									"c4ofc4j0efrl0btftglmu4of9s",
									"th7almtgpen847q8af96fc6dd0",
									"5pg4h86s5mka7k1ooqdtmg2gl8",
									"o9cvggpgdmlnqv210arv0s54so",
									"27ac4a7hv3qfcn8s4ac0tppt8s",
									"otm002ck939ft163n57nfdbbno",
									"0srvkmajlvoo5d21lgoo9htdc8",
									"90pdj27la6p3ismpk2h6871ok0",
									"kjn15va1uanr5huju3ds6fua8c" });
	}
	
	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String response = "[";
			DefaultHttpClient client = new DefaultHttpClient();
			for (String url : urls) {
				String calUrl = "http://www.google.com/calendar/feeds/"+ url + "%40group.calendar.google.com/"+
								"public/full?alt=json&orderby=starttime"+/*&max-results=15*/"&singleevents=true"+
								"&sortorder=ascending&futureevents=true&ctz=America%2FChicago";
				HttpGet httpGet = new HttpGet(calUrl);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				try{
					response += client.execute(httpGet, responseHandler) + ",";
				}catch(Exception ex) {
					ex.printStackTrace();
					System.out.println("Exception: " + ex);
				}
				
			}
			response += "]";
			return response;
		}
	    
		@Override
		protected void onPostExecute(String result) {
			parseCalendar(result);
		}
	}
	
	private void parseCalendar (String responseBody) {
		//Parse response in JSON Object
		JSONArray jsonCalendars = null;
		JSONParser parser = new JSONParser();
		try {
			jsonCalendars =(JSONArray) parser.parse(responseBody);
		}catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("responseBody: " + responseBody);
			Toast.makeText(getApplicationContext(), "Error getting calendar", Toast.LENGTH_SHORT).show();
			return;
		}
		for (Object jsonObject : jsonCalendars) {

			JSONObject feed = (JSONObject) ((JSONObject) jsonObject).get("feed");
			//Get calendar entries from JSONObject, 
			//Iterate through entries and add wanted info to new ArrayList
			ArrayList<?> entry = (ArrayList<?>) feed.get("entry");
			
			//If no entries back out
			if (entry != null) {
				
				for(Object i : entry) {
					JSONObject obj = (JSONObject) i;
				  //title
					String title = (String)((JSONObject) obj.get("title")).get("$t");
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
					this.calendar.add(newEntry);
					}//for each entry
			}//If entries
		}//for each calendar
		
		//Sort entries
		Collections.sort(this.calendar, new Comparator<CalendarEntry>() {
			public int compare(CalendarEntry one, CalendarEntry two){
				return (one.sortDate).compareTo(two.sortDate);
			}
		});

		this.calendarLoading = false;
		System.out.println("Done Loading");
		Toast.makeText(getApplicationContext(), "Calendar Loaded", Toast.LENGTH_SHORT).show();

        Intent broadcast = new Intent();
        broadcast.setAction(SEND_CALENDER);
        sendBroadcast(broadcast);

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
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class MobileApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = MobileApplication.class.getSimpleName();
	private SharedPreferences prefs;
	
	//Calendar
	public ArrayList<CalendarEntry> calendar = new ArrayList<CalendarEntry>();
	public boolean calendarLoading = false;
	private int errorLoading = 0;
	public static final String SEND_CALENDER = "edu.sbu.sbumobile.SEND_CALENDER";

	//ProgressBar
	public static final String SHOW_PROGRESS = "edu.sbu.sbumobile.SHOW_PROGRESS";
	public static final String HIDE_PROGRESS = "edu.sbu.sbumobile.HIDE_PROGRESS";
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		Log.i(TAG, "Application Started");

		DownloadFeed(true); //true for yahooFeed, false for googleFeed

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
	
	public boolean isOnline() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
	
	public void DownloadFeed(boolean yahooFeed) {
		if (/*errorLoading <= 1 && */isOnline()) {
			this.calendarLoading = true;
	        sendBroadcast(new Intent(SHOW_PROGRESS));
	        
			calendar.clear();
			DownloadWebPageTask task = new DownloadWebPageTask();
			if (yahooFeed) {
				task.execute(new String[] { "http://pipes.yahoo.com/pipes/pipe.run?_id"+
				"=6aaad7af7ac90586d6971f428a3b25b4&_render=json"});
			} else {
				task.execute(new String[] { "bju1h24pdb4qkh3flljbv2c374",//SBU *
											"6lio3us2rcug7v4hou9u4nhqes",//SBU-SGA
											"c4ofc4j0efrl0btftglmu4of9s",//SBU-UAC *
											"th7almtgpen847q8af96fc6dd0",//SBUacademics *
											"5pg4h86s5mka7k1ooqdtmg2gl8",//SBUathletics
											"o9cvggpgdmlnqv210arv0s54so",//SBUChapel
											"27ac4a7hv3qfcn8s4ac0tppt8s",//SBUclubs/organizations
											"otm002ck939ft163n57nfdbbno",//SBUintramuralSports
											"0srvkmajlvoo5d21lgoo9htdc8",//SBUperformingArts *
											"90pdj27la6p3ismpk2h6871ok0",//SBUresidenceLife
											"kjn15va1uanr5huju3ds6fua8c" });//Summer Camps
			}
		}
	}
	
	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
		boolean googleFeed = false;
		@Override
		protected String doInBackground(String... urls) {
			
			String response = "";
			DefaultHttpClient client = new DefaultHttpClient();
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			if(urls.length > 1) {
				googleFeed = true;
				response += "[";
			}
			for (String url : urls) {
				String calUrl;
				if (googleFeed)
					calUrl = "http://www.google.com/calendar/feeds/"+ url + "%40group.calendar.google.com/"+
							 "public/full?alt=json&orderby=starttime&singleevents=true"+
							 "&sortorder=ascending&futureevents=true&ctz=America%2FChicago";
				else
					calUrl = url;
				HttpGet httpGet = new HttpGet(calUrl);
				try{
					response += client.execute(httpGet, responseHandler);
					if(googleFeed)
						response += ",";
				}catch(Exception ex) {
					ex.printStackTrace();
					System.out.println("Exception: " + ex);
					calendarLoading = false;
			        sendBroadcast(new Intent(HIDE_PROGRESS));
			        errorLoading++;
//			        DownloadFeed(false);
				}
		        
			}
			if (googleFeed)
				response += "]";
			return response;
		}
	    
		@Override
		protected void onPostExecute(String result) {
			if (googleFeed)
				parseCalendar(false, result);
			else
				parseCalendar(true, result);
		}
	}
	
	private void parseCalendar (Boolean yahooFeed, String responseBody) {
		//Parse response in JSON Object
		JSONObject YahooFeed = null;
		JSONArray jsonCalendars = null;
		JSONParser parser = new JSONParser();
		try {
			if (yahooFeed) {
				YahooFeed =(JSONObject) parser.parse(responseBody);
				jsonCalendars = (JSONArray) ((JSONObject) YahooFeed.get("value")).get("items");
			} else {
				jsonCalendars =(JSONArray) parser.parse(responseBody);
			}
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			System.out.println("responseBody: " + responseBody);
//			Toast.makeText(getApplicationContext(), "Error parsing calendar", Toast.LENGTH_SHORT).show();
			calendarLoading = false;
	        sendBroadcast(new Intent(HIDE_PROGRESS));
	        errorLoading++;
//	        DownloadFeed(false);
			return;
		}
		for (Object jsonObject : jsonCalendars) {
	
			JSONObject feed = (JSONObject) ((JSONObject) jsonObject).get("feed");
			//Get calendar entries from JSONObject, 
			//Iterate through entries and add wanted info to new ArrayList
			ArrayList<?> entries = (ArrayList<?>) feed.get("entry");
			
			//If no entries back out
			if (entries != null) {
				for(Object entry : entries) {
					JSONObject obj = (JSONObject) entry;
					
					String title, author, where;
					JSONObject when;
					if (yahooFeed) {
						title = (String)((JSONObject) obj.get("title")).get("_t");
						author = (String) ((JSONObject) ((JSONObject) obj.get("author")).get("name")).get("_t");
						where = (String) ((JSONObject) (obj.get("gd_where"))).get("valueString");
						when = (JSONObject) obj.get("gd_when");
					} else {
						title = (String)((JSONObject) obj.get("title")).get("$t");
						JSONObject firstAuthor = (JSONObject) ((ArrayList<?>) obj.get("author")).get(0);
						author = (String) ((JSONObject) firstAuthor.get("name")).get("$t");
						where = (String) ((JSONObject) ((ArrayList<?>) obj.get("gd$where")).get(0)).get("valueString");
						when = (JSONObject) ((ArrayList<?>) obj.get("gd$when")).get(0);
					}
					
					//FormatDate splits dates and times and formats them correctly
					FormatDate start = new FormatDate((String) when.get("startTime"));
					FormatDate end = new FormatDate((String) when.get("endTime"));
					
					String fullTime = null;
					if (!start.allDay)
						fullTime = start.time + " - " + end.time;

					CalendarEntry newEntry = new CalendarEntry(title, author, where, start.sortDate, start.date,
							end.date, start.allDay, start.time, end.time, fullTime);
					calendar.add(newEntry);

					System.out.println(newEntry.title + " " + newEntry.startDate + " " + newEntry.sortDate);
					//Multiple day event
					int numOfDays;
					if ((numOfDays = (end.sortDate.getDate() - start.sortDate.getDate())) > 1) {
						FormatDate nextStart = start;
						for (int i=1; i<numOfDays; i++) {
//							System.out.println(title);
//							System.out.println(start.sortDate);
//							System.out.println(start.date);
							CalendarEntry nextEntry = newEntry;
//							System.out.println(nextStart.sortDate.getDate());
							nextEntry.sortDate.setDate(nextStart.sortDate.getDate() + 1);
							nextEntry.startDate = nextStart.setDisplayDate(nextEntry.sortDate);
//							System.out.println(newEntry.startDate + " " + newEntry.sortDate);
							calendar.add(nextEntry);

							System.out.println(nextEntry.title + " " + nextEntry.startDate + " " + nextEntry.sortDate);
//							System.out.println(nextEntry.startDate + " " + nextEntry.sortDate);
							System.out.println("\n");
							System.out.println("\n");
							for(CalendarEntry cal : calendar) {
								System.out.println(cal.title + " " + cal.startDate + " " + cal.sortDate);
							}
						}
					}
				}//for each entry
			}//If entries
	
		}//for each calendar
		
		//Sort entries
		Collections.sort(calendar, new Comparator<CalendarEntry>() {
			public int compare(CalendarEntry one, CalendarEntry two){
				return (one.sortDate).compareTo(two.sortDate);
			}
		});
		System.out.println("\n");
		System.out.println("\n");
		for(CalendarEntry cal : calendar) {
			System.out.println(cal.title + " " + cal.startDate + " " + cal.sortDate);
		}
	
		this.calendarLoading = false;
	
	    sendBroadcast(new Intent(HIDE_PROGRESS));
		
	    sendBroadcast(new Intent(SEND_CALENDER));
	
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
//			Date now = new Date();
//			if (now.getYear() == sortDate.getYear())
//				formatter.applyPattern("EEEE, MMMM d");
//			else
//				formatter.applyPattern("EEEE, MMMM d, yyyy");
//			this.date = formatter.format(sortDate);
			this.date = setDisplayDate(sortDate);
		}
		
		public String setDisplayDate(Date date) {
			SimpleDateFormat formatter= new SimpleDateFormat ();
			Date now = new Date();
			if (now.getYear() == date.getYear())
				formatter.applyPattern("EEEE, MMMM d");
			else
				formatter.applyPattern("EEEE, MMMM d, yyyy");
			return formatter.format(sortDate);
		}
	}
	
	
}
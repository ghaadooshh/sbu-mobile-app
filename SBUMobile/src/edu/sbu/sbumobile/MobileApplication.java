package edu.sbu.sbumobile;

import java.util.ArrayList;

import edu.sbu.sbumobile.EventsActivity.CalendarEntry;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class MobileApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = MobileApplication.class.getSimpleName();
	private SharedPreferences prefs;
	public ArrayList<CalendarEntry> calendar = new ArrayList<CalendarEntry>();

	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		Log.i(TAG, "Application Started");
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
}
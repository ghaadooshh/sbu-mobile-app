package edu.sbu.sbumobile;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class MobileApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = MobileApplication.class.getSimpleName();
	private SharedPreferences prefs;
	private Twitter twitter;
	private boolean serviceRunning; // Is the Twitter service (UpdaterService) running?

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
		
		// Clear the twitter account so that it will reinitialize with the new preferences.
		this.twitter = null;
	}

	/*
	 * Method to send a user name and password to Twitter and get an account
	 * handle in return. Synchronized so that it will be the only object
	 * interacting with Twitter across all activities. See original source code
	 * in YambaApplication.java
	 */
	public synchronized Twitter getTwitter() { //
		if (this.twitter == null) {

			// Values have been hardcoded for now
			String username = "student"; // original value: this.prefs.getString("username", "");
			String password = "password"; // original value: this.prefs.getString("password", "");
			String apiRoot = "http://yamba.marakana.com/api"; // original value: prefs.getString("apiRoot", "http://yamba.marakana.com/api");
			
			if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
					&& !TextUtils.isEmpty(apiRoot)) {
				this.twitter = new Twitter(username, password);
				this.twitter.setAPIRootUrl(apiRoot);
			}
		}
		return this.twitter;
	}

	/*
	 * Check the status of the Twitter running flag.
	 */
	public boolean isServiceRunning() { //
		return serviceRunning;
	}

	/*
	 * Set the status of the Twitter running flag.
	 */
	public void setServiceRunning(boolean serviceRunning) { //
		this.serviceRunning = serviceRunning;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "Application Terminated");
	}
}
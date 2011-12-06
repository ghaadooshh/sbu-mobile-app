package edu.sbu.sbumobile;

import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SportsActivity extends BaseActivity {
	ListView listView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);

//		ArrayList<Tweet> tweets = getTweets("from:sbuniv", 1);
		listView = (ListView) findViewById(R.id.ListViewId);
//		listView.setAdapter(new UserItemAdapter(this, R.layout.listitem, tweets));
		
		mProgress = (ProgressBar) findViewById(R.id.LoadingProgressBar);
		ProgressView = (LinearLayout) findViewById(R.id.LoadingLayout);
		ProgressView.setVisibility(LinearLayout.VISIBLE);

		getBackgroundTweets task = new getBackgroundTweets();
		task.execute("from:sbubearcats");
	}

	public class UserItemAdapter extends ArrayAdapter<Tweet> {
		private ArrayList<Tweet> tweets;

		public UserItemAdapter(Context context, int textViewResourceId,
				ArrayList<Tweet> tweets) {
			super(context, textViewResourceId, tweets);
			this.tweets = tweets;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.listitem, null);
			}

			Tweet tweet = tweets.get(position);
			if (tweet != null) {
				TextView username = (TextView) v.findViewById(R.id.username);
				TextView message = (TextView) v.findViewById(R.id.message);
				ImageView image = (ImageView) v.findViewById(R.id.avatar);

				if (username != null) {
					username.setText(tweet.username);
				}

				if (message != null) {
					message.setText(tweet.message);
				}

				if (image != null) {
					image.setImageBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.seal_tweet_icon));
				}

				message.setMovementMethod(LinkMovementMethod.getInstance());
				message.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							TextView subMessage = (TextView) v;
							String content = subMessage.getText().toString();
							String URL = content.substring(
									content.indexOf("http"), content.length());
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(URL));
							startActivity(i);
						} catch (Exception e) {
						}
					}
				});
			}
			return v;
		}
	}

	public Bitmap getBitmap(String bitmapUrl) {
		try {
			URL url = new URL(bitmapUrl);
			return BitmapFactory.decodeStream(url.openConnection()
					.getInputStream());
		} catch (Exception ex) {
			return null;
		}
	}


	private class getBackgroundTweets extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			
			String response = "";
			DefaultHttpClient client = new DefaultHttpClient();
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			for (String searchTerm : urls) {
				
				String searchUrl = "http://search.twitter.com/search.json?q=@"
						+ searchTerm + "&rpp=100&page=" + "1";
				HttpGet httpGet = new HttpGet(searchUrl);
				try{
					response = client.execute(httpGet, responseHandler);
				}catch(Exception ex) {
					ex.printStackTrace();
					System.out.println("Exception: " + ex);
				}
		        
			}
			return response;
		}
	    
		@Override
		protected void onPostExecute(String result) {
			getTweets(result);
			ProgressView.setVisibility(LinearLayout.GONE);
		}
	}
	
	public void getTweets(String responseBody) {
//		String searchUrl = "http://search.twitter.com/search.json?q=@"
//				+ searchTerm + "&rpp=100&page=" + page;
//
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
//
//		HttpClient client = new DefaultHttpClient();
//		HttpGet get = new HttpGet(searchUrl);
//
//		ResponseHandler<String> responseHandler = new BasicResponseHandler();
//
//		String responseBody = null;
//		try {
//			responseBody = client.execute(get, responseHandler);
//			System.out.println(responseBody);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}

		JSONObject jsonObject = null;
		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(responseBody);
			jsonObject = (JSONObject) obj;

		} catch (Exception ex) {
			Log.v("TEST", "Exception: " + ex.getMessage());
		}

		JSONArray arr = null;

		try {
			Object j = jsonObject.get("results");
			arr = (JSONArray) j;
		} catch (Exception ex) {
			Log.v("TEST", "Exception: " + ex.getMessage());
		}

		for (Object t : arr) {
			Tweet tweet = new Tweet(((JSONObject) t).get("from_user")
					.toString(), ((JSONObject) t).get("text").toString(),
					((JSONObject) t).get("profile_image_url").toString());
			tweets.add(tweet);
		}

		listView.setAdapter(new UserItemAdapter(this, R.layout.listitem, tweets));
	}

	public class Tweet {
		public String username;
		public String message;
		public String image_url;

		public Tweet(String username, String message, String url) {
			this.username = username;
			this.message = message;
			this.image_url = url;
		}
	}
}
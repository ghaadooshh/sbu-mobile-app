package edu.sbu.sbumobile;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class SBUCampusActivity extends MapActivity implements LocationListener {

	private MapView mapView;
	private MyLocationOverlay myLocation;
	private MapController mapController;
	private MyItemizedOverlay itemizedOverlay;
	@SuppressWarnings("unused")
	private LocationManager locationManager;

    private View viewBubble;
    private ViewGroup parentBubble;
    private Context context;
	@Override
	public void onCreate(Bundle icey) {
		super.onCreate(icey);
		setContentView(R.layout.map);
		context = this;
		
		//Setup Map
		mapView = (MapView) findViewById(R.id.m_vwMap);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		mapController = mapView.getController();
		
		//My Location
		myLocation = new MyLocationOverlay(this, mapView);
		//MyLocation moved to onResume/onPause
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		
		//Set View to SBU Campus
		mapController.animateTo(getLocation(37.60446,-93.411475));
		mapController.setZoom(16); 

		//Add pins for buildings
		Drawable pin = this.getResources().getDrawable(R.drawable.red_pin);
		itemizedOverlay = new MyItemizedOverlay(pin);
		mapView.getOverlays().add(itemizedOverlay);

		for (Building i : Buildings()) {
			itemizedOverlay.addOverlay(new OverlayItem(i.location, i.name, i.type));
		}

	}
	
	public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	    private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	    private int selectedIndex;
	    public MyItemizedOverlay(Drawable defaultMarker) {
	        super(boundCenterBottom(defaultMarker));   
	    }

	    @Override
	    protected OverlayItem createItem(int i) {
	        return mOverlays.get(i);
	    }

	    public void addOverlay(OverlayItem overlay) {
	        mOverlays.add(overlay);
	        populate();
	    }

	    public void removeOverlay(OverlayItem overlay) {
	        mOverlays.remove(overlay);
	        populate();
	    }


	    public void clear() {
	        mOverlays.clear();
	        populate();
	    }

	    @Override
	    public int size() {
	        return mOverlays.size();
	    }

	    @Override
    	protected boolean onTap(int index) {
	    	this.selectedIndex = index;
	    	OverlayItem item = mOverlays.get(selectedIndex);
	    	itemizedOverlay.setFocus(item);
	    	GeoPoint point = item.getPoint();
	    	
	        //Remove old bubble
        	mapView.removeView(viewBubble);
        	
        	parentBubble = (ViewGroup) mapView.getParent();
           //We inflate the view with the bubble
            viewBubble = getLayoutInflater().inflate(R.xml.bubble, parentBubble, false);

            //to position the bubble over the map. The -128 and -150 are the offset.
            double size = -128;
            if (item.getTitle().length() > 12)
            	size = size - (item.getTitle().length() - 13)*3.4;
            MapView.LayoutParams mvlp = new MapView.LayoutParams(
                                    MapView.LayoutParams.WRAP_CONTENT,
                                    MapView.LayoutParams.WRAP_CONTENT, 
                                    point,
                                    (int)size, //Horizontal, positive goes right
                                    -150, //Vertical, positive goes down
                                    MapView.LayoutParams.LEFT);

            //Fill the text
            FrameLayout f = (FrameLayout) viewBubble;
            TextView t = (TextView) f.getChildAt(0);
            t.setText(item.getTitle());

            //And the event.
            viewBubble.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
        	        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        	        OverlayItem item = mOverlays.get(selectedIndex);
        	        dialog.setTitle(item.getTitle());
        	        dialog.setMessage(item.getSnippet());
        	        dialog.show();
                }
            });
	        //As you see, add in the map.
            mapView.addView(viewBubble, mvlp);

            return true;
    	}

	}//MyItemizedOverlay
	
	
	private ArrayList<Building> Buildings(){
		ArrayList<Building> buildings = new ArrayList<Building>();
		
		buildings.add(new Building("Health Center",
								"803 S Pike Ave" + "\n" +
								"Bolivar, MO 65613",
								getLocation(37.606644,-93.412389),
								"Administrative",
								""));

		buildings.add(new Building("Sells Administrative Center",
								"1600 University Ave" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.601059,-93.406019),
								"Administrative",
								""));
		
		buildings.add(new Building("Dodson Field",
								"",
								getLocation(37.599085,-93.410257),
								"Athletic Facilities",
								"description"));
		buildings.add(new Building("Plaster Athletic Center",
								"",
								getLocation(37.60314,-93.413165),
								"Athletic Facilities",
								""));
		buildings.add(new Building("Plaster Stadium",
								"",
								getLocation(37.602128,-93.41325),
								"Athletic Facilities",
								""));
		buildings.add(new Building("Soccer Field",
								"",
								getLocation(37.598095,-93.410171),
								"Athletic Facilities",
								""));
		buildings.add(new Building("Softball Field",
								"",
								getLocation(37.598643,-93.411909),
								"Athletic Facilities",
								""));
		buildings.add(new Building("Tennis Courts",
								"",
								getLocation(37.602715,-93.409742),
								"Athletic Facilities",
								""));
		
		buildings.add(new Building("Mabee Chapel",
								"200 W Anderson Dr" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.601444,-93.410456),
								"Chapels",
								""));
		buildings.add(new Building("Randolph Chapel",
								"",
								getLocation(37.600364,-93.409082),
								"Chapels",
								""));
		
		buildings.add(new Building("Dining Commons",
								"",
								getLocation(37.601032,-93.411035),
								"Dining Facilities",
								""));
		buildings.add(new Building("McClelland Dining Facility",
								"1700 E Anderson Dr" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.599391,-93.407328),
								"Dining Facilities",
								""));
		
		buildings.add(new Building("Casebolt Fine Arts",
								"1364 S Pike Ave" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.601546,-93.411464),
								"Faculty Offices/Classroom",
								""));
		
		buildings.add(new Building("Gott Education Center",
								"138 W Estep Dr" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.599939,-93.410445),
								"Faculty Offices/Classroom",
								""));
		
		buildings.add(new Building("Jester Center",
								"651 E Anderson St" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.60232,-93.407366),
								"Faculty Offices/Classroom",
								""));
		
		buildings.add(new Building("Jim Mellers Center",
								"300 W Estep Dr" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.599646,-93.408283),
								"Faculty Offices/Classroom",
								""));
		
		buildings.add(new Building("Meyer Wellness Sports Center",
								"1260 S Pike Ave" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.60246,-93.411475),
								"Faculty Offices/Classroom",
								""));
		
		buildings.add(new Building("Taylor Center",
								"100 W Estep Dr" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.599778,-93.409318),
								"Faculty Offices/Classroom",
								""));
		
		buildings.add(new Building("Wheeler Science",
								"301 E Anderson Dr" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.602362,-93.408363),
								"Faculty Offices/Classroom",
								""));
		
		buildings.add(new Building("University Library",
								"",
								getLocation(37.602349,-93.406754),
								"Library",
								""));
		
		buildings.add(new Building("Hammons Center",
								"520 W. Aldrich Rd" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.597062,-93.416576),
								"Maintenance Facilities",
								""));
		
		buildings.add(new Building("Student Union",
								"1460 S Pike Ave" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.600539,-93.410976),
								"Other",
								""));
		
		buildings.add(new Building("Beasley Hall",
								"234 W South St" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.604432,-93.411786),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Casebolt Apartments",
								"614 S Clark Ave" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.607951,-93.413588),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Craig House",
								"1314 S Lillian" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.601903,-93.41508),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Gott Hall",
								"105 E Aldrich Rd" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.59855,-93.408964),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Landen Hall",
								"1520 S Pike Ave" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.599578,-93.412269),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Leslie Hall",
								"1026 S Pike Ave" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.603599,-93.412124),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Maupin Hall",
								"235 W Austin St" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.607917,-93.411791),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Memorial Hall",
								"806 S Clark Ave" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.60682,-93.413529),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Meyer Hall",
								"1715 University Dr" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.598949,-93.407752),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Plaster Lodge",
								"1730 University Ave" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.599255,-93.408406),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Roseman Apartments",
								"1860 Maple Tree Ln" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.597224,-93.411765),
								"Residence Halls",
								""));
		
		buildings.add(new Building("Woody Hall",
								"105 E Aldrich Rd" + "\n" + 
								"Bolivar, MO 65613",
								getLocation(37.598851,-93.408557),
								"Residence Halls",
								""));
//		//35 Buildings
		return buildings;
	}
	@Override
	protected void onResume() {
		super.onResume();
		myLocation.enableCompass();
		myLocation.enableMyLocation();
		mapView.getOverlays().add(myLocation);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		myLocation.disableMyLocation();
		myLocation.disableCompass();
		mapView.getOverlays().remove(myLocation);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mapToggle:
			if (mapView.isSatellite())
				mapView.setSatellite(false);
			else
				mapView.setSatellite(true);
			break;
		case R.id.itemInfo:
//			startActivity(new Intent(this, PrefsActivity.class)
//				.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		}
		return true;
	}
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		MenuItem toggleItem = menu.findItem(R.id.mapToggle);
		if (mapView.isSatellite()) {
			toggleItem.setTitle("Show Map");
		} else {
			toggleItem.setTitle("Show Satellite");
		}
		return true;
	}
	
	@Override
	public void onLocationChanged(Location arg0) {}
	@Override
	public void onProviderDisabled(String provider) {}
	@Override
	public void onProviderEnabled(String provider) {}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	private GeoPoint getLocation (double lat, double lng) {
		GeoPoint p = new GeoPoint(
			    (int) (lat * 1E6), 
			    (int) (lng * 1E6));
		return p;
	}

	public class Building {
		public String name;
		public String address;
		public GeoPoint location;
		public String type;
		public String description;
		public Building(String name,
						String address,
						GeoPoint location,
						String type,
						String description) {
			this.name = name;
			this.address = address;
			this.location = location;
			this.type = type;
			this.description = description;
			
		}
	}
	
}
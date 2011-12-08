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
	private LocationManager locationManager;
	
	@Override
	public void onCreate(Bundle icey) {
		super.onCreate(icey);
		setContentView(R.layout.map);
		mapView = (MapView) findViewById(R.id.m_vwMap);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		
		myLocation = new MyLocationOverlay(this, mapView);
		myLocation.enableCompass();
		myLocation.enableMyLocation();
		mapView.getOverlays().add(myLocation);
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		
		//Set View to SBU Campus
		mapController.animateTo(getLocation(37.604168, -93.413186));
		mapController.setZoom(16); 

		//Add pins for buildings
		Drawable pin = this.getResources().getDrawable(R.drawable.red_pin);
		itemizedOverlay = new MyItemizedOverlay(pin, this);
		mapView.getOverlays().add(itemizedOverlay);
		
		for (Building i : Buildings()) {
			itemizedOverlay.addOverlay(new OverlayItem(i.location, i.name, i.type));
		}

	}
	
	public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	    private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	    private Context context;
	    
	    public MyItemizedOverlay(Drawable defaultMarker, Context context) {
	        super(boundCenterBottom(defaultMarker));       
	        this.context = context;
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
	    protected boolean onTap(int index)
	    {
	        OverlayItem item = mOverlays.get(index);

	        //Do stuff here when you tap, i.e. :
	        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	        dialog.setTitle(item.getTitle());
	        dialog.setMessage(item.getSnippet());
	        dialog.show();

	        //return true to indicate we've taken care of it
	        return true;
	    }



	}
	
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
		
//		buildings.add(new Building("Dodson Field",
//								"",
//								"Athletic Facilities",
//								getLocation(),
//								"description"));
//		buildings.add(new Building("Plaster Athletic Center",
//								"",
//								getLocation(),
//								"Athletic Facilities",
//								""));
//		buildings.add(new Building("Plaster Stadium",
//								"",
//								getLocation(37.602128,-93.41325),
//								"Athletic Facilities",
//								""));
//		buildings.add(new Building("Soccer Field",
//								"",
//								getLocation(37.598095,-93.410171),
//								"Athletic Facilities",
//								""));
//		buildings.add(new Building("Softball Field",
//								"",
//								getLocation(),
//								"Athletic Facilities",
//								""));
//		buildings.add(new Building("Tennis Courts",
//								"",
//								getLocation(),
//								"Athletic Facilities",
//								""));
//		
//		buildings.add(new Building("Mabee Chapel",
//								"200 W Anderson Dr" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Chapels",
//								""));
//		buildings.add(new Building("Randolph Chapel",
//								"",
//								getLocation(),
//								"Chapels",
//								""));
//		
//		buildings.add(new Building("Dining Commons",
//								"",
//								getLocation(),
//								"Dining Facilities",
//								""));
//		buildings.add(new Building("McClelland Dining Facility",
//								"1700 E Anderson Dr" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Dining Facilities",
//								""));
//		
//		buildings.add(new Building("Casebolt Fine Arts",
//								"1364 S Pike Ave" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Faculty Offices/Classroom",
//								""));
//		
//		buildings.add(new Building("Gott Education Center",
//								"138 W Estep Dr" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Faculty Offices/Classroom",
//								""));
//		
//		buildings.add(new Building("Jester Center",
//								"651 E Anderson St" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Faculty Offices/Classroom",
//								""));
//		
//		buildings.add(new Building("Jim Mellers Center",
//								"300 W Estep Dr" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Faculty Offices/Classroom",
//								""));
//		
//		buildings.add(new Building("Meyer Wellness Sports Center",
//								"1260 S Pike Ave" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Faculty Offices/Classroom",
//								""));
//		
//		buildings.add(new Building("Taylor Center",
//								"100 W Estep Dr" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Faculty Offices/Classroom",
//								""));
//		
//		buildings.add(new Building("Wheeler Science",
//								"301 E Anderson Dr" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Faculty Offices/Classroom",
//								""));
//		
//		buildings.add(new Building("University Library",
//								"",
//								getLocation(),
//								"Library",
//								""));
//		
//		buildings.add(new Building("Hammons Center",
//								"520 W. Aldrich Rd" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Maintenance Facilities",
//								""));
//		
//		buildings.add(new Building("Plaster Athletic Center",
//								"",
//								getLocation(),
//								"Other",
//								""));
//		
//		buildings.add(new Building("Student Union",
//								"1460 S Pike Ave" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Other",
//								""));
//		
//		buildings.add(new Building("Beasley Hall",
//								"234 W South St" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Casebolt Apartments",
//								"614 S Clark Ave" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Craig House",
//								"1314 S Lillian" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Gott Hall",
//								"105 E Aldrich Rd" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Landen Hall",
//								"1520 S Pike Ave" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Leslie Hall",
//								"1026 S Pike Ave" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Maupin Hall",
//								"235 W Austin St" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Memorial Hall",
//								"806 S Clark Ave" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Meyer Hall",
//								"1715 University Dr" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Plaster Lodge",
//								"1730 University Ave" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Roseman Apartments",
//								"1860 Maple Tree Ln" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		
//		buildings.add(new Building("Woody Hall",
//								"105 E Aldrich Rd" + "\n" + 
//								"Bolivar, MO 65613",
//								getLocation(),
//								"Residence Halls",
//								""));
//		//35 Buildings
		return buildings;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
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
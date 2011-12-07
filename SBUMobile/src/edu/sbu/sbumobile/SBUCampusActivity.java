package edu.sbu.sbumobile;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class SBUCampusActivity extends MapActivity implements LocationListener {

	private MapView mapView;
	private MyLocationOverlay myLocation;
	private MapController mapController;
	
	private LocationManager locationManager;
	
	private static final double GEO_CONVERT = 1E6;
	
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
		
		double lat = 37.604168;
		double lng = -93.413186;

		GeoPoint p = new GeoPoint(
		    (int) (lat * 1E6), 
		    (int) (lng * 1E6));

		mapController.animateTo(p);
		mapController.setZoom(16); 


	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	

}
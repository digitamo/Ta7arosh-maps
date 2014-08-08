package com.example.ta7arosh_maps;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	private GoogleMap map;
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private LocationRequest mLocationRequest;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		// .getMap();
		if (servicesConnected()) {
			Log.d("osama", "google play services is installed");
			mLocationClient = new LocationClient(this, this, this);
			createLocationRequest();

			// setUpMapIfNeeded();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Handle the result of the fragment by Google play services

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case FragmentActivity.RESULT_OK: {
				// try connecting again
				servicesConnected();
				break;
			}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// setUpMapIfNeeded();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
		Log.d("osama", "starting to connect");
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mLocationClient.isConnected())
			mLocationClient.removeLocationUpdates(this);
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d("osama", "on connected");
		// if (!mLocationClient.isConnecting()) {
		// setUpMapIfNeeded();
		// }

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		Log.d("osama", "Disconnected");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e("osama", "connection failed");
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException e) {
				Log.e("osama", "couldn't connect to server");
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			showErrorDialog(result.getErrorCode());
		}
	}

	private void createLocationRequest() {
		mLocationRequest = LocationRequest
				.create()
				.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS)
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setFastestInterval(
						LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			Log.e("osama", "couldn't connect!!");
			// Get the error code
			// int errorCode = ConnectionResult.getErrorCode(); // -- if there
			// is an error then the error code will be the resultCode

			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getSupportFragmentManager(),
						"Geofence Detection");
			}
			Toast.makeText(this, "no play services detected",
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	private void showErrorDialog(int errorCode) {
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
		if (errorDialog != null) {
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			errorFragment.setDialog(errorDialog);
			errorFragment.show(getSupportFragmentManager(),
					"Geofence Detection");
		}
	}

	private void setUpMapIfNeeded() {
		Log.d("osama", "setUpMapIfNeeded()");

		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (map == null) {
			Log.d("osama", "map was null");
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (map != null) {
				// The Map is verified. It is now safe to manipulate the map.

				map.addMarker(new MarkerOptions()
						.position(new LatLng(29.552891, 30.736613)).title("#1")
						.snippet("this is #1"));

				map.addMarker(new MarkerOptions()
						.position(new LatLng(29.624543, 31.621013))
						.title("#2")
						.snippet("this is #2")
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

				map.addMarker(new MarkerOptions()
						.position(
								new LatLng(mCurrentLocation.getLatitude(),
										mCurrentLocation.getLongitude()))
						.title("you're here")
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		setUpMapIfNeeded();
	}

}

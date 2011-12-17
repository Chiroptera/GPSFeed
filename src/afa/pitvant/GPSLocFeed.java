package afa.pitvant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class GPSLocFeed extends Activity implements OnClickListener,
		OnCheckedChangeListener, LocationListener {
	//definition of UI elements
	TextView lat, lon, ip, port;
	Button requestCoords;
	RadioGroup choices;
	
	//GPS elements
	LocationManager ourManager;
	Location currentLoc;
	
	//various working variables
	boolean liveFeed = false;
	int timeUpdt, distUpdt;
	
	//information to be sent to UDP thread
	float lati, longi;
	String TARGETIP;
	int TARGETPORT;
	
	//our preferences
	SharedPreferences prefs;
	
	//defining the UDP thread
	private UDPClient myThread;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//no title bar
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//initialization of UI and GPS elements and our preferences
		initialize();
		
		//initializing and starting the UDP thread
		myThread = new UDPClient(TARGETIP, TARGETPORT);
		myThread.start();
		
	}
	/**
	 * Initializes the application variables related to the user interface, location manager, preference manager and the target's IP and port.
	 * @param No parameters.
	 * @return Doesn't return anything.
	 * */
	private void initialize() {
		// TODO Auto-generated method stub
		lat = (TextView) findViewById(R.id.tvLat);
		lon = (TextView) findViewById(R.id.tvLong);
		ip = (TextView) findViewById(R.id.tvIP);
		port = (TextView) findViewById(R.id.tvPORT);
		requestCoords = (Button) findViewById(R.id.bRequestCoords);
		requestCoords.setOnClickListener(this);
		
		choices = (RadioGroup) findViewById(R.id.rgChoices);
		choices.setOnCheckedChangeListener(this);
		ourManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		TARGETPORT = Integer.parseInt(prefs.getString("serverPort", "0"));
		TARGETIP = prefs.getString("serverIP", "0.0.0.0");
		ip.setText("IP: " + TARGETIP);
		port.setText("Port: " + String.valueOf(TARGETPORT));
		
		timeUpdt = Integer.parseInt(prefs.getString("timeUpdt", "0"));
		distUpdt = Integer.parseInt(prefs.getString("distUpdt", "0"));
		ourManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeUpdt, distUpdt, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		
		new MenuInflater(this).inflate(R.menu.menu, menu);
		return true;		
	}	

	/* Preferences Selection */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub		
		switch(item.getItemId()){
		case R.id.howTo:
			startActivity(new Intent(this, HowTo.class));
			break;
		case R.id.preferences:
			startActivity(new Intent(this, Prefs.class));
			break;
		case R.id.exit:
			finish();
			break;
		}		
		return true;
	}

	/* Events for button press */
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		myThread.setCoords(longi, lati);
		
	}

	/* Operating Mode Options */
	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		switch (arg1) {
		case R.id.rLiveFeed:
			requestCoords.setVisibility(View.INVISIBLE); //makes the requestCoords button invisible during live feed
			liveFeed = true;
			break;
		case R.id.rPoint2P:
			requestCoords.setVisibility(View.VISIBLE); //make the requestCoords button visible during P2P
			liveFeed = false;
			break;
		}

	}

	/* Implemented GPS functions */
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub		
		lati = (float) arg0.getLatitude();
		longi = (float) arg0.getLongitude();
		lat.setText("Latitude: " + String.valueOf(lati));
		lon.setText("Longitude: " + String.valueOf(longi));
		Log.d("CoordsMain", String.valueOf(longi)+":"+String.valueOf(lati));
		
		if (liveFeed == true) {	
			myThread.setCoords(longi, lati);
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//updates the variables from preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		timeUpdt = Integer.parseInt(prefs.getString("timeUpdt", "0"));
		distUpdt = Integer.parseInt(prefs.getString("distUpdt", "0"));
		ourManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeUpdt, distUpdt, this);
		
		TARGETIP = prefs.getString("serverIP", "0.0.0.0");
		TARGETPORT = Integer.parseInt(prefs.getString("serverPort", "0"));
		ip.setText("IP: " + TARGETIP);
		port.setText("Port: " + String.valueOf(TARGETPORT));
		myThread.setAddr(TARGETIP, TARGETPORT);
		
		Log.d("Resume:IP", TARGETIP);
		Log.d("Resume:PORT", String.valueOf(TARGETPORT));		
	}
}

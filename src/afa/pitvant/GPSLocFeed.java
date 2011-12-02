package afa.pitvant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class GPSLocFeed extends Activity implements OnClickListener,
		OnCheckedChangeListener, LocationListener {
	//definition of UI elements
	TextView lat, lon;
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
	String SERVERIP;
	int SERVERPORT;
	
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
		
		//while (dialogParse()==false){}
		// requestCoords.setVisibility(1); //makes the button invisible at startup
		
		//initialization of UI and GPS elements and our preferences
		initialize();
		
		//initializing and starting the UDP thread
		myThread = new UDPClient(SERVERIP, SERVERPORT);
		myThread.start();
		
	}

	private void initialize() {
		// TODO Auto-generated method stub
		lat = (TextView) findViewById(R.id.tvLat);
		lon = (TextView) findViewById(R.id.tvLong);
		requestCoords = (Button) findViewById(R.id.bRequestCoords);
		requestCoords.setOnClickListener(this);
		choices = (RadioGroup) findViewById(R.id.rgChoices);
		choices.setOnCheckedChangeListener(this);
		ourManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SERVERPORT = Integer.parseInt(prefs.getString("serverPort", "0"));
		SERVERIP = prefs.getString("serverIP", "0.0.0.0");
	}
	
	//dialog funcion
/*
	private boolean dialogParse (){ //fun
		final AlertDialog.Builder alert = new  AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		alert.setTitle("Inset IP:Port");
		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				SERVERIP = input.getText().toString().trim();
				Toast.makeText(getApplicationContext(), SERVERIP,
						Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
		
		int i=SERVERIP.indexOf(":");
		if (i==-1) return false;
		String port = SERVERIP.substring(i, SERVERIP.length()-1);
		SERVERIP = SERVERIP.substring(0, i);
		Log.d("ServerIp", SERVERIP);
		Log.d("ServerPort", port);
		SERVERPORT = Integer.parseInt(port);
		return true;
	}
*/
	
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

	/* Events for buttton press */
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		// currentLoc = ourManager.getLastKnownLocation(LOCATION_SERVICE);
		if (liveFeed == false){			
			// prepare a message with GPS location data
            Message messageToThread = new Message();
            Bundle messageData = new Bundle();
            messageToThread.what = 0;
            messageData.putFloat("latitude", lati);
            messageData.putFloat("longitude", longi);
            messageToThread.setData(messageData);

            // sending message to myThread
            myThread.getHandler().sendMessage(messageToThread);
		}
	}

	/* Operating Mode Options */
	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		switch (arg1) {
		case R.id.rLiveFeed:
			// requestCoords.setVisibility(1); //makes the requestCoords button invisible during live feed
			liveFeed = true;
			break;
		case R.id.rPoint2P:
			// requestCoords.setVisibility(0); //make the requestCoords button visible during P2P
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
			//prefs = PreferenceManager.getDefaultSharedPreferences(this);
			//SERVERPORT = Integer.parseInt(prefs.getString("serverPort", "0"));
			//SERVERIP = prefs.getString("serverIp", "0.0.0.0");

			// prepare a message with GPS location data
            Message messageToThread = new Message();
            Bundle messageData = new Bundle();
            messageToThread.what = 0;
            messageData.putFloat("latitude", lati);
            messageData.putFloat("longitude", longi);
            messageData.putInt("serverPort", SERVERPORT);
            messageData.putString("serverIp", SERVERIP);
            messageToThread.setData(messageData);
 
            // sending message to myThread
            myThread.getHandler().sendMessage(messageToThread);
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
		
		//uses the time and distance from the preferences for the requestLocationUpdates method
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		timeUpdt = Integer.parseInt(prefs.getString("timeUpdt", "0"));
		distUpdt = Integer.parseInt(prefs.getString("distUpdt", "0"));		
		ourManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeUpdt, distUpdt, this);
	}
}
